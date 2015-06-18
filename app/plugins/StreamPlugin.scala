package com.misfit.microservices.plugins

import javax.inject._
import play.api.{Plugin, Application, Logger}
import com.misfit.microservices.modules._

class StreamPlugin @Inject()(streamSink: StreamSink) extends Plugin {

	override def onStart() {
		streamSink.process()
		Logger.info("Simple queue plugin has started.")
	}

	override def onStop() {
		Logger.info("Simple queue plugin has stopped.")
	}

}