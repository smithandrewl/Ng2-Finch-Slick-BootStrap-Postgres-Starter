import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Duration}
import io.circe.Json
import io.finch._
import slick.driver.PostgresDriver.backend._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._
import io.circe.generic.auto._
import io.circe.syntax._



object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get("users") {

      val users = tables.AuthDAO.getUsers()

      @volatile
      var msg: Json = "".asJson

      users.onComplete {
        case Success(usrs) => msg = usrs.asJson
        case Failure(e)    => e.printStackTrace()
      }

      while(!users.isCompleted) {
        Thread.sleep(10)
      }

      Ok(msg.toString())

    }


    val server = Http.serve(":8080", api.toService)

    onExit {
      server.close()
    }

     com.twitter.util.Await.ready(server)
  }
}
