package kperson.sql.common

import kperson.sqlh.common.{Column, ExecuteQuery, ExecuteWrite, PLong, Query, Write}
import org.scalatest.matchers.should.Matchers
import kperson.sqlh.common.Postgres


class IntegerSpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE int_table (
      |  big_int_col BIGINT NULL,
      |  int_col INT NULL,
      |  medium_int_col MEDIUMINT NULL,
      |  small_int_col SMALLINT NULL,
      |  tiny_int_col TINYINT NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE int_table (
      |  big_int_col BIGINT NULL,
      |  int_col INT NULL,
      |  medium_int_col SMALLINT NULL,
      |  small_int_col SMALLINT NULL,
      |  tiny_int_col SMALLINT NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Integers" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val connection = dataSource.createConnection()
      val sql = createSQl.sql(vendor)
      connection.prepareStatement(sql).execute()
      val insert = """
        INSERT INTO int_table (big_int_col, int_col, medium_int_col, small_int_col, tiny_int_col)
        VALUES (:big_int_col, :int_col, :medium_int_col, :small_int_col, :tiny_int_col)
      """
      val params = Map(
        "big_int_col" -> PLong(1),
        "int_col" -> PLong(2),
        "medium_int_col" -> PLong(3),
        "small_int_col" -> PLong(4),
        "tiny_int_col" -> PLong(5)
      )
      ExecuteWrite(connection, Write(insert, params))
      val select =
        """
          |SELECT * FROM int_table
          |WHERE big_int_col = :big_int_col
          |AND int_col = :int_col
          |AND medium_int_col = :medium_int_col
          |AND small_int_col = :small_int_col
          |AND tiny_int_col = :tiny_int_col
          |""".stripMargin
      val result = ExecuteQuery(connection, Query(select, params)).toList
      result.size shouldBe 1
      result.head.columns.zipWithIndex.foreach { case (col: Column, index: Int) =>
        col.value shouldBe a [PLong]
        val long = col.value.asInstanceOf[PLong]
        long.value shouldBe index + 1
      }
    }
  }

}
