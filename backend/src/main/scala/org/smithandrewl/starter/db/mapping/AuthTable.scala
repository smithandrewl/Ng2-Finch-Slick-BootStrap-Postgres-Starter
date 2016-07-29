package org.smithandrewl.starter.db.mapping

import org.smithandrewl.starter.model.Auth
import slick.ast.ColumnOption.{AutoInc, PrimaryKey}
import slick.driver.PostgresDriver.api._

/**
  * Slick database table mapping for the [[org.smithandrewl.starter.model.Auth Auth]] class
  * @param tag
  */
class AuthTable(tag: Tag) extends Table[Auth](tag, "auth") {
  def authId   = column[Int]    ("authid", PrimaryKey, AutoInc)
  def username = column[String] ("username")
  def hash     = column[String] ("hash")
  def isAdmin  = column[Boolean]("isadmin")

  def * = (authId, username, hash, isAdmin) <>((Auth.apply _).tupled, Auth.unapply)
}
