import com.twitter.finagle.zipkin.thriftscala.Scribe.Log
import slick.ast.ColumnOption.PrimaryKey
import slick.lifted.{TableQuery, Tag}
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future


object tables {
  case class Auth(val userId: Int, val username: String, val hash: String, val isAdmin: Boolean)

  val db: Database = Database.forURL("jdbc:postgresql://localhost/many_tasks", user = "many_tasks_user", driver = "org.postgresql.Driver")

  class AuthTable(tag: Tag) extends Table[Auth](tag, "auth") {
    def authId   = column[Int]("authid", PrimaryKey)
    def username = column[String]("username")
    def hash     = column[String]("hash")
    def isAdmin  = column[Boolean]("isadmin")

    def * =   (authId, username, hash, isAdmin) <> ((Auth.apply _).tupled, Auth.unapply)
  }

  val users = TableQuery[AuthTable]

  object AuthDAO {
    def getUsers() (implicit e :ExecutionContext): Future[Seq[Auth]] = {
      db.run(users.result)
    }
  }
}