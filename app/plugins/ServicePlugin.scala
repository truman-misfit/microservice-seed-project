package com.misfit.microservices.plugins

import javax.inject._
import play.api.{Plugin, Application, Logger}
import com.misfit.microservices.modules._

class ServicePlugin @Inject()(consumer: ServiceConsumer) extends Plugin {

	override def onStart() {
		consumer.process()
		Logger.info("Simple queue plugin has started.")
	}

	override def onStop() {
		Logger.info("Simple queue plugin has stopped.")
	}

}