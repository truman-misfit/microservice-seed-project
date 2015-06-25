package features.steps

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.json._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout

import org.openqa.selenium._
import org.fluentlenium.core.filter.FilterConstructor._

import org.scalatest.Matchers
import cucumber.api.scala.{ ScalaDsl, EN }
import cucumber.api.DataTable
import cucumber.api.PendingException

class LoggingStepDefinitions extends ScalaDsl with EN with Matchers {

	implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
	val webDriverClass = Helpers.HTMLUNIT

	val app = FakeApplication()
	val port = 3333

	lazy val browser: TestBrowser = TestBrowser.of(webDriverClass, Some("http://localhost:" + port))
	lazy val server = TestServer(port, app)
	def driver = browser.getDriver()

	var requestAddress = s"http://localhost:$port"
	implicit val timeout = Timeout(5 seconds)

	Before() { s =>
		// init
		server.start()
	}

	After() { s =>
		// shut down
		server.stop()
		browser.quit()
	}

	Given("""^logging service is running$""") { () =>
		Logger.debug("Logging module is running")
	}

	When("""^I put a log request to "([^"]*)" path$""") { (path: String) =>
		val pathUrl = path match {
			case "log" => controllers.routes.Application.pushToMailStream.url
			case _ => throw new RuntimeException(s"unsupported page: $path")
		}
		// put request to the logging url
		requestAddress = s"http://localhost:$port" + pathUrl
		Logger.info("Logging request url: " + requestAddress)
	}

	Then("""^I should see http code ([^"]*) returned$""") {(expectedHttpCode: Int) =>
		// http code 200 is returned
		val response = Await.result(
			WS.url(requestAddress)
				.withRequestTimeout(10000)
				.get,
			timeout.duration
		).asInstanceOf[WSResponse]
		response.status should be (expectedHttpCode)
	}
}