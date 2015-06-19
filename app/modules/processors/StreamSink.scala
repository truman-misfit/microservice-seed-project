package com.misfit.microservices.modules

import scala.concurrent.Future
import javax.inject._
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.google.inject.AbstractModule
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.{ Logger, Configuration, Environment }
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration

trait StreamSink {
	def process()
}

class FakeStreamSink @Inject()(lifecycle: ApplicationLifecycle) extends StreamSink {
	// release connections when app stop
	lifecycle.addStopHook { () =>
		Future.successful(None)
	}

	def process(): Unit = { }
}

class KinesisStreamSink @Inject()(lifecycle: ApplicationLifecycle) extends StreamSink {

	def WORKER_ID: String = UUID.randomUUID.toString

	private lazy val REGION_NAME = Play.current.configuration
									.getString("module.stream.sink.region")
									.getOrElse("us-east-1")

	private lazy val STREAM_NAME = Play.current.configuration
									.getString("module.stream.sink.name")
									.getOrElse("demo_stream")

	private lazy val APP_NAME = Play.current.configuration
									.getString("module.stream.sink.app.name")
									.getOrElse("demo_app")

	private lazy val kclConfig = {
		val config = new KinesisClientLibConfiguration(
									APP_NAME, STREAM_NAME, 
									new DefaultAWSCredentialsProviderChain(),
									WORKER_ID)
		config.withRegionName(Region.getRegion(Regions.US_EAST_1).getName())
		config.withIdleTimeBetweenReadsInMillis(100L)
		config
	}

	private lazy val kclProcessorFactory = new StreamProcessorFactory()

	private lazy val worker = new Worker(kclProcessorFactory, kclConfig)

	private lazy val threadPoolNumber = 5
	private lazy val pool: ExecutorService = Executors.newFixedThreadPool(threadPoolNumber)

	// release connections when app stop
	lifecycle.addStopHook { () =>
		Future.successful(None)
	}

	def process(): Unit = {
		try {
			pool.execute(worker)
			Logger.info("start processor.")
		} catch {
			case e: Exception => {
				pool.shutdown()
				Logger.info("Caught throwable while processing data.", e)
			}
		}
	}
}

class StreamSinkModule(
	environment: Environment,
	configuration: Configuration) extends AbstractModule {

	def configure() = {
		val isEnabledStreamSinkService: Boolean = 
				configuration.getBoolean("module.stream.sink.enabled").getOrElse(false)
		if (isEnabledStreamSinkService) {
			val modeStreamSinkService: Option[String] = 
					configuration.getString("module.stream.sink.mode", Some(Set("dev", "prod")))
			modeStreamSinkService match {
				case Some("dev") => {
					bind(classOf[StreamSink])
						.to(classOf[FakeStreamSink])
						.asEagerSingleton
					Logger.info("bind fake stream sink.")
				}
				case Some("prod") => {
					bind(classOf[StreamSink])
						.to(classOf[KinesisStreamSink])
						.asEagerSingleton
					Logger.info("bind Kinesis stream sink.")
				}
				case _ => {}
			}
		}
	}

}