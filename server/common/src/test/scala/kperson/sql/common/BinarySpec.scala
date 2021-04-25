package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.matchers.should.Matchers

import java.util.Base64

import ExecuteQuery._
import ExecuteWrite._


class BinarySpec extends DBTest with Matchers {

  private val defaultCreate = """
      |CREATE TABLE binary_table (
      |  blob_col BLOB,
      |  med_col MEDIUMBLOB,
      |  tiny_col TINYBLOB,
      |  binary_col BINARY(200)
      |);
      |""".stripMargin

  private val postgresCreate = """
      |CREATE TABLE binary_table (
      |  blob_col BYTEA,
      |  med_col BYTEA,
      |  tiny_col BYTEA,
      |  binary_col BYTEA
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

}
