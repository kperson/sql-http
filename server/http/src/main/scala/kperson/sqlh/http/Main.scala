package kperson.sqlh.http

import kperson.sqlh.common._
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import java.net.InetSocketAddress


object Main extends App with HttpHandler {

  LoadDrivers()
  val port = {
    Option(System.getenv("PORT")).getOrElse("8080").toInt
  }
  val server = HttpServer.create(new InetSocketAddress(port), 0)
  server.createContext("/", this)
  server.start()
  val http = new Http()

  def handle(exchange: HttpExchange) {
    val headers = scala.collection.mutable.Map[String, String]()
    exchange.getRequestHeaders.forEach { (k, v) =>
      headers(k.toLowerCase) = v.get(0)
    }
    val request = HttpRequest(exchange.getRequestBody, headers.toMap)
    val response = http.run(request)
    exchange.getResponseHeaders.add("Content-Type", "application/json")
    exchange.sendResponseHeaders(response.status, response.body.length)
    val os = exchange.getResponseBody
    os.write(response.body.getBytes)
    os.close()
  }

}
