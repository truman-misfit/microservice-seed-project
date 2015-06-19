package controllers

import javax.inject._
import play.api._
import play.api.mvc._

class Application extends Controller {

	def swagger = Action { request =>
		Ok(views.html.swagger())
	}

}