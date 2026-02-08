package org.thp.misp.dto

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.util.Date

case class Event(
    id: String,
    published: Boolean,
    info: String,
    threatLevel: Option[Int], // from string
    analysis: Option[Int],    // from String
    date: Date,
    publishDate: Date,
    org: String,
    orgc: String,
    attributeCount: Option[Int],
    distribution: Int,
    attributes: Seq[Attribute],
    tags: Seq[Tag]
)

object Event {

  import java.text.SimpleDateFormat

  // Mikel - MISP 2.5 sends id and org_id as int instead of String
  val readStringFromInt: Reads[String] = implicitly[Reads[Int]]
    .map(x => x.toString)

  val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  implicit val reads: Reads[Event] =
    (((JsPath \ "id").read[String] or (JsPath \ "id").read[String](readStringFromInt)) and
      (JsPath \ "published").read[Boolean] and
      (JsPath \ "info").read[String] and
      ((JsPath \ "threat_level_id").readNullable[String].map(_.map(_.toInt)) or (JsPath \ "threat_level_id").readNullable[Int]) and
      ((JsPath \ "analysis").readNullable[String].map(_.map(_.toInt)) or (JsPath \ "analysis").readNullable[Int]) and
      (JsPath \ "date").read[String].map(simpleDateFormat.parse) and
      ((JsPath \ "publish_timestamp").read[String].map(t => new Date(t.toLong * 1000)) or (JsPath \ "publish_timestamp").read[Long].map(t => new Date(t * 1000))) and
      (JsPath \ "Org" \ "name").read[String] and
      (JsPath \ "Orgc" \ "name").read[String] and
      ((JsPath \ "attribute_count").readNullable[String].map(_.map(_.toInt)) or (JsPath \ "attribute_count").readNullable[Int]) and
      ((JsPath \ "distribution").read[String].map(_.toInt) or (JsPath \ "distribution").read[Int]) and
      (JsPath \ "Attribute").readWithDefault[Seq[Attribute]](Nil) and
      // Mikel - changed "attributes" to "Attribute"
      (JsPath \ "EventTag").read[Seq[JsObject]].map(_.map(eventTag => (eventTag \ "Tag").as[Tag])))(Event.apply _)
      
  implicit val writes: OWrites[Event] = OWrites[Event] { event =>
    Json.obj(
      "distribution"    -> event.distribution,
      "threat_level_id" -> event.threatLevel,
      "analysis"        -> event.analysis,
      "info"            -> event.info,
      "date"            -> simpleDateFormat.format(event.date),
      "published"       -> false,
      "Tag"             -> event.tags.map(t => JsString(t.name))
    )
  }
}
