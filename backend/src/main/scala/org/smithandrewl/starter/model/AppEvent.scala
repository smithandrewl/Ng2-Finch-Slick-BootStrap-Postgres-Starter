package org.smithandrewl.starter.model

import java.sql.Timestamp

import org.smithandrewl.starter.model.AppAction.AppAction
import org.smithandrewl.starter.model.AppActionResult.AppActionResult
import org.smithandrewl.starter.model.AppEventSeverity.AppEventSeverity
import org.smithandrewl.starter.model.AppEventType.AppEventType
import org.smithandrewl.starter.model.AppSection.AppSection

case class AppEvent(timestamp:        Timestamp,
                    userId:           Int,
                    appEventType:     AppEventType,
                    appSection:       AppSection,
                    appAction:        AppAction,
                    appActionResult:  AppActionResult,
                    appEventSeverity: AppEventSeverity)
