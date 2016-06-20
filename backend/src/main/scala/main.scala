/**
  * Created by andrew on 6/20/16.
  */
import com.twitter.finagle.Http
import com.twitter.util.{Await, Try}
import io.finch._


object Main extends App {
  val api: Endpoint[String] = get("hello") { Ok("Hello World!")}

  Await.ready(Http.server.serve(":8080", api.toService))

}