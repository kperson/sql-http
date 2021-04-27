package kperson.sqlh.lambda

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import kperson.sqlh.common.{Custom, DataSource, DataSourceReferenceResolver, Serialization}
import org.json4s.jackson.Serialization.read
import Serialization.Formats._


case class SecretsManagerPayload(secret: String, db: String)

class SecretsManagerResolver extends DataSourceReferenceResolver {

  private val client = AWSSecretsManagerClientBuilder.defaultClient()

  def convertCustomToDataSource(custom: Custom): Option[DataSource] = {
    try {
      val payload = read[SecretsManagerPayload](custom.value)
      val req = new GetSecretValueRequest()
      req.setSecretId(payload.secret)
      val value = client.getSecretValue(req).getSecretString
      val config = read[DataApiConfig](value)
      Some(config.toDataSource(payload.db))
    }
    catch {
      case _: Throwable => None
    }
  }
}
