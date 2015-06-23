package controllers

import java.nio.ByteBuffer

// swagger imports
import com.wordnik.swagger.annotations._

import javax.inject._
import play.api._
import play.api.mvc._

import com.misfit.microservices.modules._

@Api(value = "/log", description = "Stream collection services")
class LoggingController @Inject()(publisher: ServicePublisher) extends Controller {

	@ApiOperation(
		nickname = "stream data collection",
		value = "put a record into stream",
		notes = "returns status of stream collection",
		httpMethod = "PUT")
	@ApiResponses(Array(
		new ApiResponse(code = 200, message = "LGTM"),
		new ApiResponse(code = 500, message = "Internal server error")
	))
	@ApiImplicitParams(Array(
		new ApiImplicitParam (
			name = "recordContent", value = "record string content",
			required = true, dataType = "JsObject", paramType = "body")
	))
	def putRecords = Action { request =>
		val reqJSON = request.body.asJson.get
		val recordType = (reqJSON \ "recordType").asOpt[String]
		val recordContent = (reqJSON \ "recordContent").asOpt[String]

		val reqJSONString = reqJSON.toString
		val reqJSONByteBuffer = ByteBuffer.wrap(reqJSONString.getBytes)

		recordType match {
			case Some("email") => {
				publisher.put(reqJSONByteBuffer)
			}
			case Some("notification") => {
				publisher.put(reqJSONByteBuffer)
			}
			case Some("auth") => {
				publisher.put(reqJSONByteBuffer)
			}
			case _ => {}
		}	
		Ok(reqJSON)
	}

}