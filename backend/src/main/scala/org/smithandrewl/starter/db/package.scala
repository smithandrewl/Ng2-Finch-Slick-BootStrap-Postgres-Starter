package org.smithandrewl.starter

import com.twitter.logging.Logger
import slick.driver.PostgresDriver.api._

/**
  * Hold state for the database code.
  */
package object db {
  val logger = Logger("tables")

  val db: Database = Database.forURL(
    url    = "jdbc:postgresql://localhost/many_tasks",
    user   = "many_tasks_user",
    driver = "org.postgresql.Driver"
  )
}
