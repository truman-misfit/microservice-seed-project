package com.misfit.microservices.jobs

import play.api.Logger
import com.misfit.microservices.modules._

object PrintLogJob extends Consumer {
	override def registerService = "ms.backend.stream.log"
	override def onEvent(event: String) = {
		val output = "Received event from log stream: " + event
		Logger.info(output)
	}
}