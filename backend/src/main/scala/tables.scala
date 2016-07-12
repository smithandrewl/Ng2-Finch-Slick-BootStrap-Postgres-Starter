import java.sql.Timestamp
import java.time.LocalDateTime

import Authentication.{AuthFailure, AuthenticationResult}
import Model.AppAction.AppAction
import Model.AppActionResult.AppActionResult
import Model.AppEventSeverity.AppEventSeverity
import Model.AppEventType.AppEventType
import Model.AppSection.AppSection
import Model._
import slick.ast.ColumnOption.PrimaryKey
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

object tables {

  val db: Database = Database.forURL(
    url    = "jdbc:postgresql://localhost/many_tasks",
    user   = "many_tasks_user",
    driver = "org.postgresql.Driver"
  )

  /********* Enum mappings *******************************************/
  // Source for mapping method: http://stackoverflow.com/a/31717056
  // User: Roman
  implicit val eventTypeMapper = MappedColumnType.base[AppEventType, Int](
    e => e.id,
    i => AppEventType.apply(i)
  )

  implicit val appSectionMapper = MappedColumnType.base[AppSection, Int](
    e => e.id,
    i => AppSection.apply(i)
  )

  implicit val appActionMapper = MappedColumnType.base[AppAction, Int](
    e => e.id,
    i => AppAction.apply(i)
  )

  implicit val eventSeverityMapper = MappedColumnType.base[AppEventSeverity, Int](
    e => e.id,
    i => AppEventSeverity.apply(i)
  )

  implicit val actionResultMapper = MappedColumnType.base[AppActionResult, Int](
    e => e.id,
    i => AppActionResult.apply(i)
  )

/*************** Table Classes **********************************************/
  class AuthTable(tag: Tag) extends Table[Auth](tag, "auth") {
    def authId   = column[Int]    ("authid", PrimaryKey)
    def username = column[String] ("username")
    def hash     = column[String] ("hash")
    def isAdmin  = column[Boolean]("isadmin")

    def * = (authId, username, hash, isAdmin) <>((Auth.apply _).tupled, Auth.unapply)
  }

  val users = TableQuery[AuthTable]

  class EventTable(tag: Tag) extends Table[AppEvent](tag, "appevent") {
    def timestamp        = column[Timestamp]       ("timestamp")
    def ipAddress        = column[String]          ("ipaddress")
    def userId           = column[Int]             ("userid")
    def appEventType     = column[AppEventType]    ("appeventtype")
    def appSection       = column[AppSection]      ("appsection")
    def appAction        = column[AppAction]       ("appaction")
    def appActionResult  = column[AppActionResult] ("appactionresult")
    def appEventSeverity = column[AppEventSeverity]("appeventseverity")

    def * = (timestamp, ipAddress, userId, appEventType, appSection, appAction, appActionResult, appEventSeverity) <> ((AppEvent.apply _).tupled, AppEvent.unapply)
  }

  val events = TableQuery[EventTable]

  /************* DAO Objects **************************************************/
  object AuthDAO {
    def getUsers()(implicit e: ExecutionContext): Future[Seq[Auth]] = {
      db.run(users.result)
    }

    def login(username: String, hash: String): Future[AuthenticationResult] = {
      val query = users.filter(user => user.username === username).
                        filter(user => user.hash     === hash).
                        map(usr     => (usr.isAdmin, usr.authId))

      db.run(query.result).map {
        case Vector(row) => Authentication.AuthSuccess(Authentication.grantJWT(row._2, row._1))
        case _ => AuthFailure
      }
    }
  }

  object AppEventDAO {
    def getAppEvents()(implicit e:ExecutionContext): Future[Seq[AppEvent]] = {
      db.run(events.result)
    }

    def logEvent(ipAddress: String, userId: Int, appEventType: AppEventType, appSection: AppSection, appAction: AppAction, appActionResult: AppActionResult, appEventSeverity: AppEventSeverity): Future[PostgresDriver.InsertActionExtensionMethods[tables.EventTable#TableElementType]#SingleInsertResult] = {
      insertAppEvent(AppEvent(Timestamp.valueOf(LocalDateTime.now), ipAddress, userId, appEventType, appSection, appAction, appActionResult, appEventSeverity))
    }

    def insertAppEvent(event: AppEvent)(implicit e:ExecutionContext): Future[PostgresDriver.InsertActionExtensionMethods[tables.EventTable#TableElementType]#SingleInsertResult] = {
      val action = events += event

      val str = action.statements.mkString("\n")
      db.run(action)
    }
  }
}