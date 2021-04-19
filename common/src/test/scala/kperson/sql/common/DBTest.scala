package kperson.sql.common

import com.dimafeng.testcontainers.{ForEachTestContainer, MultipleContainers, MySQLContainer, PostgreSQLContainer}
import kperson.sqlh.common.{DataSource, LoadDrivers, Masked, UsernamePassword}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

import java.sql.{Connection, DriverManager}

class DBTest extends AnyFlatSpec with ForEachTestContainer with BeforeAndAfter with Matchers {

  val mysqlContainer: MySQLContainer = MySQLContainer()
  val postgresContainer: PostgreSQLContainer = PostgreSQLContainer()

  override val container: MultipleContainers = MultipleContainers(mysqlContainer, postgresContainer)

  before {
    LoadDrivers()
  }

  def foreachDB(dbFun: (DataSource, DatabaseVendor) => Any): Unit = {
    val mysql: (DataSource, DatabaseVendor) = (DataSource(mysqlContainer.jdbcUrl, Some(UsernamePassword(mysqlContainer.username, Masked(mysqlContainer.password)))), MySQL)
    val postgres: (DataSource, DatabaseVendor) = (DataSource(postgresContainer.jdbcUrl, Some(UsernamePassword(postgresContainer.username, Masked(postgresContainer.password)))), Postgres)
    List(mysql, postgres).foreach { case (source, vendor) =>
      dbFun(source, vendor)
    }
  }

}