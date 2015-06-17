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

import org.elasticmq.rest.sqs._

import com.misfit.ms.modules.queues._

object Global extends GlobalSettings {

    var server: SQSRestServer = null

    override def onStart(app: Application) {
        ////////////////////////////
        // startup local elasticmq
        // server = SQSRestServerBuilder.withPort(9325).withInterface("localhost").start()
        Logger.info("Whipper-App service has started.")
    }

    override def onStop(app: Application) {
        ///////////////////////////
        // stop the local elasticmq
        // server.stopAndWait()
        
        Logger.info("Whipper-App service has stopped.")
    }
}