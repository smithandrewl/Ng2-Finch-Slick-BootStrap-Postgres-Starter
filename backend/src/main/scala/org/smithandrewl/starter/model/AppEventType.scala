package org.smithandrewl.starter.model

/**
  * Enumeration of log event types.
  */
object AppEventType extends Enumeration {
  type AppEventType = Value

  /**
    * Represents an ''authorization'' event.
    */
  val Auth = Value(1)


  /**
    * Represents an in-app user action.
    */
  val App  = Value(2)
}