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
    val deleteUser:Endpoint[String] = get(Routes.DeleteUser :: int::header(Names.AUTHORIZATION)) {
      (id: Int, jwt: String) => {

        var jwtPayload = Authentication.extractPayload(jwt)

        val result = Bijection[Future[Int], TwitterFuture[Int]](tables.AuthDAO.deleteUser(id))

        result.map{
          case 0 => {
            Await.result(AppEventDAO.logDeleteUser(jwtPayload.userId, false), Duration.Inf)
            InternalServerError(new Exception("Failed to delete user"))
          }

          case _ => {
            Await.result(AppEventDAO.logDeleteUser(jwtPayload.userId, true), Duration.Inf)
            Ok("")
          }
        }

      }
    }
    val api: Endpoint[String] = get(Routes.ListUsers :: header( Names.AUTHORIZATION)) {

      (jwt: String) => {
        val jwtPayload = Authentication.extractPayload(jwt)
        val users: TwitterFuture[Seq[Auth]] = Bijection[Future[Seq[Auth]], TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

        Await.result(AppEventDAO.logAdminListUsers(jwtPayload.userId), Duration.Inf)

        Ok(users.map(usrs => usrs.asJson(JsonCodecs.authSeqEncoder) .toString()))
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

    val clearEvents: Endpoint[String] = get(Routes.ClearEventLog :: header(Names.AUTHORIZATION)) {
      (jwt: String) => {
        val result = Bijection[Future[Int], TwitterFuture[Int]](tables.AppEventDAO.clearAppEvents())

        val jwtPayload = Authentication.extractPayload(jwt)
        val userId = jwtPayload.userId

        Await.ready(AppEventDAO.logAdminClearEventLog(userId), Duration.Inf)

        result.map((a: Int) => {
          Ok("")
        })
      }
    }

    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq(Names.ACCEPT, Names.AUTHORIZATION, Names.ACCESS_CONTROL_ALLOW_CREDENTIALS))
    )

    val service     = (api :+: authenticate :+: verifyJWT :+: listEvents :+: clearEvents :+: deleteUser).toService
    val corsService = new Cors.HttpFilter(policy).andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }


}
