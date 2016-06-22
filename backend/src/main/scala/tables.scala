import slick.ast.ColumnOption.PrimaryKey
import slick.driver.PostgresDriver.api._

import slick.lifted.{TableQuery, Tag}

import scala.concurrent.{ExecutionContext, Future}

object tables {
  val db: Database = Database.forURL(
    url    = "jdbc:postgresql://localhost/many_tasks",
    user   = "many_tasks_user",
    driver = "org.postgresql.Driver"
  )

  /**************** Model Classes ***********************************************/
  case class Auth(userId: Int, username: String, hash: String, isAdmin: Boolean)

  /*************** Table Classes **********************************************/
  class AuthTable(tag: Tag) extends Table[Auth](tag, "auth") {
    def authId   = column[Int]("authid", PrimaryKey)
    def username = column[String]("username")
    def hash     = column[String]("hash")
    def isAdmin  = column[Boolean]("isadmin")

    def * = (authId, username, hash, isAdmin) <>((Auth.apply _).tupled, Auth.unapply)
  }

  val users = TableQuery[AuthTable]

  /************* DAO Classes **************************************************/
  object AuthDAO {
    def getUsers()(implicit e: ExecutionContext): Future[Seq[Auth]] = {
      db.run(users.result)
    }

    def verifyUser(username: String, hash: String): Future[Boolean] = {
      return db.run(users.filter(user => user.username === username).filter(user => user.hash === hash).exists.result)
    }
  }
}