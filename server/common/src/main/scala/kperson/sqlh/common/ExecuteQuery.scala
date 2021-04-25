package kperson.sqlh.common

import org.sql2o.tools.NamedParameterStatement

import SQLValue._
import Row.ResultSetJDBC

object ExecuteQuery {

  implicit class ExecuteQueryExtension(command: Query) {

    def run(resolvers: List[DataSourceReferenceResolver] = List()): List[Row] = {
      val connection = ConnectionPool.getConnection(resolvers, command.dataSourceReference)
      try {
        val statement = new NamedParameterStatement(connection, command.sql, false)
        statement.populateStatement(command.params)
        statement.executeQuery().toRows().toList
      }
      finally {
        connection.close()
      }
    }

  }
}
