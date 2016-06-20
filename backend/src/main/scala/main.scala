import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch._
import slick.driver.PostgresDriver.backend._

object Main extends TwitterServer {
  def main() {
    val db: Database = Database.forURL("jdbc:postgresql://localhost/many_tasks", user = "many_tasks_user", driver = "org.postgresql.Driver")

    val sess = db.createSession()

    val api: Endpoint[String] = get("hello") { Ok("Hello World!")}


    val server = Http.serve(":8080", api.toService)

    onExit {
      server.close()
    }

    Await.ready(server)
  }
}
