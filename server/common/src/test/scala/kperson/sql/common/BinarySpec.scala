package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers

import java.util.Base64

import ExecuteQuery._
import ExecuteWrite._


class BinarySpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE binary_table (
      |  blob_col BLOB NULL,
      |  med_col MEDIUMBLOB NULL,
      |  tiny_col TINYBLOB NULL,
      |  binary_col BINARY(200) NULL
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE binary_table (
      |  blob_col BYTEA NULL,
      |  med_col BYTEA NULL,
      |  tiny_col BYTEA NULL,
      |  binary_col BYTEA NULL
      |);
      |""".stripMargin

  private val createSQl = TestSQL(defaultCreate, Map(Postgres -> postgresCreate))

  "Binary" should "read and write to DBs" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO binary_table (blob_col, med_col, tiny_col, binary_col)
        VALUES (:blob_col, :med_col, :tiny_col, :binary_col)
      """
      val text = "hello world, how are you today?, im fine."
      val blobParam =  PBlob(Base64.getEncoder.encodeToString(text.getBytes))
      val params = Map(
        "blob_col" -> blobParam,
        "med_col" -> blobParam,
        "tiny_col" -> blobParam,
        "binary_col" -> blobParam
      )
      val results = Write(Direct(dataSource), insert, params).run()
      results.numberOfAffectedRows shouldBe 1
      val select = "SELECT * FROM binary_table"
      val result = Query(Direct(dataSource), select).run()
      result.size shouldBe 1
      result.head.columns.foreach { col =>
        col.value shouldBe a [PBlob]
        val blob = col.value.asInstanceOf[PBlob]
        val decodedText = new String(Base64.getDecoder.decode(blob.value)).trim
        decodedText shouldBe text
      }
    }
  }

  it should "write nulls" in {
    foreachDB { case (dataSource, vendor) =>
      val sql = createSQl.sql(vendor)
      ConnectionPool.getConnection(dataSource).prepareStatement(sql).execute()
      val insert = """
        INSERT INTO binary_table (blob_col, med_col, tiny_col, binary_col)
        VALUES (:blob_col, :med_col, :tiny_col, :binary_col)
      """
      val params = Map(
        "blob_col" -> PNull,
        "med_col" -> PNull,
        "tiny_col" -> PNull,
        "binary_col" -> PNull
      )
      val results = Write(Direct(dataSource), insert, params).run()
      results.numberOfAffectedRows shouldBe 1
      val select = "SELECT * FROM binary_table"
      val result = Query(Direct(dataSource), select).run()
      result.size shouldBe 1
      result.head.columns.size shouldBe 4
      result.head.columns.foreach { col =>
        col.value shouldBe PNull
      }
    }
  }

}
