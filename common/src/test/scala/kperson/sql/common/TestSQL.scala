package kperson.sql.common

sealed trait DatabaseVendor
case object MySQL extends DatabaseVendor
case object Postgres extends DatabaseVendor

case class TestSQL(
  defaultSQL: String,
  overrideSQL: Map[DatabaseVendor, String] = Map()
) {

  def sql(vendor: DatabaseVendor): String =  {
    overrideSQL.getOrElse(vendor, defaultSQL)
  }

}
