package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers

import ExecuteQuery._
import ExecuteWrite._


class StringSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE string_table (
      |  varchar_col VARCHAR(10) NULL,
      |  char_col VARCHAR(10) NULL,
      |  text_col TEXT NULL,
      |  tiny_col TINYTEXT NULL,
      |  medium_col MEDIUMTEXT NULL,
      |  long_col LONGTEXT NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE string_table (
      |  varchar_col VARCHAR(10) NULL,
      |  char_col CHAR(10) NULL,
      |  text_col TEXT NULL,
      |  tiny_col TEXT NULL,
      |  medium_col TEXT NULL,
      |  long_col TEXT NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "String" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO string_table (varchar_col, char_col, text_col, tiny_col, medium_col, long_col)
        VALUES (:varchar_col, :char_col, :text_col, :tiny_col, :medium_col, :long_col)
      """

      val abc = List("a", "b", "c", "d", "e", "f")
      val params = Map(
        "varchar_col" -> PString(abc.head),
        "char_col" -> PString(abc(1)),
        "text_col" -> PString(abc(2)),
        "tiny_col" ->  PString(abc(3)),
        "medium_col" ->  PString(abc(4)),
        "long_col" ->  PString(abc(5))
      )
      Write(Direct(dataSource), insert, params).run()

      val select = "SELECT * FROM string_table"
      val result = Query(Direct(dataSource), select).run()
      result.size shouldBe 1
      result.head.columns.zipWithIndex.foreach { case (col: Column, index: Int)  =>
          col.value shouldBe a [PString]
          val str = col.value.asInstanceOf[PString].value
          str.trim shouldBe abc(index)
      }
    }
  }

  it should "write nulls" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO string_table (varchar_col, char_col, text_col, tiny_col, medium_col, long_col)
        VALUES (:varchar_col, :char_col, :text_col, :tiny_col, :medium_col, :long_col)
      """

      val params = Map(
        "varchar_col" -> PNull,
        "char_col" -> PNull,
        "text_col" -> PNull,
        "tiny_col" -> PNull,
        "medium_col" -> PNull,
        "long_col" -> PNull
      )
      Write(Direct(dataSource), insert, params).run()

      val select = "SELECT * FROM string_table"
      val result = Query(Direct(dataSource), select).run()
      result.size shouldBe 1
      result.head.columns.size shouldBe 6
      result.head.columns.foreach { col =>
        col.value shouldBe PNull
      }
    }
  }

}