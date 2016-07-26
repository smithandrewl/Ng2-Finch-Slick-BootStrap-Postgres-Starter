package org.smithandrewl.starter.db.dao

import java.sql.Timestamp
import java.time.LocalDateTime

import org.smithandrewl.starter.db._
import org.smithandrewl.starter.db.mapping.{EventTable, _}
import org.smithandrewl.starter.model.AppAction._
import org.smithandrewl.starter.model.AppActionResult._
import org.smithandrewl.starter.model.AppEventSeverity._
import org.smithandrewl.starter.model.AppEventType._
import org.smithandrewl.starter.model._
import org.smithandrewl.starter.model.AppSection._
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ExecutionContext, Future}

object AppEventDAO {
  type EventInsertFuture = Future[PostgresDriver.InsertActionExtensionMethods[EventTable#TableElementType]#SingleInsertResult]

  def clearAppEvents()(implicit e:ExecutionContext): Future[Int] = db.run(events.delete)

  def getAppEvents()(implicit e:ExecutionContext): Future[Seq[AppEvent]] = {
    db.run(events.sortBy(_.timestamp.desc.desc).result)
  }

  private[this] def logEvent(userId: Int, appEventType: AppEventType, appSection: AppSection, appAction: AppAction, appActionResult: AppActionResult, appEventSeverity: AppEventSeverity): EventInsertFuture = {
    insertAppEvent(
      AppEvent(
        Timestamp.valueOf(LocalDateTime.now),
        userId,
        appEventType,
        appSection,
        appAction,
        appActionResult,
        appEventSeverity
      )
    )
  }

  def logAdminClearEventLog(userId: Int): EventInsertFuture = {
    logEvent(userId, AppEventType.App, AppSection.Admin, AppAction.ClearEventLog, AppActionResult.ActionSuccess, Major)
  }

  def logAdminListUsers(userId: Int): EventInsertFuture = {
    logEvent(userId, AppEventType.App, AppSection.Admin, ListUsers, ActionNormal, Normal)
  }

  // TODO: logUserLogin should log the user id of the user or a null if the username does not exist
  def logUserLogin(success: Boolean): EventInsertFuture = {
    val actionResult = if(success) ActionSuccess else ActionFailure
    val sev = if(success) Normal else Minor

    logEvent(1, AppEventType.Auth, AppSection.Login, UserLogin, actionResult, sev)
  }

  def logDeleteUser(userId: Int, worked: Boolean): EventInsertFuture = {
    val success = if(worked) ActionSuccess else ActionFailure

    logEvent(userId, AppEventType.App, AppSection.Admin, DeleteUser, success, Major)
  }

  def insertAppEvent(event: AppEvent)(implicit e:ExecutionContext): EventInsertFuture = {
    db.run(events += event)
  }
}