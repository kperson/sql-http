package kperson.sqlh.http

import kperson.sqlh.common.DataSourceReferenceResolver

object Config {

  val authorization: Map[String, String] => Boolean = { _ => true }
  val resolvers: List[DataSourceReferenceResolver] = List()

}
