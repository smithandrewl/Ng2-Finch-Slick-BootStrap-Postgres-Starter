package org.smithandrewl.starter.filter

import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import org.jose4j.lang.JoseException
import org.smithandrewl.starter.auth

/**
  * Verifies that a user is authorized to access the REST API that they are calling.
  */
class AuthenticationFilter()
    extends Filter[Request, Response, Request, Response] {

  val nonAdminRoutes = Seq("/home")

  def apply(req: Request,
            service: Service[Request, Response]): Future[Response] = {

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
      val resp = req.response

      try {
        val invalid = !(isPresent && auth.verifyJWT(jwt))

        val jwtPayload = auth.extractPayload(jwt)

        val nonAdminRoute: Boolean =
          nonAdminRoutes.exists((route: String) => path.startsWith(route))

        val accessGranted = jwtPayload.isAdmin || nonAdminRoute
        val unauthorized = !accessGranted

        if (invalid) {
          // if it is not present or is not signed with the correct key, return a 401 unauthorized
          resp.setStatusCode(401)
          Future(resp)

        } else if (unauthorized) {
          // TODO: Log unauthorized user action
          // if the user is not an admin but going to an admin route, return a 403 access denied
          resp.setStatusCode(403)
          Future(resp)
        } else {
          service(req)
        }
      } catch {
        case (e: JoseException) => {
          // TODO: Log action with invalid JWT
          // if the Authorization header is present but invalid, return a 401 unauthorized
          resp.setStatusCode(401)
          Future(resp)
        }
      }
    } else {
      service(req)
    }
  }
}
