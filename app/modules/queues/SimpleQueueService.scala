package com.misfit.ms.modules.queues

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazon.sqs.javamessaging.SQSConnection
import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper

import javax.jms.Queue
import javax.jms.Session
import javax.jms.Message
import javax.jms.TextMessage
import javax.jms.MessageConsumer
import javax.jms.MessageListener

import scala.concurrent.Future
import javax.inject._
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.Play.current

trait QueueService {

	def start()

	// set the message listener for sqs service via
	// java jms compatible library
	def setMessageListener(
		messageListner: MessageListener): Boolean
}

class FakeQueueService @Inject()(liftcycle: ApplicationLifecycle) extends QueueService {
	// init local elastic server to simulate the fake SQS service

	def start() = {}

	def setMessageListener(
		messageListner: MessageListener): Boolean = {
		true
	}

	// release connections when app stop
	liftcycle.addStopHook { () =>
		Future.successful(None)
	}
}

class SimpleQueueService @Inject()(liftcycle: ApplicationLifecycle) extends QueueService {
	// connect to the real SQS service

	private lazy val connectionFactory: SQSConnectionFactory = 
			SQSConnectionFactory.builder()
				.withRegion(Region.getRegion(Regions.US_EAST_1))
				.withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
				.build()

	private lazy val connection: SQSConnection = connectionFactory.createConnection()
	private lazy val client: AmazonSQSMessagingClientWrapper = connection.getWrappedAmazonSQSClient()
	// Create a session
    private lazy val session: Session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    // Create a queue
    private lazy val destination: Queue = {
    	val queueName = Play.current.configuration
    						.getString("module.queue.service.name")
    						.getOrElse("micro_service_demo_queue")
    	if (!client.queueExists(queueName)) {
    		client.createQueue(queueName)
    	}
    	session.createQueue(queueName)
    }
    // Create a consumer
    private lazy val consumer: MessageConsumer = {
    	val initConsumer = session.createConsumer(destination)
    	val listenerClassName = Play.current.configuration.getString("module.queue.service.listener")
    	listenerClassName.foreach { name =>
    		val listenerClass: Class[_ <: MessageListener] = 
    				Play.classloader.loadClass(name).asSubclass(classOf[MessageListener])
    		initConsumer.setMessageListener(listenerClass.newInstance())
    		Logger.info("consumer start")
    		connection.start()
    	}    	
    	initConsumer
    }

    def start() = {
    	consumer
    }

	def setMessageListener(
		messageListner: MessageListener): Boolean = {
		consumer.setMessageListener(messageListner)
		connection.start()
		true
	}

	// release connections when app stop
	liftcycle.addStopHook { () =>
		Future.successful({
			session.close()
			connection.close()
		})
	}
}