package TurboRav

import Chisel._
import Common._

object TurboRav {
  def main(args: Array[String]): Unit = {
    val mainArgs = args.slice(1, args.length)
    implicit val conf = new TurboravConfig()
    val res = args(0) match {
      case "alu" =>
        chiselMain(mainArgs, () => Module(new Alu()))
      case "regbank" =>
        chiselMain(mainArgs, () => Module(new RegBank()))
    }
  }
}
