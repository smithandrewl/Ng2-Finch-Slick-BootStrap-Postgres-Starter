package org.smithandrewl.starter.model

/**
  * Enumeration of the actions a user can take.
  */
object AppAction extends Enumeration {
  type AppAction = Value

  /**
    * Represents the action of listing user accounts.
    */
  val ListUsers     = Value(1)

  /**
    * Represents the action of logging in.
    */
  val UserLogin     = Value(2)


  /**
    * Represents the action of logging out.
    */
  val UserLogout    = Value(3)

  /**
    * Represents the action of clearing the event log.
    */
  val ClearEventLog = Value(4)


  /**
    * Represents the action of deleting a user account.
    */
  val DeleteUser    = Value(5)


  /**
    * Represents the actioin of creating a user account.
    */
  val CreateUser    = Value(6)
}
