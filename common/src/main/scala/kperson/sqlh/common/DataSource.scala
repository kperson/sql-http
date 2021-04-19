package kperson.sqlh.common

case class UsernamePassword(username: String, password: Masked)
case class DataSource(jdbcURL: String, credentials: Option[UsernamePassword] = None, properties: Option[Map[String, Any]] = None)