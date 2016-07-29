package org.smithandrewl.starter.model

import java.sql.Timestamp

import org.smithandrewl.starter.model.AppAction.AppAction
import org.smithandrewl.starter.model.AppActionResult.AppActionResult
import org.smithandrewl.starter.model.AppEventSeverity.AppEventSeverity
import org.smithandrewl.starter.model.AppEventType.AppEventType
import org.smithandrewl.starter.model.AppSection.AppSection

/**
  * Represents an application log event.
  *
  * @param timestamp The time the event occurred
  * @param userId  The id of the user who generated the event
  * @param appEventType The type of the event
  * @param appSection The section of the app where the event took place
  * @param appAction The action the user took which generated the event
  * @param appActionResult The result of the action
  * @param appEventSeverity The severity of the action
  */
case class AppEvent(timestamp:        Timestamp,
                    userId:           Int,
                    appEventType:     AppEventType,
                    appSection:       AppSection,
                    appAction:        AppAction,
                    appActionResult:  AppActionResult,
                    appEventSeverity: AppEventSeverity)
