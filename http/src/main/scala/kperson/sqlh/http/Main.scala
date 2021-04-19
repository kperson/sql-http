package kperson.sqlh.http

import kperson.sqlh.common._
import Row._
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import org.sql2o.tools.NamedParameterStatement

import java.io.{InputStream, OutputStream}
import java.net.InetSocketAddress
import java.sql.{DriverManager, JDBCType, Types}
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

object Main extends App  {
  LoadDrivers()

  val d1 = "2021-04-19T08:14:35-05:00"
  val d2 = "2021-04-19T08:15:40-0500"
  val d3 = "2021-04-19T08:15:40-0500"
  val d4 = "2021-04-19T08:16:05-05"
  val d5 = "2021-04-19T08:18:39Z"

  List(d1, d2, d3, d4, d5).foreach { x =>
    println(SQLPrimitive.formats.parse(x))
  }
//  val dataSource = DataSource(
//    "jdbc:mysql://localhost:3306/livefit?serverTimezone=America/Chicago&useSSL=false",
//    Some(UsernamePassword("root", Masked("123456")))
//  )
//  val connection = ExecuteBeginTransaction(BeginTransaction(dataSource))
//  val results = ExecuteWrite(connection, Write("INSERT INTO test_table (name, datetime_col) VALUES ('dfdfd', :date_col);", Map(
//    "date_col" -> PDate(abc.format(new Date()))
//  ), Custom("fdfldlp")))
//  println(results)
//
//  connection.close()

//  val port = {
//    Option(System.getenv("PORT")).getOrElse("8080").toInt
//  }
//  val server = HttpServer.create(new InetSocketAddress(port), 0)
//  server.createContext( "/", this)
  //server.start()


//
//
//  def handle(exchange:
//
//  import kperson.sqlh.common.Row.ResultSetJDBC
//
//  HttpExchange): Unit = {
//    displayPayload(exchange.getRequestBody)
//    val hi = "hi"
//    exchange.sendResponseHeaders(200, hi.length)
//    val os = exchange.getResponseBody
//    os.write(hi.getBytes)
//    os.close()
//  }
//
//  private def displayPayload(body: InputStream): Unit ={
//    println()
//    println("******************** REQUEST START ********************")
//    println()
//    copyStream(body, System.out)
//    println()
//    println("********************* REQUEST END *********************")
//    println()
//  }

//  private def copyStream(in: InputStream, out: OutputStream) {
//    Iterator
//      .continually(in.read)
//      .takeWhile { size => size != -1 }
//      .foreach(out.write)
//  }
}
