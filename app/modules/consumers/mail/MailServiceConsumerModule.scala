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

class FakeMailServiceConsumer @Inject()(lifecycle: ApplicationLifecycle) extends ServiceConsumer {
	// release connections when app stop
	lifecycle.addStopHook { () =>
		Future.successful(None)
	}

	def process(): Unit = { }
}

class KinesisMailServiceConsumer @Inject()(lifecycle: ApplicationLifecycle) extends ServiceConsumer {

	def WORKER_ID: String = UUID.randomUUID.toString

	private lazy val REGION_NAME = Play.current.configuration
									.getString("module.service.consumer.region")
									.getOrElse("us-east-1")

	private lazy val STREAM_NAME = Play.current.configuration
									.getString("module.service.consumer.mail.name")
									.getOrElse("mail-service-stream-dev")

	private lazy val APP_NAME = Play.current.configuration
									.getString("module.service.consumer.mail.app")
									.getOrElse("mail-service-stream-dev")

	private lazy val kclConfig = {
		val config = new KinesisClientLibConfiguration(
									APP_NAME, STREAM_NAME, 
									new DefaultAWSCredentialsProviderChain(),
									WORKER_ID)
		config.withRegionName(Region.getRegion(Regions.US_EAST_1).getName())
		config.withIdleTimeBetweenReadsInMillis(100L)
		config
	}

	private lazy val kclProcessorFactory = new MailServiceConsumerFactory()

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

class MailServiceConsumerModule(
	environment: Environment,
	configuration: Configuration) extends AbstractModule {

	def configure() = {
		val isEnabledStreamSinkService: Boolean = 
				configuration.getBoolean("module.service.consumer.mail.enabled").getOrElse(false)
		if (isEnabledStreamSinkService) {
			val modeStreamSinkService: Option[String] = 
					configuration.getString("module.service.consumer.mail.mode", Some(Set("dev", "prod")))
			modeStreamSinkService match {
				case Some("dev") => {
					bind(classOf[ServiceConsumer])
						.to(classOf[FakeMailServiceConsumer])
						.asEagerSingleton
					Logger.info("bind fake stream sink.")
				}
				case Some("prod") => {
					bind(classOf[ServiceConsumer])
						.to(classOf[KinesisMailServiceConsumer])
						.asEagerSingleton
					Logger.info("bind Kinesis stream sink.")
				}
				case _ => {}
			}
		}
	}

}