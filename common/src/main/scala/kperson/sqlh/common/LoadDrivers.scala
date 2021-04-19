package kperson.sqlh.common

object LoadDrivers {

  def apply(): Unit ={
    Class.forName("com.mysql.cj.jdbc.Driver")
    Class.forName("org.postgresql.Driver")
    Class.forName("com.snowflake.client.jdbc.SnowflakeDriver")
  }

}
