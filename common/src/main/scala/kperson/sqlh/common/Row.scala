package kperson.sqlh.common

import java.sql.{Connection, DriverManager, ResultSet, Types}
import java.text.SimpleDateFormat
import java.util.{Date, Properties, TimeZone}

case class Column(
  index: Int,
  name: String,
  label: String,
  value: SQLPrimitive
)

case class Row(columns: List[Column])



object Row {


  implicit class ResultSetJDBC(rs: ResultSet) {
    def toRow(): Row = {
      val meta = rs.getMetaData
      val columnCount = meta.getColumnCount
      val columns = (1 to columnCount).map { index =>
        Column(
          index,
          meta.getColumnName(index),
          meta.getColumnLabel(index),
          rs.primitiveValue(index)
        )
      }.toList
      Row(columns)
    }

    def toRows() = {
      LazyList.continually(if (rs.next()) Some(toRow()) else None)
        .takeWhile{ _.isDefined  }
        .flatten
    }

    def primitiveValue(index: Int): SQLPrimitive = {
      val meta = rs.getMetaData
      rs.getObject(index)
      if (rs.wasNull()) {
        NullP
      }
      else {
        meta.getColumnType(index) match {
          case Types.TIMESTAMP => PDate(SQLPrimitive.formats.timestampFormat.format(rs.getTimestamp(index)))
          case Types.DATE => PDate(SQLPrimitive.formats.timestampFormat.format(rs.getDate(index)))
          case Types.INTEGER => PLong(rs.getLong(index))
          case Types.SMALLINT => PLong(rs.getLong(index))
          case Types.TINYINT => PLong(rs.getLong(index))
          case Types.BIGINT => PLong(rs.getLong(index))
          case _ => {
            println(meta.getColumnTypeName(index))
            PString(rs.getObject(index).toString)
          }
        }
      }
    }
  }
}