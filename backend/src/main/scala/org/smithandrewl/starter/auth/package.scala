package org.smithandrewl.starter

import java.math.BigInteger
import java.security.SecureRandom

import cats.data.Xor
import io.circe.generic.auto._
import io.circe.jawn._
import io.circe.syntax._
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.keys.HmacKey
import org.jose4j.lang.JoseException
import org.smithandrewl.starter.model.JwtPayload

/**
  * Authorization support.
  *
  * Contains support functions related to JWT authorization.
  */
package object auth {

  /**
    * The key used to sign and verify JWT tokens,
    *
    * ''key'' is randomized and generated when the application starts.
    */
  val key = new HmacKey(new BigInteger(512, new SecureRandom()).toByteArray) // Generates a new random key at start


  /**
    * Grants a new signed JWT token.
    *
    * The user's Id and admin status are encoded in the ''payload'' section of the JWT.
    *
    * @param userId The id of the user
    * @param isAdmin The admin status of the user
    * @return The generated and signed JWT token
    */
  def grantJWT(userId: Int, isAdmin: Boolean): String = {
    val signature = new JsonWebSignature()

    signature.setPayload(JwtPayload(userId, isAdmin).asJson.toString())
    signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA512)
    signature.setHeader("type", "JWT")
    signature.setKey(key)

    signature.getCompactSerialization
  }

  // Returns whether the passed in JWT is valid

  /**
    * Validates a JWT token.
    *
    * ''Auth.key'' is used verify that the token was signed by our application.
    *
    * @param jwt The token to verify
    * @return The validity of the token
    */
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

  /**
    * Parses a JWT token from a string.
    *
    * Given a compact string representation of a JWT token,
    * a [[org.smithandrewl.starter.model.JwtPayload JwtPayload]] instance is returned,
    * or an exception is thrown.
    *
    * @param jwt The JWT string
    * @return The parsed JWT token
    */
  def extractPayload(jwt: String): JwtPayload = {

    val sig = new JsonWebSignature()

    sig.setCompactSerialization(jwt)
    sig.setKey(key)

    val payload = sig.getPayload

    decode[JwtPayload](payload) match {
      case Xor.Right(jwtPayload: JwtPayload) => jwtPayload
      case Xor.Left(e)                       => throw e
    }
  }
}
