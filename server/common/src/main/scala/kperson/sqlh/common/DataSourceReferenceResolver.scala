package kperson.sqlh.common

trait DataSourceReferenceResolver {

  def convertCustomToDataSource(custom: Custom): Option[DataSource]

}


