package org.thp.misp.dto

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Tag(
    id: Option[String],
    name: String,
    colour: Option[String],
    exportable: Option[Boolean]
)

object Tag {

  // Mikel - MISP 2.5 sends id and org_id as int instead of String
  val readStringFromInt: Reads[String] = implicitly[Reads[Int]]
    .map(x => x.toString)

  implicit val reads: Reads[Tag] =
    (((JsPath \ "id").readNullable[String] or (JsPath \ "id").readNullable[String](readStringFromInt)) and
      (JsPath \ "name").read[String] and
      (JsPath \ "colour").readNullable[String] and
      (JsPath \ "exportable").readNullable[Boolean])(Tag.apply _)

  implicit val writes: Writes[Tag] = Json.writes[Tag]
}
