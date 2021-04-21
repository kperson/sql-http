package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers

import java.text.SimpleDateFormat
import scala.math.abs

import ExecuteQuery._
import ExecuteWrite._


class DateSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE date_table (
      |  timestamp_col TIMESTAMP NULL,
      |  datetime_col DATETIME(6) NULL,
      |  date_col DATE NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE date_table (
      |  timestamp_col TIMESTAMPTZ NULL,
      |  datetime_col TIMESTAMP NULL,
      |  date_col DATE NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Date" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO date_table (timestamp_col, datetime_col, date_col)
        VALUES (:timestamp_col, :datetime_col, :date_col)
      """
      val today = new java.util.Date()

      SQLPrimitive.formats.parseFormats.foreach { format =>
        val params = Map(
          "timestamp_col" -> PDate(format.format(today)),
          "datetime_col" -> PDate(format.format(today)),
          "date_col" -> PDate(SQLPrimitive.formats.dateFormat.format(today))
        )
        Write(Direct(dataSource), insert, params).run()
        val select = "SELECT * FROM date_table"
        val result = Query(Direct(dataSource), select).run()
        Write(Direct(dataSource), "DELETE FROM date_table").run()

        result.size shouldBe 1
        result.head.columns.foreach { col  =>
          col.value shouldBe a [PDate]
          if (col.label == "date_col") {
            val dateStr = col.value.asInstanceOf[PDate].value
            val noTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000")
            val todayStr = noTimeFormatter.format(today)
            todayStr shouldBe dateStr
          }
          else if (format.toPattern != "yyyy-MM-dd") {
            val date = SQLPrimitive.formats.timestampFormat.parse(col.value.asInstanceOf[PDate].value)
            abs(today.getTime - date.getTime) should be < 1000L
          }
        }
      }
    }
  }

}
