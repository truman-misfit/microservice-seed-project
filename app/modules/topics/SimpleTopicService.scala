package com.misfit.ms.modules.topics

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.model.CreateTopicResult

import scala.concurrent.Future
import javax.inject._
import play.api.inject.ApplicationLifecycle

trait TopicService {
	def publish(subject: String, message: String): Unit
}

class FakeTopicService @Inject()(liftcycle: ApplicationLifecycle) extends TopicService {

	// release connections when app stop
	liftcycle.addStopHook { () =>
		Future.successful(None)
	}

	def publish(subject: String, message: String): Unit = {
		// fake publish
	}
}

class SimpleTopicService @Inject()(liftcycle: ApplicationLifecycle) extends TopicService {

	private lazy val snsClient: AmazonSNSClient = {
		val client = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain())
		client.setRegion(Region.getRegion(Regions.US_EAST_1))
		client
	}

	private lazy val initTopicRequest: CreateTopicRequest = new CreateTopicRequest("micro_service_demo_topic")

	// create topic if not exists
	private lazy val initTopicResult: CreateTopicResult = snsClient.createTopic(initTopicRequest)

	private lazy val arnTopic: String = initTopicResult.getTopicArn()

	// release connections when app stop
	liftcycle.addStopHook { () =>
		Future.successful(None)
	}

	def publish(subject: String, message: String): Unit = {
		snsClient.publish(arnTopic, message, subject)
	}
}