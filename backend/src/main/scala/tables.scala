import java.sql.Timestamp

import Authentication.{AuthFailure, AuthSuccess, AuthenticationResult}
import slick.ast.ColumnOption.PrimaryKey
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

  object AppEventType extends Enumeration {
    type AppEventType = Value

    val Auth = Value(1)
    val App  = Value(2)

    implicit val eventTypeMapper = MappedColumnType.base[AppEventType, Int](
      e => e.id,
      i => AppEventType.apply(i)
    )
  }

  object AppSection extends Enumeration {
    type AppSection = Value

    val Login = Value(1)
    val Admin = Value(2)

    implicit val appSectionMapper = MappedColumnType.base[AppSection, Int](
      e => e.id,
      i => AppSection.apply(i)
    )
  }

  object AppAction extends Enumeration {
    type AppAction = Value

    val ListUsers  = Value(1)
    val UserLogin  = Value(2)
    val UserLogout = Value(3)

    implicit val appActionMapper = MappedColumnType.base[AppAction, Int](
      e => e.id,
      i => AppAction.apply(i)
    )
  }

  object AppEventSeverity extends Enumeration {
    type AppEventSeverity = Value

    val Minor  = Value(1)
    val Major  = Value(2)
    val Normal = Value(3)

    implicit val eventSeverityMapper = MappedColumnType.base[AppEventSeverity, Int](
      e => e.id,
      i => AppEventSeverity.apply(i)
    )
  }

  object AppActionResult extends Enumeration {
    type AppActionResult = Value

    val ActionSuccess = Value(1)
    val ActionFailure = Value(2)
    val ActionNormal  = Value(3)

    implicit val actionResultMapper = MappedColumnType.base[AppActionResult, Int](
      e => e.id,
      i => AppActionResult.apply(i)
    )
  }

  import AppAction._
  import AppEventType._
  import AppSection._
  import AppEventSeverity._
  import AppActionResult._

  /**************** Model Classes ***********************************************/
  case class Auth(userId: Int, username: String, hash: String, isAdmin: Boolean)

  case class Event(
                    timestamp:        Timestamp,
                    ipAddress:        String,
                    userId:           Int,
                    appEventType:     AppEventType,
                    appSection:       AppSection,
                    appAction:        AppAction,
                    appActionResult:  AppActionResult,
                    appEventSeverity: AppEventSeverity
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

  class EventTable(tag: Tag) extends Table[Event](tag, "AppEvent") {
    def timestamp        = column[Timestamp]       ("timestamp")
    def ipAddress        = column[String]          ("ipAddress")
    def userId           = column[Int]             ("userId")
    def appEventType     = column[AppEventType]    ("AppEventType")
    def appSection       = column[AppSection]      ("AppSection")
    def appAction        = column[AppAction]       ("AppAction")
    def appActionResult  = column[AppActionResult] ("AppActionResult")
    def appEventSeverity = column[AppEventSeverity]("AppEventSeverity")

    def * = (timestamp, ipAddress, userId, appEventType, appSection, appAction, appActionResult, appEventSeverity) <> ((Event.apply _).tupled, Event.unapply)
  }

  /************* DAO Objects **************************************************/
  object AuthDAO {
    def getUsers()(implicit e: ExecutionContext): Future[Seq[Auth]] = {
      db.run(users.result)
    }

    def login(username: String, hash: String): Future[AuthenticationResult] = {
      val query = users.filter(user => user.username === username).
                        filter(user => user.hash     === hash).
                        map(usr     => usr.isAdmin)

      db.run(query.result) map {
        case Vector(isAdmin) => AuthSuccess(Authentication.grantJWT(isAdmin))
        case _               => AuthFailure
      }
    }
  }
}