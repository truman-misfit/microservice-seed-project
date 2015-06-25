package controllers

// swagger imports
import com.wordnik.swagger.annotations._

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.Results._
import play.api.libs.json._

import models._
import globals._

import javax.inject._
import javax.ws.rs.{QueryParam, PathParam}
import com.misfit.microservices.modules._

@Api(value = "/users", description = "Users micro-services")
class UserController extends Controller with UsersJSONTrait {

	@ApiOperation(
		nickname = "getUserById", 
		value = "Find a user by userid", 
		notes = "Returns a user", 
		httpMethod = "GET")
	@ApiResponses(Array(
		new ApiResponse(code = 200, message = "LGTM"),
	    new ApiResponse(code = 400, message = "Invalid ID supplied"),
	    new ApiResponse(code = 404, message = "User not found"),
	    new ApiResponse(code = 500, message = "Internal server error")
	))
	def getUser(
		@ApiParam(value = "id of the user to fetch") @PathParam("id") id: String) = Action {
		var retUserOpt = None: Option[User]

		if (retUserOpt.isDefined) {
			Ok(Json.toJson(retUserOpt.get))
		} else {
			InternalServerError
		}
	}

	@ApiOperation(
		nickname = "initUser",
		value = "Init a user",
		notes = "Init a user and return id",
		httpMethod = "PUT")
	@ApiResponses(Array(
		new ApiResponse(code = 200, message = "LGTM"),
		new ApiResponse(code = 400, message = "Invalid params"),
		new ApiResponse(code = 405, message = "User exists"),
		new ApiResponse(code = 500, message = "Internal server error")
	))
	@ApiImplicitParams(Array(
		new ApiImplicitParam (
			name = "email", value = "email of the user to init", 
			required = true, dataType = "String", paramType = "body"),
		new ApiImplicitParam (
			name = "sex", value = "sex of the user to init", 
			required = true, dataType = "String", paramType = "body"),
		new ApiImplicitParam (
			name = "passwd", value = "password the user input", 
			required = true, dataType = "String", paramType = "body")
	))
	def initUser = Action { request =>
		var retUserOpt = None: Option[User]

		if (retUserOpt.isDefined) {
			Ok(Json.toJson(retUserOpt.get))
		} else {
			InternalServerError
		}
	}
}