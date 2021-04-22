package kperson.sql.common

import kperson.sqlh.common.DatabaseVendor


case class TestSQL(
  defaultSQL: String,
  overrideSQL: Map[DatabaseVendor, String] = Map()
) {

  def sql(vendor: DatabaseVendor): String =  {
    overrideSQL.getOrElse(vendor, defaultSQL)
  }

}
