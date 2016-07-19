import Authentication.{AuthFailure, AuthSuccess, AuthenticationResult}
import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.{Future => TwitterFuture}
import tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._

import Model._

import org.jboss.netty.handler.codec.http.HttpHeaders._

import JsonCodecs._



object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get(Routes.ListUsers :: header( Names.AUTHORIZATION)) {

      (jwt: String) => {
        val jwtPayload = Authentication.extractPayload(jwt)
        val users: TwitterFuture[Seq[Auth]] = Bijection[Future[Seq[Auth]], TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

        Await.result(AppEventDAO.logAdminListUsers(jwtPayload.userId), Duration.Inf)

        Ok(users.map(usrs => usrs.asJson.toString()))
      }
    }

    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => Ok("" + Authentication.verifyJWT(jwt))
    }

    val authenticate: Endpoint[String] = get(Routes.Authenticate :: string :: string) {
      (username: String, hash: String) => {
        Bijection[Future[AuthenticationResult], TwitterFuture[AuthenticationResult]](tables.AuthDAO.login(username, hash)).map {
          case AuthSuccess(jwt) => {
            Await.result(AppEventDAO.logUserLogin(true), Duration.Inf)

            Ok(jwt)
          }
          case AuthFailure => {
            Await.result(AppEventDAO.logUserLogin(false), Duration.Inf)

            Ok("No such user or incorrect password")
          }
        }
      }
    }

    val listEvents: Endpoint[String] = get(Routes.ListEvents) {
      val events: TwitterFuture[Seq[AppEvent]] = Bijection[Future[Seq[AppEvent]], TwitterFuture[Seq[AppEvent]]](tables.AppEventDAO.getAppEvents())

      Ok(events.map(events => events.asJson.toString()))
    }

    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq(Names.ACCEPT, Names.AUTHORIZATION, Names.ACCESS_CONTROL_ALLOW_CREDENTIALS))
    )

    val service     = (api :+: authenticate :+: verifyJWT :+: listEvents).toService
    val corsService = new Cors.HttpFilter(policy).andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
