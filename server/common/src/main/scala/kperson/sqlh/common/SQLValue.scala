package kperson.sqlh.common

import java.io.ByteArrayInputStream
import java.sql.{Connection, Time, Timestamp, Types}
import java.text.SimpleDateFormat
import java.util.Date
import scala.math.abs
import scala.util.{Failure, Success, Try}

sealed trait SQLValue

case class PLong(value: Long) extends SQLValue
case class PString(value: String) extends SQLValue
case class PDate(value: String) extends SQLValue
case class PBlob(value: String) extends SQLValue
case class PTime(value: Long) extends SQLValue
case class PDouble(value: Double) extends SQLValue
case class PDecimal(value: String) extends SQLValue
case object PNull extends SQLValue

object SQLValue {

  val formats = new DateFormats()

  class DateFormats {
    private val timeFormat = new SimpleDateFormat("HH:mm:ssX")
    val timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val parseFormats = List(
      timestampFormat,
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX"),
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
      dateFormat
    )

    def decomposeTime(milliSeconds: Long): (Int, Int, Int) = {
      val milli = abs(milliSeconds)
      val hours = milli / 3600_000
      var remaining = milli - (hours * 3600_000)
      val minutes = remaining / 60_000
      remaining = remaining - (minutes * 60_000)
      val seconds = remaining / 1000
      (hours.toInt, minutes.toInt, seconds.toInt)
    }

    def formatTimeOnly(milliSeconds: Long): String = {
      val (hours, minutes, seconds) = decomposeTime(abs(milliSeconds))
      val rs = String.format("%03d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
      if (milliSeconds < 0) "-" + rs else rs
    }

    def parseTimeOnly(timeStr: String): Long = {
      val isPostgresTime = (timeStr.contains("-") || timeStr.contains("+")) && !timeStr.startsWith("-")
      if (isPostgresTime) {
        timeFormat.parse(timeStr).getTime
      }
      else {
        val str = if (timeStr.startsWith("-")) timeStr.substring(1) else timeStr
        val strSplit = str.split(":").toList
        val split: List[(String, Long)] = strSplit.reverse.zip(List(1_000L, 60_000L, 3600_000L).take(strSplit.size))
        val rs = split.foldLeft(0L) { case (acc, (longAsStr, multi)) =>
          acc + multi * longAsStr.toLong
        }
        if (timeStr.startsWith("-")) {
          rs * -1L
        }
        else {
          rs
        }
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


  implicit class NamedParameterStatementJDBC(statement: org.sql2o.Query) {
    def populateStatement(name: String, value: SQLValue, connection: Connection) {
      value match {
        case PLong(v) => statement.addParameter(name, v)
        case PString(v) => statement.addParameter(name, v)
        case PBlob(v) => statement.addParameter(name, new ByteArrayInputStream(java.util.Base64.getDecoder.decode(v)))
        case PDate(v) => statement.addParameter(name, new Timestamp(formats.parse(v).getTime))
        case PDouble(v) => {
          val d: java.lang.Double = v
          statement.addParameter(name, d)
        }
        case PDecimal(v) => statement.addParameter(name, new java.math.BigDecimal(v))
        case PTime(v) => {
          val isPostgres = connection.getMetaData.getURL.toLowerCase.startsWith("jdbc:postgresql")
          if (isPostgres) {
            val (hours, minutes, seconds) = formats.decomposeTime(v)
            statement.addParameter(name, Time.valueOf(s"$hours:$minutes:$seconds"))
          }
          else {
            val formatted = formats.formatTimeOnly(v)
            statement.addParameter(name, formatted)
          }
        }
        case PNull => {
          val x: AnyRef = null
          statement.bind()
          statement.addParameter(name, x)
        }
      }
    }

    def populateStatement(params: Map[String, SQLValue], connection: Connection) {
      params.foreach { case (name, value) =>
        statement.populateStatement(name, value, connection)
      }
    }
  }

}