package kperson.sqlh.common

sealed trait DataSourceReference

case class Direct(value: DataSource) extends DataSourceReference
case class Custom(value: String) extends DataSourceReference
