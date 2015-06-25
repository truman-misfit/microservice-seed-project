package com.misfit.microservices.modules

trait ServiceConsumerAbstract {
	def register(service: String, consumer: String => Unit): Unit
	def start(): Unit
	def shutdown(): Unit
}