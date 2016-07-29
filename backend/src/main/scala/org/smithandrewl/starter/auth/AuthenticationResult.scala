package org.smithandrewl.starter.auth

/**
  * Represents the possible result states of authentication.
  */
sealed trait AuthenticationResult

/**
  * Represents a successful authentication.
  *
  * @param jwt The resulting [[https://tools.ietf.org/html/rfc7519 JWT token]]
  */
case class  AuthSuccess(jwt:String) extends AuthenticationResult

/**
  * Represents an authentication failure.
  */
case object AuthFailure extends AuthenticationResult