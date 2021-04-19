package kperson.sql.common

import org.scalatest.flatspec.AnyFlatSpec
import kperson.sqlh.common.Serialization._
import kperson.sqlh.common._
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

  it should "write one off transaction references" in {
    val reference: TransactionReference = OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    val json = write(reference)
    val expectedJSON = """{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}"""
    json shouldBe expectedJSON
  }

  it should "read one off transaction references" in {
    val json = """{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}"""
    val reference = read[TransactionReference](json)
    val expectedReference: TransactionReference = OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    reference shouldBe expectedReference
  }

  it should "write transaction id transaction references" in {
    val reference: TransactionReference = TransactionId("abc")
    val json = write(reference)
    val expectedJSON = """{"dataType":"TransactionId","data":"abc"}"""
    json shouldBe expectedJSON
  }

  it should "read transaction id transaction references" in {
    val json = """{"dataType":"TransactionId","data":"abc"}"""
    val reference = read[TransactionReference](json)
    val expectedReference: TransactionReference = TransactionId("abc")
    reference shouldBe expectedReference
  }

  it should "write custom transaction references" in {
    val reference: TransactionReference = Custom("abc")
    val json = write(reference)
    val expectedJSON = """{"dataType":"Custom","data":"abc"}"""
    json shouldBe expectedJSON
  }

  it should "read custom transaction references" in {
    val json = """{"dataType":"Custom","data":"abc"}"""
    val reference = read[TransactionReference](json)
    val expectedReference: TransactionReference = Custom("abc")
    reference shouldBe expectedReference
  }

  it should "write begin transaction sql command" in {
    val command: SQLCommand = BeginTransaction(DataSource("jdbc:mysql://localhost:3306/mydb"))
    val json = write(command)
    val expectedJSON = """{"dataType":"BeginTransaction","data":{"dataSource":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}"""
    json shouldBe expectedJSON
  }

  it should "read begin transaction sql command" in {
    val json = """{"dataType":"BeginTransaction","data":{"dataSource":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = BeginTransaction(DataSource("jdbc:mysql://localhost:3306/mydb"))
    command shouldBe expectedCommand
  }


  it should "write commit sql command" in {
    val command: SQLCommand = Commit("abc")
    val json = write(command)
    val expectedJSON = """{"dataType":"Commit","data":{"transactionId":"abc"}}"""
    json shouldBe expectedJSON
  }

  it should "read commit sql command" in {
    val json = """{"dataType":"Commit","data":{"transactionId":"abc"}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Commit("abc")
    command shouldBe expectedCommand
  }

  it should "write rollback sql command" in {
    val command: SQLCommand = Rollback("abc")
    val json = write(command)
    val expectedJSON = """{"dataType":"Rollback","data":{"transactionId":"abc"}}"""
    json shouldBe expectedJSON
  }

  it should "read rollback sql command" in {
    val json = """{"dataType":"Rollback","data":{"transactionId":"abc"}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Rollback("abc")
    command shouldBe expectedCommand
  }

  it should "read and write SQL Primitives" in {
    val primitives  = List[(SQLPrimitive, String)](
      (PInteger(3), """{"dataType":"Integer","data":3}"""),
      (PLong(4), """{"dataType":"Long","data":4}"""),
      (NullP, """{"dataType":"Null"}""")
    )
    primitives.foreach { case (primitive, expectedJSON) =>
      val json = write(primitive)
      json shouldBe expectedJSON
      val readPrimitive = read[SQLPrimitive](expectedJSON)
      readPrimitive shouldBe primitive
    }
  }

  it should "write query sql command" in {
    val command: SQLCommand = Query(
      "SELECT * FROM test_table",
      Map(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    val json = write(command)
    println(json)
    val expectedJSON = """{"dataType":"Query","data":{"sql":"SELECT * FROM test_table","params":{},"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    json shouldBe expectedJSON
  }

  it should "read query sql command" in {
    val json = """{"dataType":"Query","data":{"sql":"SELECT * FROM test_table","params":{},"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Query(
      "SELECT * FROM test_table",
      Map(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    command shouldBe expectedCommand
  }

  it should "write write sql command" in {
    val command: SQLCommand = Write(
      "UPDATE test_table SET col = 1",
      Map(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    val json = write(command)
    val expectedJSON = """{"dataType":"Write","data":{"sql":"UPDATE test_table SET col = 1","params":{},"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    json shouldBe expectedJSON
  }

  it should "read write sql command" in {
    val json = """{"dataType":"Write","data":{"sql":"UPDATE test_table SET col = 1","params":{},"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = Write(
      "UPDATE test_table SET col = 1",
      Map(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    command shouldBe expectedCommand
  }

  it should "write batch write sql command" in {
    val command: SQLCommand =  BatchWrite(
      "UPDATE test_table SET col = 1",
      List(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    val json = write(command)
    val expectedJSON = """{"dataType":"BatchWrite","data":{"sql":"UPDATE test_table SET col = 1","params":[],"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    json shouldBe expectedJSON
  }

  it should "read batch write sql command" in {
    val json = """{"dataType":"BatchWrite","data":{"sql":"UPDATE test_table SET col = 1","params":[],"transactionReference":{"dataType":"OneOff","data":{"jdbcURL":"jdbc:mysql://localhost:3306/mydb"}}}}"""
    val command = read[SQLCommand](json)
    val expectedCommand: SQLCommand = BatchWrite(
      "UPDATE test_table SET col = 1",
      List(),
      OneOff(DataSource("jdbc:mysql://localhost:3306/mydb"))
    )
    command shouldBe expectedCommand
  }

}