package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers


class TimeSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE time_table (
      |  time_col TIME NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE time_table (
      | time_col TIME NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Times" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val connection = dataSource.createConnection()
      val sql = createSQl.sql(vendor)
      connection.prepareStatement(sql).execute()
      val times = {
        List(
          (3600 * 1000 * 34, Set[DatabaseVendor](MySQL, MariaDB)),
          (3600 * 1000 * 8, Set[DatabaseVendor](MySQL, MariaDB, Postgres))
        )
      }

      times.filter { a => a._2.contains(vendor) }
      .map  { a => a._1 }
      .foreach { time =>
        val insert =
          """
          INSERT INTO time_table (time_col)
          VALUES (:time_col)
        """

        val params = Map(
          "time_col" -> PTime(time),
        )
        ExecuteWrite(connection, Write(insert, params))
        val select = "SELECT * FROM time_table"
        val result = ExecuteQuery(connection, Query(select)).toList
        ExecuteWrite(connection, Write("DELETE FROM time_table"))

        result.size shouldBe 1
        result.head.columns.foreach { col =>
          col.value shouldBe a[PTime]
          val dbTime = col.value.asInstanceOf[PTime]
          dbTime.value shouldBe time
        }
      }
    }
  }

}
