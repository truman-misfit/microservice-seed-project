package com.misfit.microservices.modules

import javax.inject._
import java.nio.ByteBuffer
import scala.concurrent.Future
import com.google.inject.AbstractModule
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.{ Logger, Environment }

import com.amazonaws.ClientConfiguration
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.AmazonClientException
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.services.kinesis.model.DescribeStreamResult
import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.model.ResourceNotFoundException

class FakeMailServicePublisher @Inject()(lifecycle: ApplicationLifecycle) extends ServicePublisher {
	// release connections when app stop
	lifecycle.addStopHook { () =>
		Future.successful(None)
	}

	def put(data: ByteBuffer): Unit = { }
}

class KinesisMailServicePublisher @Inject()(lifecycle: ApplicationLifecycle) extends ServicePublisher {

	def TIMESTAMP: String = System.currentTimeMillis().toString

	private lazy val REGION_NAME = Play.current.configuration
									.getString("module.service.producer.region")
									.getOrElse("us-east-1")

	private lazy val STREAM_NAME = Play.current.configuration
    								.getString("module.service.producer.mail.name")
    								.getOrElse("mail-service-stream-dev")

	private lazy val configuration = {
		val config = new ClientConfiguration()
		config 
	}

	private lazy val kinesisClient: AmazonKinesis = {
		val client = new AmazonKinesisClient(
				new DefaultAWSCredentialsProviderChain(),
                configuration)
		client.setRegion(Region.getRegion(Regions.US_EAST_1))
		validateStream(client)
		client
	} 

	private def validateStream(kinesisClient: AmazonKinesis) = {
		try {
			val result = kinesisClient.describeStream(STREAM_NAME)
			if ("ACTIVE" != result.getStreamDescription().getStreamStatus()) {
				System.err.println("Stream " + STREAM_NAME + " is not active. Please wait a few moments and try again.")
                System.exit(1)
			}
		} catch {
			case e: ResourceNotFoundException => {
				System.err.println("Stream " + STREAM_NAME + " does not exist. Please create it in the console.")
	            System.err.println(e)
	            System.exit(1)
			}
			case e: Exception => {
				System.err.println("Error found while describing the stream " + STREAM_NAME)
	            System.err.println(e)
	            System.exit(1)
			}
		}
	}

	// release connections when app stop
	lifecycle.addStopHook { () =>
		Future.successful(None)
	}

	def put(data: ByteBuffer): Unit = {
		val putRecord: PutRecordRequest = new PutRecordRequest()
		putRecord.setStreamName(STREAM_NAME)
		putRecord.setPartitionKey(TIMESTAMP)
		putRecord.setData(data)
		try {
			kinesisClient.putRecord(putRecord)
		} catch {
			case ex: AmazonClientException => {
				Logger.warn("Error sending record to Amazon Kinesis.", ex)
			}			
		}
	}
}

class MailServicePublisherModule(
	Environment: Environment,
	configuration: play.api.Configuration) extends AbstractModule {

	def configure() = {
		val isEnabledStreamSourceService: Boolean =
				configuration.getBoolean("module.service.producer.mail.enabled").getOrElse(false)
		if (isEnabledStreamSourceService) {
			val modeStreamSourceService: Option[String] = 
					configuration.getString("module.service.producer.mail.mode", Some(Set("dev", "prod")))
			modeStreamSourceService match {
				case Some("dev") => {
					bind(classOf[ServicePublisher])
						.to(classOf[FakeMailServicePublisher])
						.asEagerSingleton
					Logger.info("bind fake stream source.")
				}
				case Some("prod") => {
					bind(classOf[ServicePublisher])
						.to(classOf[KinesisMailServicePublisher])
						.asEagerSingleton
					Logger.info("bind Kinesis stream source.")
				}
				case _ => {}
			}
		}
	}
}