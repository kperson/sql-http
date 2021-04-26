package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.flatspec.AnyFlatSpec
import kperson.sqlh.common.Serialization.Formats._
import org.json4s.jackson.Serialization.{read, write}
import org.scalatest.matchers.should._


class SerializationSpec extends AnyFlatSpec with Matchers {

  "Serialization" should "write masked values" in {
    val masked = Masked("my.secret.password")
    val json = write(masked)
    val expectedJSON = """"my.secret.password""""
    json shouldBe expectedJSON
  }

  it should "read masked values" in {
    val json = """"my.secret.password""""
    val masked = read[Masked](json)
    val expectedMasked = Masked("my.secret.password")
    masked shouldBe expectedMasked
  }

  it should "write direct data source references" in {
    val reference: DataSourceReference = Direct(DataSource("jdbc:mysql://localhost:3306/mydb"))
    val json = write(reference)
    val expectedJSON = """{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}"""
    json shouldBe expectedJSON
  }

  it should "read direct data source references" in {
    val json = """{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}"""
    val reference = read[DataSourceReference](json)
    val expectedReference: DataSourceReference = Direct(DataSource("jdbc:mysql://localhost:3306/mydb"))
    reference shouldBe expectedReference
  }

  it should "write custom transaction references" in {
    val reference: DataSourceReference = Custom("abc")
    val json = write(reference)
    val expectedJSON = """{"dataType":"Custom","data":"abc"}"""
    json shouldBe expectedJSON
  }

  it should "read custom transaction references" in {
    val json = """{"dataType":"Custom","data":"abc"}"""
    val reference = read[DataSourceReference](json)
    val expectedReference: DataSourceReference = Custom("abc")
    reference shouldBe expectedReference
  }

  it should "read and write SQL Primitives" in {
    val primitives  = List[(SQLValue, String)](
      (PLong(4), """{"dataType":"Long","data":4}"""),
      (PNull, """{"dataType":"Null"}"""),
      (PString("abc"), """{"dataType":"String","data":"abc"}"""),
      (PTime(2), """{"dataType":"Time","data":2}"""),
      (PBlob("aGVsbG8gd29ybGQ="), """{"dataType":"Blob","data":"aGVsbG8gd29ybGQ="}"""),
      (PDate("2020-03-21"), """{"dataType":"Date","data":"2020-03-21"}"""),
      (PDouble(3.14), """{"dataType":"Double","data":3.14}"""),
      (PDecimal("3.14"), """{"dataType":"Decimal","data":"3.14"}""")
    )
    primitives.foreach { case (primitive, expectedJSON) =>
      val json = write(primitive)
      json shouldBe expectedJSON
      val readPrimitive = read[SQLValue](expectedJSON)
      readPrimitive shouldBe primitive
    }
  }

  it should "write query sql command" in {
    val command: SQLCommand = Query(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "SELECT * FROM test_table"
    )
    val json = write(command)
    val expectedJSON = """{"dataType":"Query","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"SELECT * FROM test_table","params":{}}}"""
    json shouldBe expectedJSON
  }

  it should "read query sql command" in {
    val json = """{"dataType":"Query","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"SELECT * FROM test_table","params":{}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Query(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "SELECT * FROM test_table"
    )
    command shouldBe expectedCommand
  }

  it should "write write sql command" in {
    val command: SQLCommand = Write(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "UPDATE test_table SET col = 1",
    )
    val json = write(command)
    val expectedJSON = """{"dataType":"Write","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"UPDATE test_table SET col = 1","params":{}}}"""
    json shouldBe expectedJSON
  }

  it should "read write sql command" in {
    val json = """{"dataType":"Write","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"UPDATE test_table SET col = 1","params":{}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Write(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "UPDATE test_table SET col = 1"
    )
    command shouldBe expectedCommand
  }

  it should "write batch write sql command" in {
    val command: SQLCommand =  BatchWrite(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "UPDATE test_table SET col = 1"
    )
    val json = write(command)
    val expectedJSON = """{"dataType":"BatchWrite","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"UPDATE test_table SET col = 1","params":[]}}"""
    json shouldBe expectedJSON
  }

  it should "read batch write sql command" in {
    val json = """{"dataType":"BatchWrite","data":{"dataSourceReference":{"dataType":"Direct","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}},"sql":"UPDATE test_table SET col = 1","params":[]}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = BatchWrite(
      Direct(DataSource("jdbc:mysql://localhost:3306/mydb")),
      "UPDATE test_table SET col = 1"
    )
    command shouldBe expectedCommand
  }

}