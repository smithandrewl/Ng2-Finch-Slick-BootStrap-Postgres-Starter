package org.smithandrewl.starter.db.dao

import org.mindrot.jbcrypt.BCrypt
import org.smithandrewl.starter.auth
import org.smithandrewl.starter.auth.{AuthFailure, AuthSuccess, AuthenticationResult}
import org.smithandrewl.starter.db._
import org.smithandrewl.starter.db.mapping._
import org.smithandrewl.starter.model.Auth
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

object AuthDAO {

  def insertUser(username: String, hash: String, isAdmin: Boolean): Future[Int] = {
    db.run(users += Auth(0, username, BCrypt.hashpw(hash, BCrypt.gensalt(10)), isAdmin))
  }
  def deleteUser(id: Int): Future[Int] = {
    db.run(users.filter(_.authId === id).delete)
  }

  def getUsers()(implicit e: ExecutionContext): Future[Seq[Auth]] = {
    db.run(users.result)
  }

  def login(username: String, pw: String): Future[AuthenticationResult] = {

    val query = users.filter(user => user.username === username).
      map(usr     => (usr.hash, usr.isAdmin, usr.authId))

    db.run(query.result).map {
      case Vector(row) => BCrypt.checkpw(pw, row._1) match {
        case true => AuthSuccess(auth.grantJWT(row._3, row._2))
        case _ => AuthFailure
      }
      case _ => AuthFailure
    }
  }
}