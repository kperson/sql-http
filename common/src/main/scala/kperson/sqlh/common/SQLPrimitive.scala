package kperson.sqlh.common

import org.sql2o.tools.NamedParameterStatement

import java.io.ByteArrayInputStream
import java.sql.{Time, Timestamp, Types}
import java.text.SimpleDateFormat
import java.util.Date
import scala.math.abs
import scala.util.{Failure, Success, Try}

sealed trait SQLPrimitive

case class PLong(value: Long) extends SQLPrimitive
case class PString(value: String) extends SQLPrimitive
case class PDate(value: String) extends SQLPrimitive
case class PBlob(value: String) extends SQLPrimitive
case class PTime(value: Long) extends SQLPrimitive
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

    def formatTimeOnlyDecomposed(milliSeconds: Long): (Int, Int, Int) = {
      val milli = abs(milliSeconds)
      val hours = milli / 3600_000
      var remaining = milli - (hours * 3600_000)
      val minutes = remaining / 60_000
      remaining = remaining - (minutes * 60_000)
      val seconds = remaining / 1000
      (hours.toInt, minutes.toInt, seconds.toInt)
    }

    def formatTimeOnly(milliSeconds: Long): String = {
      val milli = abs(milliSeconds)
      val hours = milli / 3600_000
      var remaining = milli - (hours * 3600_000)
      val minutes = remaining / 60_000
      remaining = remaining - (minutes * 60_000)
      val seconds = remaining / 1000
      val rs = String.format("%03d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
      if(milliSeconds < 0) {
         "-1" + rs
      }
      else {
        rs
      }

    }

    def parseTimeOnly(timeStr: String): Long = {
      val str = if (timeStr.startsWith("-1")) timeStr.substring(1) else timeStr
      val strSplit = str.split(":").toList
      val split: List[(String, Long)] = strSplit.reverse.zip(List(1_000L, 60_000L, 3600_000L).take(strSplit.size))
      val rs = split.foldLeft(0L) { case (acc, (longAsStr, multi))  =>
        acc + multi * longAsStr.toLong
      }
      if (timeStr.startsWith("-1")) {
        rs * -1L
      }
      else {
        rs
      }
    }

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
        case PTime(v) => {
          val isPostgres = statement.getStatement.getConnection.getMetaData.getURL.toLowerCase.startsWith("jdbc:postgresql")
          if (isPostgres) {
            val (hours, minutes, seconds) = formats.formatTimeOnlyDecomposed(v)
            statement.setTime(name, Time.valueOf(s"$hours:$minutes:$seconds"))
          }
          else {
            val formatted = formats.formatTimeOnly(v)
            statement.setString(name, formatted)
          }
        }
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