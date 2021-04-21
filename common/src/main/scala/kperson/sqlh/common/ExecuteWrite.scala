package kperson.sqlh.common

import kperson.sqlh.common.Row.ResultSetJDBC
import kperson.sqlh.common.SQLPrimitive._
import org.sql2o.tools.NamedParameterStatement


object ExecuteWrite {

  implicit class WriteExecuteExtension(command: Write) {
    def run(resolvers: List[DataSourceReferenceResolver] = List()): WriteResponse = {
      val connection = ConnectionPool.getConnection(resolvers, command.dataSourceReference)
      try {
        val statement = new NamedParameterStatement(connection, command.sql, true)
        statement.populateStatement(command.params)
        val numberOfAffectedRows = statement.executeUpdate()
        val autoGeneratedKeys = statement.getStatement.getGeneratedKeys.toRows().map { r =>
          r.columns.head.value
        }.toList
        WriteResponse(numberOfAffectedRows, autoGeneratedKeys)
      }
      finally {
        connection.close()
      }
    }
  }
}

