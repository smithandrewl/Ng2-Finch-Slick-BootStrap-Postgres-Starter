import java.sql.Timestamp
import java.time.LocalDateTime

import Authentication.{AuthFailure, AuthenticationResult}
import Model.AppAction._
import Model.AppActionResult._
import Model.AppEventSeverity._
import Model.AppEventType.AppEventType
import Model.AppSection._
import Model._
import com.twitter.logging.Logger
import org.mindrot.jbcrypt.BCrypt
import slick.ast.ColumnOption.PrimaryKey
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}

object tables {


  val logger = Logger("tables")

  val db: Database = Database.forURL(
    url    = "jdbc:postgresql://localhost/many_tasks",
    user   = "many_tasks_user",
    driver = "org.postgresql.Driver"
  )

  /********* Enum mappings *******************************************/
  // Source for mapping method: http://stackoverflow.com/a/31717056
  // User: Roman
  implicit val eventTypeMapper     = MappedColumnType.base[AppEventType, Int]    (_.id, AppEventType.apply)
  implicit val appSectionMapper    = MappedColumnType.base[AppSection, Int]      (_.id, AppSection.apply)
  implicit val appActionMapper     = MappedColumnType.base[AppAction, Int]       (_.id, AppAction.apply)
  implicit val eventSeverityMapper = MappedColumnType.base[AppEventSeverity, Int](_.id, AppEventSeverity.apply)
  implicit val actionResultMapper  = MappedColumnType.base[AppActionResult, Int] (_.id, AppActionResult.apply)

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
    def userId           = column[Int]             ("userid")
    def appEventType     = column[AppEventType]    ("appeventtype")
    def appSection       = column[AppSection]      ("appsection")
    def appAction        = column[AppAction]       ("appaction")
    def appActionResult  = column[AppActionResult] ("appactionresult")
    def appEventSeverity = column[AppEventSeverity]("appeventseverity")

    def * = (timestamp, userId, appEventType, appSection, appAction, appActionResult, appEventSeverity) <> ((AppEvent.apply _).tupled, AppEvent.unapply)
  }

  val events = TableQuery[EventTable]

  /************* DAO Objects **************************************************/
  object AuthDAO {
    def deleteUser(id: Int): Future[Int] = {
      db.run(users.filter(_.authId === id).delete)

    }

    def getUsers()(implicit e: ExecutionContext): Future[Seq[Auth]] = {
      db.run(users.result)
    }

    def login(username: String, pw: String): Future[AuthenticationResult] = {

      val query = users.filter(user => user.username === username).
                        map(usr     => (usr.hash, usr.isAdmin, usr.authId))

      db.run(query.result).map {
        case Vector(row) => BCrypt.checkpw(pw, row._1) match {
          case true => Authentication.AuthSuccess(Authentication.grantJWT(row._3, row._2))
          case _ => AuthFailure
        }
        case _ => AuthFailure
      }
    }
  }

  object AppEventDAO {
    type EventInsertFuture = Future[PostgresDriver.InsertActionExtensionMethods[tables.EventTable#TableElementType]#SingleInsertResult]

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
      logEvent(userId, AppEventType.App, AppSection.Admin, ClearEventLog, ActionSuccess, Major)
    }

    def logAdminListUsers(userId: Int): EventInsertFuture = {
      logEvent(userId, AppEventType.App, Admin, ListUsers, ActionNormal, Normal)
    }

    // TODO: logUserLogin should log the user id of the user or a null if the username does not exist
    def logUserLogin(success: Boolean): EventInsertFuture = {
      val actionResult = if(success) ActionSuccess else ActionFailure
      val sev = if(success) Normal else Minor

      logEvent(1, AppEventType.Auth, Login, UserLogin, actionResult, sev)
    }

    def logDeleteUser(userId: Int, worked: Boolean): EventInsertFuture = {
      val success = if(worked) ActionSuccess else ActionFailure

      logEvent(userId, AppEventType.App, Admin, DeleteUser, success, Major)
    }

    def insertAppEvent(event: AppEvent)(implicit e:ExecutionContext): EventInsertFuture = {
      db.run(events += event)
    }
  }
}