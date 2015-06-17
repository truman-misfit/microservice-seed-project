package com.misfit.ms.modules.topics

import javax.inject._
import com.google.inject.AbstractModule
import com.google.inject.name.Names

import play.api.Logger
import play.api.{ Configuration, Environment }

class SimpleTopicModule extends AbstractModule {

	// override DI configure method
	def configure() = {
		bind(classOf[TopicService])
			.to(classOf[SimpleTopicService])
			.asEagerSingleton
		Logger.info("Bind simple topic service.")
	}

}

class FakeTopicModule extends AbstractModule {

	// override DI configure method
	def configure() = {
		bind(classOf[TopicService])
			.to(classOf[FakeTopicService])
			.asEagerSingleton
		Logger.info("Bind fake topic service.")
	}

}

class TopicModule(
	environment: Environment,
	configuration: Configuration) extends AbstractModule {

	def configure() = {
		val isEnabledTopicService: Boolean = 
				configuration.getBoolean("module.topic.service.enabled").getOrElse(false)
		if (isEnabledTopicService) {
			val modeTopicService: Option[String] = 
				configuration.getString("module.topic.service.mode", Some(Set("dev", "prod")))
			modeTopicService match {
				case Some("dev") => {
					// dev mode bindings
					bind(classOf[TopicService])
						.to(classOf[FakeTopicService])
						.asEagerSingleton
					Logger.info("Bind fake topic service.")
				}
				case Some("prod") => {
					// prod mode bindings
					bind(classOf[TopicService])
						.to(classOf[SimpleTopicService])
						.asEagerSingleton
					Logger.info("Bind simple topic service.")
				}
				case _ => {}
			}
		}
	}

}