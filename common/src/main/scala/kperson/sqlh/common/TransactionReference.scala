package kperson.sqlh.common

sealed trait TransactionReference

case class OneOff(value: DataSource) extends TransactionReference
case class TransactionId(value: String) extends TransactionReference
case class Custom(value: String) extends TransactionReference
