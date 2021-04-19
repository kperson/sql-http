package kperson.sqlh.common

import java.sql.{Connection, DriverManager}
import java.util.Properties
import DataSource._

object ExecuteBeginTransaction {

  def apply(command: BeginTransaction): Connection = {
    val connection = command.toConnection()
    //connection.setAutoCommit(false)
    connection
  }

  implicit class BeginTransactionJDBC(command: BeginTransaction) {
    def toConnection(): Connection = {
      command.dataSource.createConnection()
    }
  }

}
