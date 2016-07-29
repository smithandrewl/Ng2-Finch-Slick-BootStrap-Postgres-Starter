package org.smithandrewl.starter.model

/**
  * Represents the contents of the payload section of a JWT token.
  *
  * @param userId The id of the user
  * @param isAdmin Whether the user is an administrator
  */
case class JwtPayload(userId: Int, isAdmin: Boolean)