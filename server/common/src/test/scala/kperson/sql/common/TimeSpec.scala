package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers
import ExecuteQuery._
import ExecuteWrite._

import java.time.{Instant, ZoneId, ZonedDateTime}


class TimeSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE time_table (
      |  time_col TIME NULL,
      |  time_tz TIME NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE time_table (
      | time_col TIME NULL,
      | time_tz time with time zone
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Times" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
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
          INSERT INTO time_table (time_col, time_tz)
          VALUES (:time_col, :time_tz)
        """

        val params = Map(
          "time_col" -> PTime(time),
          "time_tz" -> PTime(time)
        )
        Write(Direct(dataSource), insert, params).run()
        val select = "SELECT * FROM time_table"
        val result = Query(Direct(dataSource), select).run()
        Write(Direct(dataSource), "DELETE FROM time_table").run()

        result.size shouldBe 1
        result.head.columns.foreach { col =>
          col.value shouldBe a[PTime]
          val dbTime = col.value.asInstanceOf[PTime]
          if (col.name == "time_tz" && vendor == Postgres) {
            val adjusted = dbTime.value + ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault()).getOffset.getTotalSeconds * 1000
            adjusted shouldBe time
          }
          else {
            dbTime.value shouldBe time
          }
        }
      }
    }
  }

}
