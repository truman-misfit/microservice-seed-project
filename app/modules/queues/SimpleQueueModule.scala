package com.misfit.ms.modules.queues

import javax.inject._
import com.google.inject.AbstractModule
import com.google.inject.name.Names

import play.api.Logger
import play.api.{ Configuration, Environment}

class SimpleQueueModule extends AbstractModule {

	// override DI configure method
	def configure() = {
		bind(classOf[QueueService])
			.to(classOf[SimpleQueueService])
			.asEagerSingleton
		Logger.info("Bind simple queue service.")
	}

}

class FakeQueueModule extends AbstractModule {

	// override DI configure method
	def configure() = {
		bind(classOf[QueueService])
			.to(classOf[FakeQueueService])
			.asEagerSingleton
		Logger.info("Bind fake queue service.")
	}

}

class QueueModule(
	environment: Environment,
	configuration: Configuration) extends AbstractModule {

	def configure() = {
		val isEnabledQueueService: Boolean = 
				configuration.getBoolean("module.queue.service.enabled").getOrElse(false)
		if (isEnabledQueueService) {
			val modeQueueService: Option[String] = 
				configuration.getString("module.queue.service.mode", Some(Set("dev", "prod")))
			modeQueueService match {
				case Some("dev") => {
					// dev mode bindings
					bind(classOf[QueueService])
						.to(classOf[FakeQueueService])
						.asEagerSingleton
					Logger.info("Bind fake queue service.")
				}
				case Some("prod") => {
					// prod mode bindings
					bind(classOf[QueueService])
						.to(classOf[SimpleQueueService])
						.asEagerSingleton
					Logger.info("Bind simple queue service.")
				}
				case _ => {}
			}
		}
	}

}