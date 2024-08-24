package web

import org.slf4j.LoggerFactory
import ujson.Value

object ElasticWeb extends cask.MainRoutes {

  val logger = LoggerFactory.getLogger(getClass)

  override def port: Int = 8082

  override def host: String = "0.0.0.0"

  @cask.get("/")
  def hello(): String = {
    "Hello World!"
  }

  @cask.postJson("/convert")
  def convert(expression: ujson.Value): Value = {
    val str = expression.str

    logger.debug(s"expression: ${str}")

    val expParse: Parser.ParseResult[Expression] = Parser.parseExpression(str)

    val exp = expParse.get

    val valid: Either[String, SearchDSL] = BasicExpressionValidator.validate(exp)

    logger.debug(s"Parsed expression: ${exp}")

    if (valid.isLeft) {
      ujson.Obj("error" -> valid.left.get)
    } else {
      ujson.Obj(
        "result" -> ElasticTransformation.transform(exp)
      )
    }
  }

  initialize()
}
