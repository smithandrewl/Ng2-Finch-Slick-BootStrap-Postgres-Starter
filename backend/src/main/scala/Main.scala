import java.math.BigInteger
import java.security.SecureRandom

import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.{Future => TwitterFuture}
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch.Output.Failure
import io.finch._
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.keys.HmacKey
import tables.Auth

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends TwitterServer {
  def main() {
    val key: HmacKey = new HmacKey(new BigInteger(512, new SecureRandom()).toByteArray)

    val api: Endpoint[String] = get("users") {
      val users: TwitterFuture[Seq[Auth]] =  Bijection[Future[Seq[Auth]],TwitterFuture[Seq[Auth]]](tables.AuthDAO.getUsers())

      Ok(users.map(usrs => usrs.asJson.toString()))
    }

    val verifyJWT: Endpoint[String] = get("verify_jwt" :: string) {
      (jwt: String) => {
        val signature = new JsonWebSignature()

        signature.setCompactSerialization(jwt)
        signature.setKey(key)

        Ok("" + signature.verifySignature())
      }
    }

    val authenticate: Endpoint[String] = get("authenticate" :: string :: string) {
      (username: String, hash: String) => {
        val isCorrect: TwitterFuture[Option[Boolean]] = Bijection[Future[Option[Boolean]], TwitterFuture[Option[Boolean]]](tables.AuthDAO.verifyUser(username, hash))

        isCorrect.map((user: Option[Boolean]) => user match {
          case Some(isAdmin) => {
            val signature = new JsonWebSignature()

            signature.setPayload(s"IsAdmin: $isAdmin")
            signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512)
            signature.setHeader("type", "JWT")

            signature.setKey(key)

            Ok(signature.getCompactSerialization)
          }
          case None => Output.failure(new Exception("no such username or incorrect password"))
        }
        )
      }
    }

    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin  = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST")),
      allowsHeaders = _ => Some(Seq("Accept"))
    )

    val service     = (api :+: authenticate :+: verifyJWT).toService
    val corsService = new Cors.HttpFilter(policy).andThen(service)
    val server      =  Http.server.configured(Stats(statsReceiver)).serve(":8080",  corsService )

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
