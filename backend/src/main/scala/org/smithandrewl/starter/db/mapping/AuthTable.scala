package org.smithandrewl.starter.db.mapping

import org.smithandrewl.starter.model.Auth
import slick.ast.ColumnOption.PrimaryKey
import slick.driver.PostgresDriver.api._

class AuthTable(tag: Tag) extends Table[Auth](tag, "auth") {
  def authId   = column[Int]    ("authid", PrimaryKey)
  def username = column[String] ("username")
  def hash     = column[String] ("hash")
  def isAdmin  = column[Boolean]("isadmin")

  def * = (authId, username, hash, isAdmin) <>((Auth.apply _).tupled, Auth.unapply)
}
