package com.misfit.microservices.plugins

import javax.inject._
import play.api.{Plugin, Application, Logger}
import com.misfit.microservices.modules._

class ServicePlugin @Inject()(consumer: ServiceConsumerAbstract) extends Plugin {

	override def onStart() {

		def onEvent(event: String) = print(s"Received event: $event")
		// register all the actions before application has started
		consumer.register("ms.backend.stream.mail", onEvent)
		consumer.register("ms.backend.stream.log", onEvent)

		// start all registered consumers
		consumer.start()
		Logger.info("Simple queue plugin has started.")
	}

	override def onStop() {
		Logger.info("Simple queue plugin has stopped.")
	}

}