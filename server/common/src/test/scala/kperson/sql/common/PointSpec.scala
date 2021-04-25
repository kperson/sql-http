package kperson.sql.common

import kperson.sqlh.common.ExecuteQuery._
import kperson.sqlh.common.ExecuteWrite._
import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers
import scala.math.abs


class PointSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE point_table (
      |  dec_col DECIMAL(10, 4),
      |  double_col DOUBLE,
      |  float_col FLOAT
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE point_table (
      |  dec_col DECIMAL(10, 4),
      |  double_col double precision,
      |  float_col real
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Point" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO point_table (dec_col, double_col, float_col)
        VALUES (:dec_col, :double_col, :float_col)
      """
      val params = Map(
        "dec_col" -> PDecimal("3.1416"),
        "double_col" -> PDouble(3.1416),
        "float_col" -> PDouble(3.1416)
      )
      Write(Direct(dataSource), insert, params).run()
      val select = "SELECT * FROM point_table"
      val result = Query(Direct(dataSource), select).run()
      result.size shouldBe 1
      result.head.columns.foreach { col =>
        if(col.label == "dec_col") {
          val dec = col.value.asInstanceOf[PDecimal]
          BigDecimal(dec.value) shouldBe BigDecimal("3.1416")
        }
        else {
          val float = col.value.asInstanceOf[PDouble]
          abs(float.value - 3.1416) < 0.001 shouldBe true
        }
      }
    }
  }

}
