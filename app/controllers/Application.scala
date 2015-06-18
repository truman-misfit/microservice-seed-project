package controllers

import java.nio.ByteBuffer
import javax.inject._
import play.api._
import play.api.mvc._
import com.misfit.microservices.modules._

class Application @Inject()(streamSource: StreamSource) extends Controller {

	def swagger = Action { request =>
		Ok(views.html.swagger())
	}

	def kinesisPut(input: String) = Action {
		val msg = ByteBuffer.wrap(input.getBytes)
		streamSource.put(msg)
		Ok
	}

}