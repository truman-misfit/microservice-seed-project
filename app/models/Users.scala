package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class User(
	var name: Option[String] = None,
	var email: Option[String] = None,
	var tombstone: Option[Int] = Option(0),
	var updtime: Option[Long] = Option(System.currentTimeMillis()/1000L),
	var inittime: Option[Long] = Option(System.currentTimeMillis()/1000L),
	var uuid: Option[String] = None)

trait UsersJSONTrait {
	implicit val UserFormat = Json.format[User]	
}