import Authentication.{AuthFailure, AuthSuccess, AuthenticationResult}
import Model.JwtPayload
import cats.data.Xor
import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.{Future => TwitterFuture}
import org.jose4j.jws.JsonWebSignature
import tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import io.circe.jawn._

import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._

import Model._
import JsonCodecs._

object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get("users" :: header("Authorization")) {

      (jwt: String) => {

        val jwtPayload = Authentication.extractPayload(jwt)

        Await.result(
          AppEventDAO.logEvent(
            "127.0.0.1",
            jwtPayload.userId,
            AppEventType.App,
            AppSection.Admin,
            AppAction.ListUsers,
            AppActionResult.ActionNormal,
            AppEventSeverity.Normal
          ),
          Duration.Inf
        )

        val users: TwitterFuture[Seq[Auth]] = Bijection[Future[Seq[Auth]], TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

        Ok(users.map(usrs => usrs.asJson.toString()))

        }
    }

    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => Ok("" + Authentication.verifyJWT(jwt))
    }

    val authenticate: Endpoint[String] = get("authenticate" :: string :: string) {
      (username: String, hash: String) => {
        Bijection[Future[AuthenticationResult], TwitterFuture[AuthenticationResult]](tables.AuthDAO.login(username, hash)).map {
          case AuthSuccess(jwt) => {
            Await.result(
              AppEventDAO.logEvent(
                "127.0.0.1",
                1,
                AppEventType.Auth,
                AppSection.Login,
                AppAction.UserLogin,
                AppActionResult.ActionSuccess,
                AppEventSeverity.Normal
              ),
              Duration.Inf
            )

            Ok(jwt)
          }
          case AuthFailure => {
            Await.result(
              AppEventDAO.logEvent(
                "127.0.0.1",
                1,
                AppEventType.Auth,
                AppSection.Login,
                AppAction.UserLogin,
                AppActionResult.ActionFailure,
                AppEventSeverity.Minor
              ),
              Duration.Inf)

            Ok("No such user or incorrect password")
          }
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
      allowsHeaders = _ => Some(Seq("Accept", "Authorization", "Access-Control-Allow-Origin"))
    )

    val service     = (api :+: authenticate :+: verifyJWT :+: listEvents).toService
    val corsService = new Cors.HttpFilter(policy).andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
