package web

object Normalizer {
  def norm(value: String): Vector[String] = {
    value.map(c => if (c.isLetter) c else ' ').split(" ").filter(_.nonEmpty).toVector
  }
}
