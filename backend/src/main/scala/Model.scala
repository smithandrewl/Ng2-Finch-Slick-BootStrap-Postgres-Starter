import Model.AppAction.AppAction
import Model.AppActionResult.AppActionResult
import Model.AppEventSeverity.AppEventSeverity
import Model.AppEventType.AppEventType
import Model.AppSection.AppSection

import java.sql.Timestamp

object Model {
  case class Auth(userId: Int, username: String, hash: String, isAdmin: Boolean)
  case class JwtPayload(userId: Int, isAdmin: Boolean)

  object AppEventType extends Enumeration {
    type AppEventType = Value

    val Auth = Value(1)
    val App  = Value(2)
  }

  object AppSection extends Enumeration {
    type AppSection = Value

    val Login = Value(1)
    val Admin = Value(2)
  }

  object AppAction extends Enumeration {
    type AppAction = Value

    val ListUsers     = Value(1)
    val UserLogin     = Value(2)
    val UserLogout    = Value(3)
    val ClearEventLog = Value(4)
    val DeleteUser    = Value(5)
  }

  object AppEventSeverity extends Enumeration {
    type AppEventSeverity = Value

    val Minor  = Value(1)
    val Major  = Value(2)
    val Normal = Value(3)
  }

  object AppActionResult extends Enumeration {
    type AppActionResult = Value

    val ActionSuccess = Value(1)
    val ActionFailure = Value(2)
    val ActionNormal  = Value(3)
  }

  case class AppEvent(timestamp:        Timestamp,
                      userId:           Int,
                      appEventType:     AppEventType,
                      appSection:       AppSection,
                      appAction:        AppAction,
                      appActionResult:  AppActionResult,
                      appEventSeverity: AppEventSeverity)

}
