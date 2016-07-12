import java.math.BigInteger
import java.net.InetSocketAddress
import java.security.SecureRandom

import Model.JwtPayload
import com.fasterxml.jackson.core.io.JsonStringEncoder
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import io.circe.{Encoder, Json, JsonObject}
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.keys.HmacKey
import io.circe.Decoder
import io.circe.jawn._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch._
import tables.AppEvent


object Authentication {

  // Generate a new random key at start
  val key = new HmacKey(new BigInteger(512, new SecureRandom()).toByteArray)

  // Represents the result of authentication
  sealed trait AuthenticationResult
  case class AuthSuccess(jwt:String) extends AuthenticationResult
  case object AuthFailure extends AuthenticationResult

  // Returns a new signed JWT token
  def grantJWT(userId: Int, isAdmin: Boolean): String = {
    val signature = new JsonWebSignature()

    val payload = JwtPayload(userId, isAdmin)




    signature.setPayload(payload.asJson.toString())

    signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512)
    signature.setHeader("type", "JWT")
    signature.setKey(key)

    signature.getCompactSerialization
  }

  // Returns whether the passed in JWT is valid
  def verifyJWT(jwt: String): Boolean = {
    val signature = new JsonWebSignature()

    signature.setCompactSerialization(jwt)
    signature.setKey(key)

    signature.verifySignature()
  }
}
