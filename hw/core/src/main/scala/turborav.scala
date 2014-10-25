package TurboRav

import Chisel._

object TurboRav {
  def main(args: Array[String]): Unit = {
    val mainArgs = args.slice(1, args.length)
    val res = args(0) match {
      case "alu"      => chiselMain(mainArgs, () => Module(new Alu(32)))
      case "regbank"  => chiselMain(mainArgs, () => Module(new RegBank(32)))
      case "mult"     => chiselMain(mainArgs, () => Module(new Mult(32)))
      case "cache"    => chiselMain(mainArgs, () => Module(new Cache(64, 128, 1)))
    }
  }
}

