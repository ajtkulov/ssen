package web

import scala.util.parsing.combinator.RegexParsers
import scala.language.postfixOps

object Parser extends RegexParsers {
  def term: Parser[Term] = """\b\w+\b|\"[^\"]+\"""".r ^^ { x => Term(x) }

  def functionName: Parser[FunctionName] = "\\w+".r ^^ { x => FunctionName(x) }

  def expression: Parser[Expression] = functionCall ^^ { x => Expression(Left(x)) } | term ^^ { x => Expression(Right(x)) }

  def functionCall: Parser[FunctionCall] = "(" ~ functionName ~ (expression *) ~ ")" ^^ { case "(" ~ operator ~ (operands) ~ ")" => FunctionCall(operator, operands) }

  def parseFunctionCall(value: String): ParseResult[FunctionCall] = {
    parse(functionCall, value)
  }

  def parseExpression(value: String): ParseResult[Expression] = {
    parse(expression, value)
  }
}

sealed trait SearchDSL {}

case class Term(private val value: String) extends SearchDSL {
  val normValueCaseSensitive: String = {
    if (value.startsWith("\"")) {
      value.drop(1).dropRight(1)
    } else {
      value
    }
  }

  val normValueCaseIgnored: String = {
    normValueCaseSensitive.toUpperCase
  }
}

case class FunctionName(value: String) extends SearchDSL

case class Expression(value: Either[FunctionCall, Term]) extends SearchDSL {}

case class FunctionCall(funcCall: FunctionName, arguments: List[Expression]) extends SearchDSL {}

object ExpressionValidator {
  lazy val funcNames = Set("OR", "AND", "NOT", "CS")

  def validateArg(value: String): Either[String, Unit] = {
    val split = value.split(" ").filter(_.nonEmpty)
    if (split.forall(arg => arg.count(_ == '*') == 0 || (arg.count(_ == '*') == 1 && arg.endsWith("*")))) {
      Right()
    } else {
      Left("Asterisk allowed only at the end of the word")
    }
  }

  def validate(searchDSL: SearchDSL): Either[String, Unit] = {
    searchDSL match {
      case Term(_) =>
        Right()
      case FunctionName(value) => if (funcNames.contains(value.toUpperCase)) {
        Right()
      } else {
        Left(s"Unknown function name: ${value}")
      }

      case Expression(Left(f)) => validate(f)
      case Expression(Right(f)) => validate(f)

      case FunctionCall(f, a) =>
        val notCheck = if (f.value.toUpperCase == "NOT" && a.length != 1) {
          Left("Not function must have only one argument")
        } else if (f.value.toUpperCase == "CS" && a.length != 1 && !a.head.isInstanceOf[Expression] && !a.head.value.isRight) {
          Left("Case Ignore function must have only one argument with Term/just_a_string argument")
        } else {
          Right()
        }

        val all: List[Either[String, Unit]] = a.map(x => validate(x)) :+ validate(f) :+ notCheck
        CollectionUtils.combine(all)
    }
  }

}
