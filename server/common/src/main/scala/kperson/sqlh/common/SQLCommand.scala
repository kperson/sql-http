package kperson.sqlh.common

sealed trait SQLCommand
case class Query(dataSourceReference: DataSourceReference, sql: String, params: Map[String, SQLPrimitive] = Map()) extends SQLCommand
case class Write(dataSourceReference: DataSourceReference, sql: String, params: Map[String, SQLPrimitive] = Map()) extends SQLCommand
case class BatchWrite(dataSourceReference: DataSourceReference, sql: String, params: List[Map[String, SQLPrimitive]] = List()) extends SQLCommand
