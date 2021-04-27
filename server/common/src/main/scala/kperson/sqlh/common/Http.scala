package kperson.sqlh.common

import java.io.InputStream
import Serialization.Formats._
import org.json4s.MappingException
import org.json4s.ParserUtil.ParseException
import org.json4s.jackson.Serialization.{read, write}

case class HttpRequest(body: InputStream, headers: Map[String, String])
case class HttpResponse(status: Int, body: String)
case class HttpError(error: String)

class Http(
  authorization: Map[String, String] => Boolean =  { _ => true } ,
  resolvers: List[DataSourceReferenceResolver] = List(),
) {

  private val runner = new CommandRunner(resolvers)

  def run(request: HttpRequest): HttpResponse = {
    if (!authorization(request.headers)) {
      HttpResponse(500, write(HttpError("authorization denied")))
    }
    else {
      try {
        val command = read[SQLCommand](request.body)
        HttpResponse(200, write(runner.run(command)))
      }
      catch {
        case _: MappingException | _: ParseException =>
          HttpResponse(500, write(HttpError("unable to parse request")))
        case ex: Throwable =>  HttpResponse(500, write(HttpError(ex.getMessage)))
      }
    }
  }

}
