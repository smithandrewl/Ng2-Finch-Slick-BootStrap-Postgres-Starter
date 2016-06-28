import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.keys.HmacKey

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._
import java.security.SecureRandom
import java.math.BigInteger

import com.twitter.bijection.Bijection
import com.twitter.finagle.param.Stats
import tables.Auth
import com.twitter.bijection.twitter_util.UtilBijections._
import scala.concurrent.Future
import com.twitter.util.{Future => TwitterFuture}


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

        val isCorrect = tables.AuthDAO.verifyUser(username, hash)

        @volatile
        var msg: String = "no such username or incorrect password"

        isCorrect.onComplete {
          case Success(value) => {

            if (value) {
              val signature = new JsonWebSignature()

              signature.setPayload("payload")
              signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512)
              signature.setHeader("type", "JWT")

              signature.setKey(key)

              msg = signature.getCompactSerialization()
            }
          }
        }

        while(!isCorrect.isCompleted) Thread.sleep(10)
        Ok(msg.toString)
      }
    }

    val server =  Http.server.configured(Stats(statsReceiver)).serve(":8080", (api :+: authenticate :+: verifyJWT).toService)

    onExit { server.close() }

    com.twitter.util.Await.ready(server)
  }
}
