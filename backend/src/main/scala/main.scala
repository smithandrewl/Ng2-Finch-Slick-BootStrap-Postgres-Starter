import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Duration}
import io.finch._
import slick.driver.PostgresDriver.backend._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._


object Main extends TwitterServer {
  def main() {

    val api: Endpoint[String] = get("users") {

      val users = tables.AuthDAO.getUsers()

      // users.onSuccess(usr => s"id = $usr.id name = $usr.name hash = $usr.hash isAdmin= $usr.isAdmin")
      @volatile
      var msg: String = ""

      users.onComplete {
        case Success(usrs) => msg = "{\"id\":" + usrs(0)._1 + ", \"name\": \"" + usrs(0)._2 + "\", \"hash\": \"" + usrs(0)._3 + "\",  \"isAdmin\": \"" + usrs(0)._4 + "\"}"
        case Failure(e)    => e.printStackTrace()
      }

      while(!users.isCompleted) {
        Thread.sleep(10)
      }

      Ok(msg)

    }


    val server = Http.serve(":8080", api.toService)

    onExit {
      server.close()
    }

     com.twitter.util.Await.ready(server)
  }
}
