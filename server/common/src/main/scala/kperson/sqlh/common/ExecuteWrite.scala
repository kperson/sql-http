package kperson.sqlh.common

import kperson.sqlh.common.SQLValue._
import org.sql2o.Sql2o

object ExecuteWrite {

  implicit class WriteExecuteExtension(command: Write) {
    def run(resolvers: List[DataSourceReferenceResolver] = List()): WriteResponse = {
      val dataSource = ConnectionPool.getJDBCDataSource(resolvers, command.dataSourceReference)
      val sql2o = new Sql2o(dataSource).open()
      try {
        val query = sql2o.createQuery(command.sql, true)
        query.populateStatement(command.params, sql2o.getJdbcConnection)
        val update = query.executeUpdate()
        val keys = update.getKeys.flatMap { value: AnyRef =>
          Option(value).map { _.toString }
        }.toList
        WriteResponse(update.getResult, keys)
      }
      finally {
        sql2o.close()
      }
    }
  }
}

