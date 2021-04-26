package kperson.sqlh.common

import kperson.sqlh.common.SQLValue._
import org.sql2o.Sql2o


object ExecuteBatchWrite {

  implicit class BatchWriteExecuteExtension(command: BatchWrite) {
    def run(resolvers: List[DataSourceReferenceResolver] = List()): BatchWriteResponse = {
      val dataSource = ConnectionPool.getJDBCDataSource(resolvers, command.dataSourceReference)
      val sql2o = new Sql2o(dataSource).open()
      try {
        val query = sql2o.createQuery(command.sql, true)
        command.params.foreach { entry =>
          query.populateStatement(entry, sql2o.getJdbcConnection)
          query.addToBatch()
        }
        val update = query.executeBatch()
        BatchWriteResponse(update.getBatchResult.toList)
      }
      finally {
        sql2o.close()
      }
    }
  }
}

