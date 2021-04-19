package kperson.sqlh.common

import java.sql.{Connection, DriverManager}
import java.util.Properties

object ExecuteBeginTransaction {

  def apply(command: BeginTransaction): Connection = {
    val connection = command.toConnection()
    //connection.setAutoCommit(false)
    connection
  }

  implicit class BeginTransactionJDBC(command: BeginTransaction) {
    def toConnection(): Connection =  {
      val jdbcURL = command.dataSource.jdbcURL
      (command.dataSource.credentials, command.dataSource.properties) match {
        case (Some(c), _) => DriverManager.getConnection(jdbcURL, c.username, c.password.value)
        case (_, Some(p)) => {
          val properties = new Properties()
          p.foreach { case (key, value) =>
            properties.put(key, value)
          }
          DriverManager.getConnection(jdbcURL, properties)
        }
        case _ => DriverManager.getConnection(jdbcURL)
      }
    }
  }

}
