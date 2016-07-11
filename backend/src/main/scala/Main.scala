import Authentication.{AuthFailure, AuthSuccess, AuthenticationResult}
import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.{Future => TwitterFuture}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._
import tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get("users") {
      val a = AppEventDAO.logEvent("127.0.0.1", 1, AppEventType.App, AppSection.Admin, AppAction.ListUsers, AppActionResult.ActionNormal, AppEventSeverity.Normal)

      Await.result(a, Duration.Inf)

      val users: TwitterFuture[Seq[Auth]] =  Bijection[Future[Seq[Auth]],TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

      Ok(users.map(usrs => usrs.asJson.toString()))
    }

    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => Ok("" + Authentication.verifyJWT(jwt))
    }

    val authenticate: Endpoint[String] = get("authenticate" :: string :: string) {
      (username: String, hash: String) => {
        Bijection[Future[AuthenticationResult], TwitterFuture[AuthenticationResult]](tables.AuthDAO.login(username, hash)).map {
          case AuthSuccess(jwt) => {
            val a = AppEventDAO.logEvent("127.0.0.1", 1, AppEventType.Auth, AppSection.Login, AppAction.UserLogin, AppActionResult.ActionSuccess, AppEventSeverity.Normal)

            Await.result(a, Duration.Inf)

            Ok(jwt)
          }
          case AuthFailure      => {
            val a = AppEventDAO.logEvent("127.0.0.1", 1, AppEventType.Auth, AppSection.Login, AppAction.UserLogin, AppActionResult.ActionFailure, AppEventSeverity.Minor)

            Await.result(a, Duration.Inf)

            Ok("No such user or incorrect password")
          }
        }
      }
    }

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

    implicit val appActionDecoder = enumDecoder(AppAction)
    implicit val appActionEncoder = enumEncoder(AppAction)

    implicit val appEventTypeDecoder = enumDecoder(AppEventType)
    implicit val appEventTypeEncoder = enumEncoder(AppEventType)

    implicit val appSectionDecoder = enumDecoder(AppSection)
    implicit val appSectionEncoder = enumEncoder(AppSection)

    implicit val appEventSeverityDecoder = enumDecoder(AppEventSeverity)
    implicit val appEventSeverityEncoder = enumEncoder(AppEventSeverity)

    implicit val appActionResultDecoder = enumDecoder(AppActionResult)
    implicit val appActionResultEncoder = enumEncoder(AppActionResult)

    implicit val AppActionDecoder = enumDecoder(AppAction)
    implicit val AppActionEncoder = enumEncoder(AppAction)

    implicit val appEventEncoder = new Encoder[AppEvent] {
      override def apply(event: AppEvent): Json = Encoder.encodeJsonObject{
        JsonObject.fromMap{
          Map(
            "Timestamp" -> Encoder.encodeString(event.timestamp.toString),
            "IPAddress" -> Encoder.encodeString(event.ipAddress),
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

    val listEvents: Endpoint[String] = get("events") {
      val events: TwitterFuture[Seq[AppEvent]] = Bijection[Future[Seq[AppEvent]], TwitterFuture[Seq[AppEvent]]](tables.AppEventDAO.getAppEvents())

      Ok(events.map(events => events.asJson.toString()))
    }

    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq("Accept", "Authorization"))
    )

    val service     = (api :+: authenticate :+: verifyJWT :+: listEvents).toService
    val corsService = new Cors.HttpFilter(policy).andThen(service) //.andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
