package com.misfit.microservices.modules

import java.nio.ByteBuffer

trait ServicePublisherAbstract {
	def publish(service: String, content: String): Unit
	def shutdown(): Unit
}