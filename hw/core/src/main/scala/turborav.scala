package TurboRav

import Chisel._

object TurboRav {
  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "cache"
    if (args.length >= 1) args + "--backend c" + "--genHarness"

    val mainArgs = args.slice(1, args.length)

    val res = module match {
      case "alu"      => chiselMain(mainArgs, () => Module(new Alu(32)))
      case "regbank"  => chiselMain(mainArgs, () => Module(new RegBank(32)))
      case "mult"     => chiselMain(mainArgs, () => Module(new Mult(32)))
      case "cache"    => chiselMain(mainArgs, () => Module(new Cache(128, 128, 1)))
    }
  }
}

