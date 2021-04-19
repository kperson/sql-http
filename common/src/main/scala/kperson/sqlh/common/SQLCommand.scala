package kperson.sqlh.common

sealed trait SQLCommand


case class BeginTransaction(dataSource: DataSource) extends SQLCommand
case class Commit(transactionId: String) extends SQLCommand
case class Query(sql: String, params: Map[String, SQLPrimitive], transactionReference: TransactionReference = Custom("")) extends SQLCommand
case class Write(sql: String, params: Map[String, SQLPrimitive], transactionReference: TransactionReference = Custom("")) extends SQLCommand
case class BatchWrite(sql: String, params: List[Map[String, SQLPrimitive]], transactionReference: TransactionReference = Custom("")) extends SQLCommand
case class Rollback(transactionId: String) extends SQLCommand