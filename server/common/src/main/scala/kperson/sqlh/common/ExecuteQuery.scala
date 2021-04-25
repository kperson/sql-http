package kperson.sqlh.common

import SQLValue._
import Row.ResultSetJDBC
import org.sql2o.{ResultSetHandler, Sql2o}
import scala.jdk.CollectionConverters._

import java.sql.ResultSet

object ExecuteQuery {

  implicit class ExecuteQueryExtension(command: Query) {

    def run(resolvers: List[DataSourceReferenceResolver] = List()): List[Row] = {
      val dataSource = ConnectionPool.getJDBCDataSource(resolvers, command.dataSourceReference)
      val sql2o = new Sql2o(dataSource).open()
      try {
        val query = sql2o.createQuery(command.sql)
        query.populateStatement(command.params, sql2o.getJdbcConnection)
        query.executeAndFetch(new ResultSetHandler[Row] {
          override def handle(resultSet: ResultSet): Row = {
            resultSet.toRow()
          }
        }).asScala.toList
      }
      finally {
        sql2o.close()
      }
    }

  }
}
