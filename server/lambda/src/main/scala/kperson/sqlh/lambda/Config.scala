package kperson.sqlh.lambda

import kperson.sqlh.common.DataSourceReferenceResolver

object Config {

  val authorization: Map[String, String] => Boolean = { _ => true }
  val resolvers: List[DataSourceReferenceResolver] = List()

}
