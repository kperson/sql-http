package kperson.sqlh.common

sealed trait SQLCommand

case class BeginTransaction(value: DataSource) extends SQLCommand
case class Commit(transactionId: String) extends SQLCommand
case class Statement(sql: String, transactionReference: TransactionReference) extends SQLCommand
case class Rollback(transactionId: String) extends SQLCommand