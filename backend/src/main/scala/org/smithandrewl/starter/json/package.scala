package org.smithandrewl.starter

import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json, JsonObject}
import org.smithandrewl.starter.model._

import scala.util.Try

/**
  * Mappings between json and model classes
  */
package object json {
  /**
    * Mappings between json and model classes
    */
  object JsonCodecs {

    ////////// https://github.com/travisbrown/circe/pull/325/files /////////////////////////////////

    /**
      * Utility method which decodes a json object into an enumeration type.
      *
      * @param enum The enumeration object
      * @tparam E The type of the enumeration object
      * @return The enumeration representation of the JSON object
      */
    def enumDecoder[E <: Enumeration](enum: E): Decoder[E#Value] =
    Decoder.decodeString.flatMap { str =>
      Decoder.instanceTry { _ =>
        Try(enum.withName(str))
      }
    }

    /**
      * Utility method which encodes an enumeration type into a JSON object.
      *
      * @param enum The enumeration object
      * @tparam E The type of the enumeration object
      * @return The JSON representation of the enumeration
      */
    def enumEncoder[E <: Enumeration](enum: E): Encoder[E#Value] = new Encoder[E#Value] {
      override def apply(e: E#Value): Json = Encoder.encodeString(e.toString)
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
      * JSON encoder for AppAction types.
      */
    implicit val appActionEncoder = enumEncoder(AppAction)

    /**
      * JSON encoder for AppEventType types.
      */
    implicit val appEventTypeEncoder = enumEncoder(AppEventType)

    /**
      * JSON encoder for AppSection types.
      */
    implicit val appSectionEncoder = enumEncoder(AppSection)

    /**
      * JSON encoder for AppEventSeverity types.
      */
    implicit val appEventSeverityEncoder = enumEncoder(AppEventSeverity)

    /**
      * JSON encoder for AppActionResult types.
      */
    implicit val appActionResultEncoder = enumEncoder(AppActionResult)

    /**
      * Converts an [[org.smithandrewl.starter.model.Auth Auth]] instance to a Json object.
      */
    implicit val authEncoder = new Encoder[Auth] {
      override def apply(a: Auth): Json = {
        Encoder.encodeJsonObject {
          JsonObject.fromMap(Map(
            "userId" -> a.userId.asJson,
            "username" -> a.username.asJson,
            "isAdmin" -> a.isAdmin.asJson
          ))
        }
      }
    }

    /**
      * Converts a Seq[ [[org.smithandrewl.starter.model.Auth Auth]] ] instance to a Json object.
      */
    implicit val authSeqEncoder = new Encoder[Seq[Auth]] {
      override def apply(a: Seq[Auth]): Json = {
        Json.fromValues(a.map(_.asJson))
      }
    }

    /**
      * Converts a [[org.smithandrewl.starter.model.JwtPayload JwtPayload]] instance to a Json object.
      */
    implicit val jwtPayloadtEncoder = new Encoder[JwtPayload] {
      override def apply(jwtPayload: JwtPayload): Json = Encoder.encodeJsonObject {
        JsonObject.fromMap(Map(
          "userId" -> jwtPayload.userId.asJson,
          "isAdmin" -> jwtPayload.isAdmin.asJson
        ))
      }
    }

    /**
      * Converts an [[org.smithandrewl.starter.model.AppEvent AppEvent]] instance to a Json object.
      */
    implicit val appEventEncoder = new Encoder[AppEvent] {
      override def apply(event: AppEvent): Json = Encoder.encodeJsonObject {
        JsonObject.fromMap {
          Map(
            "Timestamp" -> Encoder.encodeString(event.timestamp.toString),
            "User" -> Encoder.encodeInt(event.userId),
            "Type" -> appEventTypeEncoder(event.appEventType),
            "Section" -> appSectionEncoder(event.appSection),
            "Result" -> appActionResultEncoder(event.appActionResult),
            "Severity" -> appEventSeverityEncoder(event.appEventSeverity),
            "Action" -> appActionEncoder(event.appAction)
          )
        }
      }
    }

    /**
      * Converts a Seq[ [[org.smithandrewl.starter.model.AppEvent AppEvent]] ] to a Json object.
      */
    implicit val appEventSeqEncoder = new Encoder[Seq[AppEvent]] {
      override def apply(a: Seq[AppEvent]): Json = {
        Json.fromValues(a.map(_.asJson))
      }
    }
  }

}
