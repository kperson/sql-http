package kperson.sqlh.common

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection
import scala.annotation.tailrec

class FailedCustomResolverException(custom: Custom) extends RuntimeException(s"failed to convert ${custom.value} to DataSource")

object ConnectionPool {

  private val pools = scala.collection.mutable.Map[DataSource, HikariDataSource]()

  def getConnection(resolvers: List[DataSourceReferenceResolver], reference: DataSourceReference): Connection = {
    getJDBCDataSource(resolvers, reference).getConnection
  }

  def getJDBCDataSource(resolvers: List[DataSourceReferenceResolver], reference: DataSourceReference): HikariDataSource = {
    reference match {
      case Direct(source) => getJDBCDataSource(source)
      case c: Custom => getJDBCDataSource(lookupDataSource(c, resolvers))
    }
  }

  def getDataSource(resolvers: List[DataSourceReferenceResolver], reference: DataSourceReference): DataSource = {
    reference match {
      case Direct(source) => source
      case c: Custom => lookupDataSource(c, resolvers)
    }
  }

  @tailrec def lookupDataSource(custom: Custom, resolvers: List[DataSourceReferenceResolver]): DataSource = {
    resolvers match {
      case head :: tail => head.convertCustomToDataSource(custom) match {
        case Some(d) => d
        case _ => lookupDataSource(custom, tail)
      }
      case Nil => throw new FailedCustomResolverException(custom)
    }
  }

  def getConnection(dataSource: DataSource): Connection = {
    getJDBCDataSource(dataSource).getConnection
  }

  def getJDBCDataSource(dataSource: DataSource): HikariDataSource = {
    pools.get(dataSource) match {
      case Some(source) => source
      case _ =>
        this.synchronized {
          val source = new HikariDataSource(createHikariConfig(dataSource))
          pools(dataSource) = source
          source
        }
    }
  }

  private def createHikariConfig(dataSource: DataSource): HikariConfig = {
    val config = new HikariConfig()
    config.setJdbcUrl(dataSource.jdbcURL)
    dataSource.credentials.foreach { c =>
      config.setUsername(c.username)
      config.setPassword(c.password.value)
    }
    dataSource.properties.foreach { m =>
      m.foreach { case (key, value) =>
        config.addDataSourceProperty(key, value)
      }
    }
    config

  }

}
