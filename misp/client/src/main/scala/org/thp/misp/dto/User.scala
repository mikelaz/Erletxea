package org.thp.misp.dto

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class User(id: String, orgId: String, email: String)

object User {
  // Mikel - MISP 2.5 sends id and org_id as int instead of String
  val readStringFromInt: Reads[String] = implicitly[Reads[Int]]
    .map(x => x.toString)

  implicit val reads: Reads[User] =
    (((JsPath \ "id").read[String] or (JsPath \ "id").read[String](readStringFromInt)) and
      ((JsPath \ "org_id").read[String] or (JsPath \ "org_id").read[String](readStringFromInt)) and
      (JsPath \ "email").read[String])(User.apply _)
}
