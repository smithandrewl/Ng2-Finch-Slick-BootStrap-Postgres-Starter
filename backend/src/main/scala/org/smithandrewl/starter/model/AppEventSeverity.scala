package org.smithandrewl.starter.model

/**
  * Enumeration of the severity levels of a logged event.
  */
object AppEventSeverity extends Enumeration {
  type AppEventSeverity = Value

  /**
    * Represents a minor logging level.
    */
  val Minor  = Value(1)


  /**
    * Represents a major logging level.
    */
  val Major  = Value(2)


  /**
    * Represents a normal logging level.
    */
  val Normal = Value(3)
}