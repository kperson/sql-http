package kperson.sqlh.common

import org.json4s.{CustomSerializer, Extraction, Formats}
import org.json4s.JsonAST._

object Serialization {

  private case class EnumerationWrapper[T](dataType: String, data: T)
  private case class EnumerationWrapperOptional[T](dataType: String, data: Option[T])

  private object SQLPrimitiveSerializer extends CustomSerializer[SQLPrimitive](implicit format => (
    {
      case jsonObj: JObject =>
        val dataType = (jsonObj \ "dataType").extract[String]
        dataType match {
          case "Long" => PLong((jsonObj \ "data").extract[Long])
          case "String" => PString((jsonObj \ "data").extract[String])
          case "Blob" => PBlob((jsonObj \ "data").extract[String])
          case "Date" => PDate((jsonObj \ "data").extract[String])
          case "Time" => PTime((jsonObj \ "data").extract[Long])
          case "Null" => NullP
        }
    },
    {
      case v: PLong => Extraction.decompose(EnumerationWrapper("Long", v.value))
      case v: PString => Extraction.decompose(EnumerationWrapper("String", v.value))
      case v: PBlob => Extraction.decompose(EnumerationWrapper("Blob", v.value))
      case v: PDate => Extraction.decompose(EnumerationWrapper("Date", v.value))
      case v: PTime => Extraction.decompose(EnumerationWrapper("Time", v.value))
      case _: NullP.type => Extraction.decompose(EnumerationWrapperOptional("Null", None))
    }
  ))

  private object MaskedSerializer extends CustomSerializer[Masked](implicit format => (
    {
      case jsonObj: JString =>
        Masked(jsonObj.values)
    },
    {
      case v: Masked => Extraction.decompose(v.value)(format)
    }
  ))

  private object TransactionReferenceSerializer extends CustomSerializer[DataSourceReference](implicit format => (
    {
      case jsonObj: JObject =>
        val dataType = (jsonObj \ "dataType").extract[String]
        dataType match {
          case "Direct" => Direct(jsonObj.extract[EnumerationWrapper[DataSource]].data)
          case "Custom" => Custom(jsonObj.extract[EnumerationWrapper[String]].data)
        }
    },
    {
      case v: Direct =>  Extraction.decompose(EnumerationWrapper("Direct", v.value))
      case v: Custom => Extraction.decompose(EnumerationWrapper("Custom", v.value))
    }
  ))

  val tmpFormats = org.json4s.DefaultFormats +
    MaskedSerializer +
    SQLPrimitiveSerializer +
    TransactionReferenceSerializer

  private class SQLCommandSerializer(implicit val formats: Formats) extends CustomSerializer[SQLCommand](_ => (
    {
      case jsonObj: JObject =>
        val dataType = (jsonObj \ "dataType").extract[String]
        dataType match {
          case "Query" => jsonObj.extract[EnumerationWrapper[Query]].data
          case "Write" => jsonObj.extract[EnumerationWrapper[Write]].data
          case "BatchWrite" => jsonObj.extract[EnumerationWrapper[BatchWrite]].data
        }
    },
    {
      case v: Query => Extraction.decompose(EnumerationWrapper("Query", v))(formats)
      case v: Write => Extraction.decompose(EnumerationWrapper("Write", v))(formats)
      case v: BatchWrite => Extraction.decompose(EnumerationWrapper("BatchWrite", v))(formats)
    }
  ))

  object Formats {
    implicit val formats = tmpFormats + new SQLCommandSerializer()(tmpFormats)
  }

}



