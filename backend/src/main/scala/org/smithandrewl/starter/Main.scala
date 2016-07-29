package org.smithandrewl.starter

import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.{Future => TwitterFuture}
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._
import org.jboss.netty.handler.codec.http.HttpHeaders._
import org.smithandrewl.starter.auth.{AuthFailure, AuthSuccess, AuthenticationResult}
import org.smithandrewl.starter.db.dao.{AppEventDAO, AuthDAO}
import org.smithandrewl.starter.filter.AuthenticationFilter
import org.smithandrewl.starter.json._
import org.smithandrewl.starter.model.{AppEvent, Auth}
import org.smithandrewl.starter.util.Routes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The REST API server.
  */
object Main extends TwitterServer  {
  /**
    * The main method.
    */
  def main() {

    /**
      * The finch endpoint handling user deletion.
      *
      * Expects a user id parameter and an authorization header.
      */
    val deleteUser:Endpoint[String] = get(Routes.DeleteUser :: int::header(Names.AUTHORIZATION)) {
      (id: Int, jwt: String) => {

        var jwtPayload = auth.extractPayload(jwt)

        val result = Bijection[Future[Int], TwitterFuture[Int]](AuthDAO.deleteUser(id))

        result.map{
          case 0 => {

            Await.result(AppEventDAO.logDeleteUser(jwtPayload.userId, false), Duration.Inf)
            val cause: Exception = new Exception("Failed to delete user")

            log.warning(cause, s"User with UID = ${jwtPayload.userId} Failed to delete user with UID = $id")

            InternalServerError(cause)
          }

          case _ => {
            Await.result(AppEventDAO.logDeleteUser(jwtPayload.userId, true), Duration.Inf)

            log.debug(s"User with UID = ${jwtPayload.userId} deleted user with UID = $id")

            Ok("")

          }
        }
      }
    }

    /**
      * The finch endpoint for listing user accounts.
      *
      * Expects an authorization header.
      *
      */
    val api: Endpoint[String] = get(Routes.ListUsers :: header( Names.AUTHORIZATION)) {

      (jwt: String) => {
        val jwtPayload = auth.extractPayload(jwt)
        val users: TwitterFuture[Seq[Auth]] = Bijection[Future[Seq[Auth]], TwitterFuture[Seq[Auth]]](AuthDAO.getUsers())

        Await.result(AppEventDAO.logAdminListUsers(jwtPayload.userId), Duration.Inf)

        log.debug(s"User with UID = ${jwtPayload.userId} listed the users")

        Ok(users.map(usrs => usrs.asJson(JsonCodecs.authSeqEncoder) .toString()))
      }
    }

    /**
      * A finch endpoint for verifying jwt tokens.
      *
      * Expects the compact form of a jwt token.
      */
    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => Ok("" + auth.verifyJWT(jwt))
    }

    /**
      * The finch endpoint which handler user logins.
      *
      * Expects a username and password.
      */
    val authenticate: Endpoint[String] = get(Routes.Authenticate :: string :: string) {
      (username: String, hash: String) => {
        Bijection[Future[AuthenticationResult], TwitterFuture[AuthenticationResult]](AuthDAO.login(username, hash)).map {
          case AuthSuccess(jwt) => {
            val jwtPayload = auth.extractPayload(jwt)

            Await.result(AppEventDAO.logUserLogin(true), Duration.Inf)

            log.debug(s"User $username with UID = ${jwtPayload.userId} just logged in")

            Ok(jwt)
          }
          case AuthFailure => {
            Await.result(AppEventDAO.logUserLogin(false), Duration.Inf)

            log.debug("User login failed")

            Ok("No such user or incorrect password")
          }
        }
      }
    }

    /**
      * The finch endpoint for listing events from the event log.
      *
      * Expects an authorization header.
      */
    val listEvents: Endpoint[String] = get(Routes.ListEvents :: header(Names.AUTHORIZATION)) {

      (jwt: String) => {

        val jwtPayload = auth.extractPayload(jwt)
        val userId = jwtPayload.userId

        val events: TwitterFuture[Seq[AppEvent]] = Bijection[Future[Seq[AppEvent]], TwitterFuture[Seq[AppEvent]]](AppEventDAO.getAppEvents())

        log.debug(s"User with UID = $userId listed events")

        Ok(events.map(events => events.asJson(org.smithandrewl.starter.json.JsonCodecs.appEventSeqEncoder).toString()))
      }
    }

    /**
      * The finch endpoint for clearing the event log.
      *
      * Expects an authorization header.
      */
    val clearEvents: Endpoint[String] = get(Routes.ClearEventLog :: header(Names.AUTHORIZATION)) {
      (jwt: String) => {
        val result = Bijection[Future[Int], TwitterFuture[Int]](AppEventDAO.clearAppEvents())

        val jwtPayload = auth.extractPayload(jwt)
        val userId = jwtPayload.userId

        Await.ready(AppEventDAO.logAdminClearEventLog(userId), Duration.Inf)

        log.debug(s"User with UID = $userId cleared the event log")

        result.map((a: Int) => {
          Ok("")
        })
      }
    }

    /**
      * The finch endpoint for creating a user account.
      *
      * Expects a username, password, admin status and an authorization header.
      *
      */
    val createUser: Endpoint[String] = get(Routes.CreateUser :: string :: string :: boolean :: header(Names.AUTHORIZATION)) {
      (username: String, hash: String, isAdmin: Boolean, jwt: String) => {
        val jwtPayload = auth.extractPayload(jwt)
        val userId = jwtPayload.userId
        Await.ready(AppEventDAO.logAdminCreateUserEvent(userId), Duration.Inf)

        val result = Bijection[Future[Int], TwitterFuture[Int]](AuthDAO.insertUser(username, hash, isAdmin))

        result.map{
          r=> Ok("")
        }
      }
    }

    /**
      * Adds the CORS headers necessary for REST API calls.
      */
    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq(Names.ACCEPT, Names.AUTHORIZATION, Names.ACCESS_CONTROL_ALLOW_CREDENTIALS))
    )

    val service     = (api :+: authenticate :+: verifyJWT :+: listEvents :+: clearEvents :+: deleteUser :+: createUser).toService
    val corsService = new Cors.HttpFilter(policy).andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }


}
