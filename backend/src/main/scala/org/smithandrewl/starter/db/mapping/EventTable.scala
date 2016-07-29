package org.smithandrewl.starter.db.mapping

import java.sql.Timestamp

import org.smithandrewl.starter.model.AppAction._
import org.smithandrewl.starter.model.AppActionResult._
import org.smithandrewl.starter.model.AppEvent
import org.smithandrewl.starter.model.AppEventSeverity._
import org.smithandrewl.starter.model.AppEventType._
import org.smithandrewl.starter.model.AppSection._
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

import org.smithandrewl.starter.db._
import org.smithandrewl.starter.db.mapping._

/**
  * Slick database table mapping for the [[org.smithandrewl.starter.model.AppEventType AppEventType]] class
  * @param tag
  */
class EventTable(tag: Tag) extends Table[AppEvent](tag, "appevent") {
  def timestamp        = column[Timestamp]       ("timestamp")
  def userId           = column[Int]             ("userid")
  def appEventType     = column[AppEventType]    ("appeventtype")
  def appSection       = column[AppSection]      ("appsection")
  def appAction        = column[AppAction]       ("appaction")
  def appActionResult  = column[AppActionResult] ("appactionresult")
  def appEventSeverity = column[AppEventSeverity]("appeventseverity")

  def * = (timestamp, userId, appEventType, appSection, appAction, appActionResult, appEventSeverity) <> ((AppEvent.apply _).tupled, AppEvent.unapply)
}