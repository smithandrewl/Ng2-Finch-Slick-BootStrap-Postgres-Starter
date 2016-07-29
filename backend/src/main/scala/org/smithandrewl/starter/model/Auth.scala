package org.smithandrewl.starter.model

/**
  * Represents a user account.
  *
  * @param userId The id of the user
  * @param username The username of the user
  * @param hash The password of the user
  * @param isAdmin Whether the user is an admin
  */
case class Auth(userId: Int, username: String, hash: String, isAdmin: Boolean)

