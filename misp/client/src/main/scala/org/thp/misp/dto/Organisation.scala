package org.thp.misp.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

import java.util.UUID

case class Organisation(id: String, name: String, description: Option[String], uuid: UUID)

object Organisation {
  // Mikel - MISP 2.5 sends id as int instead of String
  val readStringFromInt: Reads[String] = implicitly[Reads[Int]]
    .map(x => x.toString)
  
  implicit val reads: Reads[Organisation] = 
    //Json.reads[Organisation]
    (((JsPath \ "id").read[String] or (JsPath \ "id").read[String](readStringFromInt)) and
      (JsPath \ "name").read[String] and 
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "uuid").read[UUID]
      )(Organisation.apply _)

}
