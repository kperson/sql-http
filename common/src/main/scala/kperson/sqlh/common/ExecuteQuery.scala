package kperson.sqlh.common

import org.sql2o.tools.NamedParameterStatement
import java.sql.Connection

import SQLPrimitive._
import Row.ResultSetJDBC

object ExecuteQuery {

  def apply(connection: Connection, command: Query): LazyList[Row] = {
    val statement = new NamedParameterStatement(connection, command.sql, false)
    statement.populateStatement(command.params)
    statement.executeQuery().toRows()
  }
}
