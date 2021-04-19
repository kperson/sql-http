package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers


class StringSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE string_table (
      |  varchar_col VARCHAR(10) NULL,
      |  char_col VARCHAR(10) NULL,
      |  text_col TEXT NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE string_table (
      |  varchar_col VARCHAR(10) NULL,
      |  char_col CHAR(10) NULL,
      |  text_col TEXT NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "String" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val connection = dataSource.createConnection()
      val sql = createSQl.sql(vendor)
      connection.prepareStatement(sql).execute()
      val insert = """
        INSERT INTO string_table (varchar_col, char_col, text_col)
        VALUES (:varchar_col, :char_col, :text_col)
      """

      val abc = List("a", "b", "c")
      val params = Map(
        "varchar_col" -> PString(abc(0)),
        "char_col" -> PString(abc(1)),
        "text_col" -> PString(abc(2))
      )
      ExecuteWrite(connection, Write(insert, params))

      val select = "SELECT * FROM string_table"
      val result = ExecuteQuery(connection, Query(select)).toList
      result.size shouldBe 1
      result.head.columns.zipWithIndex.foreach { case (col: Column, index: Int)  =>
          col.value shouldBe a [PString]
          val str = col.value.asInstanceOf[PString].value
          str.trim shouldBe abc(index)
      }
    }
  }

}
