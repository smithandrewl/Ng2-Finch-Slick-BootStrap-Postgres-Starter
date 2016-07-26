package org.smithandrewl.starter.auth

import java.math.BigInteger
import java.security.SecureRandom

import cats.data.Xor
import io.circe.generic.auto._
import io.circe.jawn._
import io.circe.syntax._
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.keys.HmacKey
import org.jose4j.lang.JoseException
import org.smithandrewl.starter.model.Model.JwtPayload

object Authentication {

  // Generate a new random key at start
  val key = new HmacKey(new BigInteger(512, new SecureRandom()).toByteArray)

  // Represents the result of authentication
  sealed trait AuthenticationResult
  case class  AuthSuccess(jwt:String) extends AuthenticationResult
  case object AuthFailure             extends AuthenticationResult

  // Returns a new signed JWT token
  def grantJWT(userId: Int, isAdmin: Boolean): String = {
    val signature = new JsonWebSignature()

    signature.setPayload(JwtPayload(userId, isAdmin).asJson.toString())
    signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512)
    signature.setHeader("type", "JWT")
    signature.setKey(key)

    signature.getCompactSerialization
  }

  // Returns whether the passed in JWT is valid
  def verifyJWT(jwt: String): Boolean = {
    val signature = new JsonWebSignature()

    try {
      signature.setCompactSerialization(jwt)
      signature.setKey(key)
      signature.verifySignature()
    } catch {
      case e:JoseException => false
    }
  }

  def extractPayload(jwt: String): JwtPayload = {

    val sig = new JsonWebSignature()

    sig.setCompactSerialization(jwt)
    sig.setKey(Authentication.key)

    val payload = sig.getPayload

    decode[JwtPayload](payload) match {
      case Xor.Right(jwtPayload: JwtPayload) => jwtPayload
      case Xor.Left(e)                       => throw e
    }
  }
}
