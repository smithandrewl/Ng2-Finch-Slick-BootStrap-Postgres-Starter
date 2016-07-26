package org.smithandrewl.starter.model

object AppEventType extends Enumeration {
  type AppEventType = Value

  val Auth = Value(1)
  val App  = Value(2)
}