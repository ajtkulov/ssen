package web

import org.scalatest.funsuite.AnyFunSuite

class MatcherTests extends AnyFunSuite {
  implicit val validator = BasicExpressionValidator

  test("Matcher test 1") {
    val exp = Parser.parseExpression("test").get
    assert(Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 2") {
    val exp = Parser.parseExpression(""""tes*"""").get
    assert(Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 3") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*")""").get
    assert(Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 4") {
    val exp = Parser.parseExpression("""(AND "sim" "tes*")""").get
    assert(!Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 5") {
    val exp = Parser.parseExpression(""""sim* tes*"""").get
    assert(Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 6") {
    val exp = Parser.parseExpression(""""sim tes*"""").get
    assert(!Matcher.matchedText("simple test", exp))
  }

  test("Matcher test 7") {
    val exp = Parser.parseExpression(""""sim* tes*"""").get
    assert(!Matcher.matchedText("simple inter test", exp))
  }

  test("Matcher test 8") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*")""").get
    assert(Matcher.matchedText("simple inter test", exp))
  }

  test("Matcher test 9") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*" "inter")""").get
    assert(Matcher.matchedText("simple inter test", exp))
  }

  test("Matcher test 10") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*" (NOT "inter"))""").get
    assert(!Matcher.matchedText("simple inter test", exp))
  }

  test("Matcher test 11") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*" (NOT "interpol"))""").get
    assert(Matcher.matchedText("simple inter test", exp))
  }

  test("Matcher test 12") {
    val exp = Parser.parseExpression("""(AND "sim*" "tes*")""").get
    assert(Matcher.matchedText("simple inter Test case ignored by default", exp))
  }

  test("Matcher test 12.1") {
    val exp = Parser.parseExpression("""(AND "sim*" "TES*")""").get
    assert(Matcher.matchedText("simple inter test case ignored by default", exp))
  }

  test("Matcher test 13") {
    val exp = Parser.parseExpression("""(AND "sim*" (CS "tes*"))""").get
    assert(!Matcher.matchedText("simple inter Test ", exp))
  }

  test("Matcher test 13.1") {
    val exp = Parser.parseExpression("""(AND "sim*" (CS "Tes*"))""").get
    assert(Matcher.matchedText("simple inter Test with case sensitive", exp))
  }

  test("Matcher test - validation") {
    intercept[java.lang.AssertionError](Parser.parseStrict("""(AND "*sim*")"""))
  }

  test("Matcher test - validation - 1") {
    assert(Parser.parseStrict("""(AND "sim*")""").isInstanceOf[Expression])
  }

  test("Matcher test - validation - 2") {
    intercept[java.lang.AssertionError](Parser.parseStrict("""Tes*t"""))
  }

  test("Matcher test - validation - 3") {
    intercept[java.lang.AssertionError](Parser.parseStrict("""(AN D "*sim*")"""))
  }
}
