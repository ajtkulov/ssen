package web

import ujson.Value

object Web extends cask.MainRoutes {
  override def port: Int = 8081

  override def host: String = "0.0.0.0"

  @cask.get("/")
  def hello(): String = {
    "Hello World!"
  }

  @cask.staticFiles("/static/:path")
  def staticFileRoutes(path: String) = "static/" + path

  @cask.postJson("/search")
  def search(data: ujson.Value): Value = {
    val str = data.str

    ujson.Obj(
      "byPage" -> "byPageJson",
    )
  }

  initialize()
}
