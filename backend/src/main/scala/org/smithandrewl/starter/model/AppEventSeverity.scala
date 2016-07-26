package org.smithandrewl.starter.model

object AppEventSeverity extends Enumeration {
  type AppEventSeverity = Value

  val Minor  = Value(1)
  val Major  = Value(2)
  val Normal = Value(3)
}