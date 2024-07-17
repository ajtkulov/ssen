package web

case class TextIR(value: String) {
  lazy val norm: Vector[String] = Normalizer.norm(value)
}

case class TermPair(pattern: String, wildCard: Boolean) {
  def isMatched(value: String) = {
    if (wildCard) {
      value.startsWith(pattern)
    } else {
      value == pattern
    }
  }
}

case class TermIR(value: String) {
  lazy val norm: Vector[TermPair] = {
    value.split(" ").toVector.map { str =>
      if (str.endsWith("*")) {
        TermPair(str.dropRight(1), true)
      } else {
        TermPair(str, false)
      }
    }
  }
}

object Matcher {
  def matchedText(text: String, matcher: Expression): Boolean = {
    matched(TextIR(text), matcher)
  }

  def matched(text: TextIR, matcher: Expression): Boolean = {
    matcher match {
      case Expression(Left(FunctionCall(fn, args))) if fn.value.toUpperCase == "AND" => args.forall(arg => matched(text, arg))
      case Expression(Left(FunctionCall(fn, args))) if fn.value.toUpperCase == "OR" => args.exists(arg => matched(text, arg))
      case Expression(Left(FunctionCall(fn, args))) if fn.value.toUpperCase == "NOT" => !matched(text, args.head)
      case Expression(Right(term)) => termMatch(text, TermIR(term.normValue)).isDefined
      case _ => false
    }
  }

  def termMatch(text: TextIR, pattern: TermIR): Option[Int] = {
    text.norm.sliding(pattern.norm.size).zipWithIndex.find {
      case (window, _) => windowMatch(window, pattern)
    }.map(_._2)
  }

  def windowMatch(text: Vector[String], pattern: TermIR): Boolean = {
    text.zip(pattern.norm).forall(x => x._2.isMatched(x._1))
  }
}
