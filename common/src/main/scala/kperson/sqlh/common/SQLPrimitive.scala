package kperson.sqlh.common

import org.sql2o.tools.NamedParameterStatement

import java.sql.{Timestamp, Types}
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import scala.util.{Failure, Success, Try}

sealed trait SQLPrimitive

case class PShort(value: Short) extends SQLPrimitive
case class PInteger(value: Int) extends SQLPrimitive
case class PLong(value: Long) extends SQLPrimitive
case class PString(value: String) extends SQLPrimitive
case class PDate(value: String) extends SQLPrimitive
case object NullP extends SQLPrimitive

object SQLPrimitive {

  val formats = new DateFormats()

  class DateFormats {
    val timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    private val parseFormats = List(
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"),
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
        case PShort(v) => statement.setInt(name, v)
        case PInteger(v) => statement.setInt(name, v)
        case PLong(v) => statement.setLong(name, v)
        case PString(v) => statement.setString(name, v)
        case PDate(v) => statement.setTimestamp(name, Timestamp.from(Instant.ofEpochMilli(formats.parse(v).getTime)))
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