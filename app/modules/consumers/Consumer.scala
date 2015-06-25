package com.misfit.microservices.modules

trait Consumer {
	def registerService: String
	def onEvent(event: String): Unit
}