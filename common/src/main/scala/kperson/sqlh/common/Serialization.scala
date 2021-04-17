package kperson.sqlh.common

import org.json4s.{CustomSerializer, Extraction, Formats}
import org.json4s.JsonAST._

object Serialization {

  private case class EnumerationWrapper[T](dataType: String, data: T)

  private object MaskedSerializer extends CustomSerializer[Masked](implicit format => (
    {
      case jsonObj: JString =>
        Masked(jsonObj.values)
    },
    {
      case v: Masked => Extraction.decompose(v.value)
    }
  ))

  private object TransactionReferenceSerializer extends CustomSerializer[TransactionReference](implicit format => (
    {
      case jsonObj: JObject =>
        val dataType = (jsonObj \ "dataType").extract[String]
        dataType match {
          case "OneOff" => OneOff(jsonObj.extract[EnumerationWrapper[DataSource]].data)
          case "TransactionId" => TransactionId(jsonObj.extract[EnumerationWrapper[String]].data)
          case "Custom" => Custom(jsonObj.extract[EnumerationWrapper[String]].data)
        }
    },
    {
      case v: OneOff =>  Extraction.decompose(EnumerationWrapper("OneOff", v.value))
      case v: TransactionId => Extraction.decompose(EnumerationWrapper("TransactionId", v.value))
      case v: Custom => Extraction.decompose(EnumerationWrapper("Custom", v.value))
    }
  ))

  val tmpFormats = org.json4s.DefaultFormats +
    MaskedSerializer +
    TransactionReferenceSerializer

  private class SQLCommandSerializer(implicit val formats: Formats) extends CustomSerializer[SQLCommand](_ => (
    {
      case jsonObj: JObject =>
        val dataType = (jsonObj \ "dataType").extract[String]
        dataType match {
          case "BeginTransaction" => jsonObj.extract[EnumerationWrapper[BeginTransaction]].data
          case "Commit" => jsonObj.extract[EnumerationWrapper[Commit]].data
          case "Statement" => jsonObj.extract[EnumerationWrapper[Statement]].data
          case "Rollback" => jsonObj.extract[EnumerationWrapper[Rollback]].data
        }
    },
    {
      case v: BeginTransaction => Extraction.decompose(EnumerationWrapper("BeginTransaction", v))(formats)
      case v: Commit => Extraction.decompose(EnumerationWrapper("Commit", v))(formats)
      case v: Statement => Extraction.decompose(EnumerationWrapper("Statement", v))(formats)
      case v: Rollback => Extraction.decompose(EnumerationWrapper("Rollback", v))(formats)
    }
  ))



  implicit val formats = tmpFormats + new SQLCommandSerializer()(tmpFormats)
}



