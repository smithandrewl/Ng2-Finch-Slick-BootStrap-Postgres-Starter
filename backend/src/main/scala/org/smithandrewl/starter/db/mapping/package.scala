package org.smithandrewl.starter.db

import org.smithandrewl.starter.model.AppAction.{apply => _, _}
import org.smithandrewl.starter.model.AppActionResult.{apply => _, _}
import org.smithandrewl.starter.model.AppEventSeverity.{apply => _, _}
import org.smithandrewl.starter.model._
import org.smithandrewl.starter.model.AppEventType._
import org.smithandrewl.starter.model.AppSection.{apply => _, _}
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.api._

/**
  *
  */
package object mapping {
  val users  = TableQuery[AuthTable]
  val events = TableQuery[EventTable]

  /********* Enum mappings *******************************************/
  // Source for mapping method: http://stackoverflow.com/a/31717056
  // User: Roman
  implicit val eventTypeMapper     = MappedColumnType.base[AppEventType, Int]    (_.id, AppEventType.apply)
  implicit val appSectionMapper    = MappedColumnType.base[AppSection, Int]      (_.id, AppSection.apply)
  implicit val appActionMapper     = MappedColumnType.base[AppAction, Int]       (_.id, AppAction.apply)
  implicit val eventSeverityMapper = MappedColumnType.base[AppEventSeverity, Int](_.id, AppEventSeverity.apply)
  implicit val actionResultMapper  = MappedColumnType.base[AppActionResult, Int] (_.id, AppActionResult.apply)
}
