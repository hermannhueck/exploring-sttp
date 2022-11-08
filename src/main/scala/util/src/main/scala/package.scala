package object util {

  def line(length: Int): String =
    "\u2500" * length

  val line10 = line(10)
  val line20 = line(20)
  val line80 = line(80)

  implicit final class StringExtensions(private val str: String) extends AnyVal {

    @inline def color(escape: String): String = s"$escape$str${Console.RESET}"
    @inline def green: String                 = color(Console.GREEN)
    @inline def red: String                   = color(Console.RED)
    @inline def blue: String                  = color(Console.BLUE)
    @inline def yellow: String                = color(Console.YELLOW)
    @inline def magenta: String               = color(Console.MAGENTA)
    @inline def cyan: String                  = color(Console.CYAN)
  }

  implicit final class EitherOps[L, R](private val either: Either[L, R]) extends AnyVal {

    @inline def biMap[L2, R2](fl: L => L2, fr: R => R2): Either[L2, R2] =
      either match {
        case Left(l)  => Left(fl(l))
        case Right(r) => Right(fr(r))
      }

    @inline def leftMap[L2](f: L => L2): Either[L2, R] =
      biMap(f, identity)
  }
}
