package kperson.sqlh.lambda

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import kperson.sqlh.common.HttpError
import org.json4s.jackson.Serialization.{read, write}
import kperson.sqlh.common.Serialization.Formats._
import org.json4s.MappingException
import org.json4s.ParserUtil.ParseException

import java.io.{ByteArrayInputStream, InputStream, OutputStream}
import java.nio.charset.StandardCharsets

//https://rieckpil.de/java-aws-lambda-container-image-support-complete-guide/
class Handler extends RequestStreamHandler {
  val http = new kperson.sqlh.common.Http(Config.authorization, Config.resolvers)

  override def handleRequest(input: InputStream, output: OutputStream, context: Context) {
    val request = read[LambdaHttpRequest](input).normalize()
    val responseHeaders = Map("Content-Type" -> "application/json", "Cache-Control" -> "no-cache")
    try {
      val nativeRequest = kperson.sqlh.common.HttpRequest(
        new ByteArrayInputStream(request.body.getBytes(StandardCharsets.UTF_8)),
        request.headers.getOrElse(Map())
      )
      val nativeResponse = http.run(nativeRequest)
      val lambdaResponse = LambdaHttpResponse(
        nativeResponse.status,
        nativeResponse.body,
        responseHeaders
      )
      writeResponse(lambdaResponse, output)
    }

    catch {
      case _: MappingException | _: ParseException =>
        val lambdaResponse = LambdaHttpResponse(500, write(HttpError("unable to parse request")), responseHeaders)
        writeResponse(lambdaResponse, output)
      case ex: Throwable =>
        val lambdaResponse = LambdaHttpResponse(500, write(HttpError(ex.getMessage)), responseHeaders)
        writeResponse(lambdaResponse, output)
    }
  }

  private def writeResponse(lambdaResponse: LambdaHttpResponse, output: OutputStream) {
    output.write(write(lambdaResponse).getBytes())
    output.flush()
    output.close()
  }
}


case class LambdaHttpRequest(
  body: String = "",
  headers: Option[Map[String, String]] = None,
  isBase64Encoded: Boolean = false
) {

  def normalize(): LambdaHttpRequest = {
    if (isBase64Encoded) {
      val bs = java.util.Base64.getDecoder.decode(body)
      copy(body = new String(bs), isBase64Encoded = false)
    }
    else {
      this
    }
  }

}

case class LambdaHttpResponse(statusCode: Int, body: String = "", headers: Map[String, String] = Map.empty)