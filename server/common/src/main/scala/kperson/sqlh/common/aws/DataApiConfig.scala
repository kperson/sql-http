package kperson.sqlh.common.aws

import kperson.sqlh.common.{DataSource, Masked, UsernamePassword}

case class DataApiConfig(engine: String, host: String, username: Option[String], password: Option[String], port: Option[Int]) {
  def isMySQL: Boolean = List("aurora-mysql", "aurora", "mysql").contains(engine.toLowerCase)

  def isPostgres: Boolean = List("aurora-postgresql", "postgres").contains(engine.toLowerCase)

  def isMariaBD: Boolean = List("mariadb").contains(engine.toLowerCase)

  def toDataSource(db: String): DataSource = {
    val usernamePassword = (username, password) match {
      case (Some(u), Some(p)) => Some(UsernamePassword(u, Masked(p)))
      case _ => None
    }
    if (isMySQL) {
      DataSource(s"jdbc:mysql://$host:${port.getOrElse(3306)}/$db", usernamePassword, None)
    }
    else if (isMariaBD) {
      DataSource(s"jdbc:mariadb://$host:${port.getOrElse(3306)}/$db", usernamePassword, None)
    }
    else {
      DataSource(s"jdbc:postgresql://$host:${port.getOrElse(3306)}/$db", usernamePassword, None)
    }

  }

}
