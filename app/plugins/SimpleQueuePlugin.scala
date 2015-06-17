package com.misfit.ms.plugins

import javax.inject._
import play.api.{Plugin, Application, Logger}
import com.misfit.ms.modules.queues._

class SimpleQueuePlugin @Inject()(queueService: QueueService) extends Plugin {

	override def onStart() {
		queueService.start
		Logger.info("Simple queue plugin has started.")
	}

	override def onStop() {
		Logger.info("Simple queue plugin has stopped.")
	}

}