package kperson.sqlh.common

case class Masked(value: String) {
  override def toString = "**********"
}
