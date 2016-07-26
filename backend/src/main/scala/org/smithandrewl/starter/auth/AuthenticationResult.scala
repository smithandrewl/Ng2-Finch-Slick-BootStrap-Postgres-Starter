package org.smithandrewl.starter.auth

// Represents the result of authentication
sealed trait AuthenticationResult
case class  AuthSuccess(jwt:String) extends AuthenticationResult
case object AuthFailure             extends AuthenticationResult