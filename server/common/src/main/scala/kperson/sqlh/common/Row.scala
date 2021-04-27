package kperson.sqlh.common

import java.sql.{ResultSet, Types}

case class Column(
  index: Int,
  name: String,
  label: String,
  value: SQLValue
)

case class Row(columns: List[Column])

object Row {

  implicit class ResultSetJDBC(rs: ResultSet) {
    def toRow: Row = {
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

    def toRows: LazyList[Row] = {
      LazyList.continually(if (rs.next()) Some(rs.toRow) else None)
        .takeWhile{ _.isDefined  }
        .flatten
    }

    def primitiveValue(index: Int): SQLValue = {
      val meta = rs.getMetaData
      rs.getString(index)
      if (rs.wasNull()) {
        PNull
      }
      else {
        meta.getColumnType(index) match {
          case Types.TIMESTAMP | Types.TIME_WITH_TIMEZONE => PDate(SQLValue.formats.timestampFormat.format(rs.getTimestamp(index)))
          case Types.DATE => PDate(SQLValue.formats.timestampFormat.format(rs.getDate(index)))
          case Types.INTEGER | Types.SMALLINT | Types.TINYINT | Types.BIGINT => PLong(rs.getLong(index))
          case Types.TIME =>
            val time = SQLValue.formats.parseTimeOnly(rs.getString(index))
            PTime(time)
          case Types.DOUBLE | Types.FLOAT | Types.REAL  => PDouble(rs.getDouble(index))
          case Types.DECIMAL | Types.NUMERIC => PDecimal(rs.getString(index))
          case Types.VARCHAR | Types.NVARCHAR | Types.LONGVARCHAR | Types.LONGNVARCHAR | Types.CHAR | Types.NCHAR => PString(rs.getString(index))
          case Types.LONGVARBINARY | Types.BINARY | Types.VARBINARY | Types.BLOB =>
            val is = rs.getBinaryStream(index)
            val bytes = LazyList.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
            PBlob(java.util.Base64.getEncoder.encodeToString(bytes))
          case _ =>
            PString(rs.getObject(index).toString)
        }
      }
    }
  }
}