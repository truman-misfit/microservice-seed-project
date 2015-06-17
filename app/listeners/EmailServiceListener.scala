package com.misfit.ms.listeners

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json._
import play.api.libs.mailer._

import javax.jms.Message
import javax.jms.TextMessage
import javax.jms.MessageListener

class EmailServiceListener extends MessageListener {
	def onMessage(message: Message) {
            message match {
			case text: TextMessage => {
				// Consumer message via ws
                val textMessage = text.getText()
                val textJSONMessage = Json.parse(textMessage)
                val subjectOfTextJSONMessageOpt = (textJSONMessage \ "Subject").asOpt[String]
                val detailOfTextJSONMessageOpt = (textJSONMessage \ "Message").asOpt[String]
                subjectOfTextJSONMessageOpt match {
                	case Some("email") => {
                		// sending emails request
                		detailOfTextJSONMessageOpt.foreach { mailContent =>
                			Logger.info("Send email : " + mailContent)
                		}	
                	}
                	case _ => {
                		// nothing matchs the current background service
                	}
                }
			}
			case _ => {
				throw new Exception("Unhandled message type: " + message.getClass.getSimpleName)
			}
		}
	}
}