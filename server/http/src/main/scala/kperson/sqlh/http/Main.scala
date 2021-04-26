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
  val http = new Http(Config.authorization, Config.resolvers)

  def handle(exchange: HttpExchange) {
    val headers = scala.collection.mutable.Map[String, String]()
    exchange.getRequestHeaders.forEach { (k, v) =>
      headers(k) = v.get(0)
    }
    val request = HttpRequest(exchange.getRequestBody, headers.toMap)
    val response = http.run(request)
    exchange.getResponseHeaders.add("Content-Type", "application/json")
    exchange.getResponseHeaders.add("Cache-Control", "no-cache")
    exchange.sendResponseHeaders(response.status, response.body.length)
    val os = exchange.getResponseBody
    os.write(response.body.getBytes)
    os.close()
  }

}
