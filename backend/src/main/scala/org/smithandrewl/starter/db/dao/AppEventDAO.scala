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
import scala.concurrent.{Awaitable, ExecutionContext, Future}

/**
  * The data access object for [[org.smithandrewl.starter.model.AppEvent AppEvent]] instances.
  */
object AppEventDAO {
  /**
    * Logs the creation of a new user.
    *
    * @param userId The id of the user
    * @return The result of the insert
    */
  def logAdminCreateUserEvent(userId: Int): EventInsertFuture = {
    logEvent(userId, AppEventType.App, AppSection.Admin, AppAction.CreateUser, AppActionResult.ActionSuccess, AppEventSeverity.Major)
  }

  /**
    * The result of inserting an AppEvent into the database.
    */
  type EventInsertFuture = Future[PostgresDriver.InsertActionExtensionMethods[EventTable#TableElementType]#SingleInsertResult]

  /**
    * Deletes all event log entries.
    *
    * @param e Execution context
    * @return The number of rows deleted
    */
  def clearAppEvents()(implicit e:ExecutionContext): Future[Int] = db.run(events.delete)

  /**
    * Returns a collection of every log entry in the database.
    * @param e ExecutionContext
    * @return The collection of log entries
    */
  def getAppEvents()(implicit e:ExecutionContext): Future[Seq[AppEvent]] = {
    db.run(events.sortBy(_.timestamp.desc.desc).result)
  }

  /**
    * Logs a user event into the event log.
    *
    * @param userId The id of the user who generated the event
    * @param appEventType The type of the event
    * @param appSection The section of the application where the event was generated
    * @param appAction The user action which is being logged
    * @param appActionResult The result of the user action
    * @param appEventSeverity The severity of the logged event
    * @return The result of the insertion
    */
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

  /**
    * Logs the clearing of the event log.
    *
    * @param userId The id of the user clearing the log
    * @return The result of the insertion
    */
  def logAdminClearEventLog(userId: Int): EventInsertFuture = {
    logEvent(userId, AppEventType.App, AppSection.Admin, AppAction.ClearEventLog, AppActionResult.ActionSuccess, Major)
  }

  /**
    * Logs the listing of user accounts.
    *
    * @param userId The id of the user view the accounts
    * @return The result of the insertion
    */
  def logAdminListUsers(userId: Int): EventInsertFuture = {
    logEvent(userId, AppEventType.App, AppSection.Admin, ListUsers, ActionNormal, Normal)
  }

  /**
    * Logs a user login attempt.
    *
    * @param success Whether the attempt was successful
    * @return The result of the insertion.
    */
  // TODO: logUserLogin should log the user id of the user or a null if the username does not exist
  def logUserLogin(success: Boolean): EventInsertFuture = {
    val actionResult = if(success) ActionSuccess else ActionFailure
    val sev = if(success) Normal else Minor

    logEvent(1, AppEventType.Auth, AppSection.Login, UserLogin, actionResult, sev)
  }

  /**
    * Logs the deletion of a user account.
    *
    * @param userId The id of the user performing the account deletion
    * @param worked Whether or not the attempt succeeded
    * @return The result of the insertion
    */
  def logDeleteUser(userId: Int, worked: Boolean): EventInsertFuture = {
    val success = if(worked) ActionSuccess else ActionFailure

    logEvent(userId, AppEventType.App, AppSection.Admin, DeleteUser, success, Major)
  }

  /**
    * A helper method to insert an [[org.smithandrewl.starter.model.AppEvent AppEvent]] instance into the database.
    *
    * @param event The event to insert
    * @param e Execution context
    * @return The result of the insertion.
    */
  def insertAppEvent(event: AppEvent)(implicit e:ExecutionContext): EventInsertFuture = {
    db.run(events += event)
  }
}