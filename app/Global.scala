package globals

import play.api._
import play.api.mvc._

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.model.CreateTopicResult

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazon.sqs.javamessaging.SQSConnection
import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper
import javax.jms.Queue
import javax.jms.Session
import javax.jms.Message
import javax.jms.TextMessage
import javax.jms.MessageConsumer
import javax.jms.MessageListener

object Global extends GlobalSettings {

    override def onStart(app: Application) {
        Logger.info("Whipper-App service has started.")
    }

    override def onStop(app: Application) {
        Logger.info("Whipper-App service has stopped.")
    }
}