/**
  * Created by andrew on 6/20/16.
  */
import com.twitter.finagle.Http
import com.twitter.util.{Await, Try}
import io.finch._
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.io.Charsets
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

object Main extends TwitterServer {
  val api: Endpoint[String] = get("hello") { Ok("Hello World!")}

  def main() {
    val server = Http.serve(":8080", api.toService)

    onExit {
      server.close()
    }

    Await.ready(server)
  }
}
