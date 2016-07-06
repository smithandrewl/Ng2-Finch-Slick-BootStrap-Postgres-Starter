import Authentication.{AuthFailure, AuthSuccess, AuthenticationResult}
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
import tables.Auth

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get("users") {
      val users: TwitterFuture[Seq[Auth]] =  Bijection[Future[Seq[Auth]],TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

      Ok(users.map(usrs => usrs.asJson.toString()))
    }

    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => Ok("" + Authentication.verifyJWT(jwt))
    }

    val authenticate: Endpoint[String] = get("authenticate" :: string :: string) {
      (username: String, hash: String) => {
        Bijection[Future[AuthenticationResult], TwitterFuture[AuthenticationResult]](tables.AuthDAO.login(username, hash)).map {
          case AuthSuccess(jwt) => Ok(jwt)
          case AuthFailure      => Ok("No such user or incorrect password")
        }
      }
    }

    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq("Accept"))
    )

    val service     = (api :+: authenticate :+: verifyJWT).toService
    val corsService = new Cors.HttpFilter(policy).andThen(new AuthenticationFilter().andThen(service))
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
