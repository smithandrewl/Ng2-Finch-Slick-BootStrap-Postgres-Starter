import com.twitter.finagle.zipkin.thriftscala.Scribe.Log
import slick.ast.ColumnOption.PrimaryKey
import slick.lifted.{TableQuery, Tag}
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object tables {
  val db: Database = Database.forURL("jdbc:postgresql://localhost/many_tasks", user = "many_tasks_user", driver = "org.postgresql.Driver")



  class Auth(tag: Tag) extends Table[(Int, String, String, Boolean)](tag, "auth") {
    def authId   = column[Int]("authid", PrimaryKey)
    def username = column[String]("username")
    def hash     = column[String]("hash")
    def isAdmin  = column[Boolean]("isadmin")

    def * = (authId, username, hash, isAdmin)
  }

  val users = TableQuery[Auth]

  object AuthDAO {

    def getUsers() (implicit e :ExecutionContext): Future[Seq[(Int, String, String, Boolean)]] = {

      db.run(users.result)
    }

  }
}