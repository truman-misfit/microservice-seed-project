package com.misfit.microservices.modules

import java.nio.ByteBuffer

import scala.concurrent.Future
import scala.util.Try

trait ServicePublisher {
	def put(data: ByteBuffer)
}

trait ServicePublisherAbstract {
	def publish(service: String, content: String): Unit
	def shutdown(): Unit
}