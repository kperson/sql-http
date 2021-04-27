package kperson.sqlh.lambda

import kperson.sqlh.common.DataSourceReferenceResolver

object Config {

  val authorization: Map[String, String] => Boolean = { _ => true }
  val resolvers: List[DataSourceReferenceResolver] = List(
    new CachedResolver(new SecretsManagerResolver(), 1000 * 60)
  )

}
