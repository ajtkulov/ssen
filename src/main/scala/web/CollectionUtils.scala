package web

object CollectionUtils {
  def combine(values: List[Either[String, Unit]]): Either[String, Unit] = {
    values.foldLeft[Either[String, Unit]](Right()) {
      case (Right(_), Right(_)) => Right()
      case (Left(a), Right(_)) => Left(a)
      case (Left(a), Left(_)) => Left(a)
      case (_, Left(b)) => Left(b)
    }
  }
}
