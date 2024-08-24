package web

object ElasticTransformation {
  def transform(expression: SearchDSL): ujson.Obj = {
    expression match {
      case Term(x) if x.last == '*' =>
        ujson.Obj("match_phrase_prefix" -> ujson.Obj("text" -> x.dropRight(1)))
      case Term(x) =>
        ujson.Obj("match_phrase" -> ujson.Obj("text" -> x))
      case FunctionCall(FunctionName("AND"), list) => ujson.Obj("bool" -> ujson.Obj("must" -> list.map(exp => transform(exp))))
      case FunctionCall(FunctionName("OR"), list) => ujson.Obj("bool" -> ujson.Obj("should" -> list.map(exp => transform(exp))))
      case FunctionCall(FunctionName("NOT"), list) => ujson.Obj("bool" -> ujson.Obj("must_not" -> list.map(exp => transform(exp))))
      case Expression(Left(x)) => transform(x)
      case Expression(Right(x)) => transform(x)
      case _ => ???
    }
  }
}
