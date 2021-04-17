package kperson.sqlh.http

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

import java.io.{InputStream, OutputStream}
import java.net.InetSocketAddress
import java.sql.DriverManager

object Main extends App with HttpHandler {
  val port = {
    Option(System.getenv("PORT")).getOrElse("8080").toInt
  }
  val server = HttpServer.create(new InetSocketAddress(port), 0)
  server.createContext( "/", this)
  //server.start()




  def handle(exchange: HttpExchange): Unit = {
    displayPayload(exchange.getRequestBody)
    val hi = "hi"
    exchange.sendResponseHeaders(200, hi.length)
    val os = exchange.getResponseBody
    os.write(hi.getBytes)
    os.close()
  }

  private def displayPayload(body: InputStream): Unit ={
    println()
    println("******************** REQUEST START ********************")
    println()
    copyStream(body, System.out)
    println()
    println("********************* REQUEST END *********************")
    println()
  }

  private def copyStream(in: InputStream, out: OutputStream) {
    Iterator
      .continually(in.read)
      .takeWhile { size => size != -1 }
      .foreach(out.write)
  }
}
