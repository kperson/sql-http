package kperson.sql.common

import kperson.sqlh.common.ExecuteBatchWrite._
import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers


class BatchSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE auto_table (
      |  id INT NOT NULL AUTO_INCREMENT,
      |  name TEXT,
      |  PRIMARY KEY (id)
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE auto_table (
      |  id SERIAL,
      |  name TEXT
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Batch" should "write" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val params = (1 to 10).map { _ =>
        Map("name" -> PString("bob"))
      }.toList
      val update = BatchWrite(Direct(dataSource), "INSERT INTO auto_table (name) VALUES (:name)", params).run()
      update.numberOfAffectedRows.size shouldBe(10)
      update.numberOfAffectedRows.foreach { num =>
        num shouldBe 1
      }
    }
  }

}
