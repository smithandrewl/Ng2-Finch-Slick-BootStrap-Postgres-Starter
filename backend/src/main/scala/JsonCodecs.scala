import Model.{AppEvent, JwtPayload, _}

import io.circe.{Encoder, Json, JsonObject}
import io.circe.Decoder
import io.circe.syntax._

import scala.util.Try

object JsonCodecs {

  ////////// https://github.com/travisbrown/circe/pull/325/files /////////////////////////////////
  def enumDecoder[E <: Enumeration](enum: E): Decoder[E#Value] =
    Decoder.decodeString.flatMap { str =>
      Decoder.instanceTry { _ =>
        Try(enum.withName(str))
      }
    }

  def enumEncoder[E <: Enumeration](enum: E): Encoder[E#Value] = new Encoder[E#Value] {
    override def apply(e: E#Value): Json = Encoder.encodeString(e.toString)
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////

  implicit val appActionEncoder        = enumEncoder(AppAction)
  implicit val appEventTypeEncoder     = enumEncoder(AppEventType)
  implicit val appSectionEncoder       = enumEncoder(AppSection)
  implicit val appEventSeverityEncoder = enumEncoder(AppEventSeverity)
  implicit val appActionResultEncoder  = enumEncoder(AppActionResult)
  implicit val AppActionEncoder        = enumEncoder(AppAction)

  implicit val jwtPayloadtEncoder = new Encoder[JwtPayload] {
    override def apply(jwtPayload: JwtPayload): Json = Encoder.encodeJsonObject {
      JsonObject.fromMap(Map(
        "userId" -> jwtPayload.userId.asJson,
        "isAdmin" -> jwtPayload.isAdmin.asJson
      ))
    }
  }

  implicit val appEventEncoder = new Encoder[AppEvent] {
    override def apply(event: AppEvent): Json = Encoder.encodeJsonObject{
      JsonObject.fromMap{
        Map(
          "Timestamp" -> Encoder.encodeString(event.timestamp.toString),
          "User"      -> Encoder.encodeInt(event.userId),
          "Type"      -> appEventTypeEncoder(event.appEventType),
          "Section"   -> appSectionEncoder(event.appSection),
          "Result"    -> appActionResultEncoder(event.appActionResult),
          "Severity"  -> appEventSeverityEncoder(event.appEventSeverity),
          "Action"    -> appActionEncoder(event.appAction)
        )
      }
    }
  }
}
