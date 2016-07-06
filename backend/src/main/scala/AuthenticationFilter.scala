import com.twitter.finagle.{Filter, Service}
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.util.Future
import io.finch._

class AuthenticationFilter()
    extends Filter[Request, Response, Request, Response] {
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

      if (invalid) {
        // if it is not present or malformed, return a 402 unauthorized
        val resp = req.response

        resp.setStatusCode(402)
        Future(resp)

      } else {
        service(req)
      }
    } else {
      service(req)
    }
  }
}
