package com.misfit.microservices.jobs

import play.api.Logger
import com.misfit.microservices.modules._

object PrintMailJob extends Consumer {
	override def registerService = "ms.backend.stream.mail"
	override def onEvent(event: String) = {
		val output = "Received event from mail stream: " + event
		Logger.info(output)
	}
}