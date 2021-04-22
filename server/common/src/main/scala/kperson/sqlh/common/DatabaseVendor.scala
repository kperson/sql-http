package kperson.sqlh.common

sealed trait DatabaseVendor
case object MySQL extends DatabaseVendor
case object Postgres extends DatabaseVendor
case object MariaDB extends DatabaseVendor