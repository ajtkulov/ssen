package web

import org.slf4j.LoggerFactory
import ujson.Value

object Web extends cask.MainRoutes {

  val logger = LoggerFactory.getLogger(getClass)
  override def port: Int = 8081

  override def host: String = "0.0.0.0"

  @cask.get("/")
  def hello(): String = {
    "Hello World!"
  }

  @cask.staticFiles("/static/:path")
  def staticFileRoutes(path: String) = "static/" + path

  @cask.postJson("/search")
  def search(expression: ujson.Value, date: ujson.Value): Value = {
    val str = expression.str
    val d = date.str


    logger.debug(s"expression: ${str}")
    logger.debug(s"date: ${date}")

    val expParse: Parser.ParseResult[Expression] = Parser.parseExpression(str)

    val exp = expParse.get

    val valid: Either[String, Unit] = ExpressionValidator.validate(exp)

    logger.debug(s"Parsed expression: ${exp}")

    val res: Vector[TgPost] = CH.tgEager(d, exp).sortBy(x => (x.channel, x.id))

    logger.debug(s"Result size: ${res.size}")

    if (valid.isLeft) {
      ujson.Obj("error" -> valid.left.get)
    } else {
      ujson.Obj(
        "result" -> ujson.Arr(res.map(_.toJson)),
      )
    }
  }

  CH.main(Array())
  initialize()
}
