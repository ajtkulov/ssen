package web

object CollectionUtils {
  def combine[T](values: List[Either[String, T]]): Either[String, T] = {
    values.find(x => x.isLeft).getOrElse(values.head)
  }
}
