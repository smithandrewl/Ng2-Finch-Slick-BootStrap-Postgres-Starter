package org.smithandrewl.starter.model

/**
  * An enumeration representing the possible results of a user action.
  */
object AppActionResult extends Enumeration {
  type AppActionResult = Value

  /**
    * Represents a successful user action, such as a successful login.
    */
  val ActionSuccess = Value(1)

  /**
    * Represents a failed user action, such as a failed login.
    */
  val ActionFailure = Value(2)

  /**
    * Represents a user action which does not fail or succeed, such as viewing user accounts.
    */
  val ActionNormal  = Value(3)
}