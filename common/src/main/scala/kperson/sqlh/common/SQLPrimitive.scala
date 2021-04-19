package kperson.sqlh.common

import org.sql2o.tools.NamedParameterStatement

import java.io.ByteArrayInputStream
import java.sql.{Timestamp, Types}
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.{Failure, Success, Try}

sealed trait SQLPrimitive

case class PLong(value: Long) extends SQLPrimitive
case class PString(value: String) extends SQLPrimitive
case class PDate(value: String) extends SQLPrimitive
case class PBlob(value: String) extends SQLPrimitive
case object NullP extends SQLPrimitive

object SQLPrimitive {

  val formats = new DateFormats()

  class DateFormats {
    val timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    val parseFormats = List(
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
      timestampFormat,
      dateFormat
    )

    def parse(str: String): Date = {
      if (!str.contains("T")) {
        Try(dateFormat.parse(str)) match {
          case Success(date) => date
          case Failure(_) => parse(str, parseFormats)
        }
      }
      else {
          parse(str, parseFormats)
      }
    }

    private def parse(str: String, formats: List[SimpleDateFormat]): Date = {
      formats match {
        case head :: tail => {
          Try(head.parse(str)) match {
            case Success(date) => date
            case Failure(_) => parse(str, tail)
          }
        }
        case Nil => throw new RuntimeException(s"unable to convert $str to a date")
      }
    }
  }


  implicit class NamedParameterStatementJDBC(statement: NamedParameterStatement) {
    def populateStatement(name: String, value: SQLPrimitive)  {
      value match {
        case PLong(v) => statement.setLong(name, v)
        case PString(v) => statement.setString(name, v)
        case PBlob(v) => statement.setInputStream(name, new ByteArrayInputStream(java.util.Base64.getDecoder.decode(v)))
        case PDate(v) => statement.setTimestamp(name, new Timestamp(formats.parse(v).getTime))
        case NullP => statement.setNull(name, Types.VARCHAR)
      }
    }

    def populateStatement(params: Map[String, SQLPrimitive])  {
      params.foreach { case (name, value) =>
        statement.populateStatement(name, value)
      }
    }
  }

}