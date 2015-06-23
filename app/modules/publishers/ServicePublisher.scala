package com.misfit.microservices.modules

import java.nio.ByteBuffer

trait ServicePublisher {
	def put(data: ByteBuffer)
}