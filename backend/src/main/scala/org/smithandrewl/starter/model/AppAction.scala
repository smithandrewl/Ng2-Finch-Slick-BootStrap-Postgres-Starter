package org.smithandrewl.starter.model

object AppAction extends Enumeration {
  type AppAction = Value

  val ListUsers     = Value(1)
  val UserLogin     = Value(2)
  val UserLogout    = Value(3)
  val ClearEventLog = Value(4)
  val DeleteUser    = Value(5)
}
