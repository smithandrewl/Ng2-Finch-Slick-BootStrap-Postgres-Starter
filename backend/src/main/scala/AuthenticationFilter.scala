import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future

class AuthenticationFilter() extends Filter[Request, Response, Request, Response] {

  val nonAdminRoutes = Seq("/home")

  def apply(req: Request, service: Service[Request, Response]): Future[Response] = {

    val path = req.path
    val isAuthPath = path.startsWith("/authenticate")

    // if the path is any except for /authenticate
    if (!isAuthPath) {
      val isPresent = req.headerMap.contains(Fields.Authorization)
      var jwt: String = ""

      if (isPresent) {
        // Get jwt from authenticate header
        jwt = req.headerMap.getOrElse(Fields.Authorization, "")
      }

      val invalid = !(isPresent && Authentication.verifyJWT(jwt))

      val jwtPayload = Authentication.extractPayload(jwt)


      val nonAdminRoute: Boolean = nonAdminRoutes.exists((route: String) => path.startsWith(route))

      val accessGranted = jwtPayload.isAdmin || nonAdminRoute
      val unauthorized = !accessGranted
      val resp = req.response

      if (invalid) {
        // if it is not present or malformed, return a 401 unauthorized
        resp.setStatusCode(401)
        Future(resp)

      } else if(unauthorized) {
        // TODO: Log unauthorized user action
        // if the user is not an admin but going to an admin route, return a 403 access denied
        resp.setStatusCode(403)
        Future(resp)
      } else {
        service(req)
      }
    } else {
      service(req)
    }
  }
}
