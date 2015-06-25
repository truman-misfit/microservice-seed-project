package com.misfit.microservices.plugins

import javax.inject._
import play.api.{Plugin, Application, Logger}
import com.misfit.microservices.modules._
import com.misfit.microservices.jobs._

class ServicePlugin @Inject()(consumer: ServiceConsumerAbstract) extends Plugin {

	override def onStart() {

		// register all the actions before application has started
		// consumer.register("ms.backend.stream.mail", onEvent)
		// consumer.register("ms.backend.stream.log", onEvent)

		consumer.register(
			PrintLogJob.registerService, 
			PrintLogJob.onEvent)
		consumer.register(
			PrintMailJob.registerService,
			PrintMailJob.onEvent)

		// start all registered consumers
		consumer.start()
		Logger.info("Simple queue plugin has started.")
	}

	override def onStop() {
		Logger.info("Simple queue plugin has stopped.")
	}

}