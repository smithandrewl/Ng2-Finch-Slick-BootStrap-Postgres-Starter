package org.smithandrewl.starter.model

object AppActionResult extends Enumeration {
  type AppActionResult = Value

  val ActionSuccess = Value(1)
  val ActionFailure = Value(2)
  val ActionNormal  = Value(3)
}