package kperson.sqlh.common

import java.sql.{Connection, DriverManager}
import java.util.Properties

case class UsernamePassword(username: String, password: Masked)
case class DataSource(jdbcURL: String, credentials: Option[UsernamePassword] = None, properties: Option[Map[String, Any]] = None) {

  def createConnection(): Connection = {
    (credentials, properties) match {
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