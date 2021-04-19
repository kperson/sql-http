package kperson.sql.common

import com.dimafeng.testcontainers._
import kperson.sqlh.common.{DataSource, LoadDrivers, Masked, UsernamePassword}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._


class DBTest extends AnyFlatSpec with ForEachTestContainer with BeforeAndAfter with Matchers {

  val mysqlContainer: MySQLContainer = MySQLContainer()
  val postgresContainer: PostgreSQLContainer = PostgreSQLContainer()
  val mariaContainer: MariaDBContainer = MariaDBContainer()

  override val container: MultipleContainers = MultipleContainers(mysqlContainer, postgresContainer, mariaContainer)

  before {
    LoadDrivers()
  }

  def foreachDB(dbFun: (DataSource, DatabaseVendor) => Any): Unit = {
    val mysql: (DataSource, DatabaseVendor) = (DataSource(mysqlContainer.jdbcUrl, Some(UsernamePassword(mysqlContainer.username, Masked(mysqlContainer.password)))), MySQL)
    val postgres: (DataSource, DatabaseVendor) = (DataSource(postgresContainer.jdbcUrl, Some(UsernamePassword(postgresContainer.username, Masked(postgresContainer.password)))), Postgres)
    val mariaDB: (DataSource, DatabaseVendor) = (DataSource(mariaContainer.jdbcUrl, Some(UsernamePassword(mariaContainer.username, Masked(mariaContainer.password)))), MariaDB)

    List(mysql, mariaDB, postgres).foreach { case (source, vendor) =>
      dbFun(source, vendor)
    }
  }

}