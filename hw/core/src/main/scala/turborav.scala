package TurboRav

import Chisel._
import Common._

object TurboRav {
  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "cache"
    if (args.length >= 1) args + "--backend c" + "--genHarness"

    val mainArgs = args.slice(1, args.length)

    implicit val conf = new TurboravConfig()

    val res = module match {
      case "alu"     => chiselMain(mainArgs, () => Module(new Alu()))
      case "regbank" => chiselMain(mainArgs, () => Module(new RegBank()))
      case "rom"     => chiselMain(mainArgs, () => Module(new Rom()))
        // TODO: Use conf in Mult and Cache as well.
      case "mult"    => chiselMain(mainArgs, () => Module(new Mult(conf.xlen)))
      case "cache"    => chiselMain(mainArgs, () => Module(new Cache(128, 128, 1)))
      case "fetch"   => chiselMain(mainArgs, () => Module(new Fetch()))
      case "decode"   => chiselMain(mainArgs, () => Module(new Decode()))
      case "execute" => chiselMain(mainArgs, () => Module(new Execute()))
      case "memory"   => chiselMain(mainArgs, () => Module(new Memory()))
      case "writeback"   => chiselMain(mainArgs, () => Module(new Writeback()))
      case "ravv"    => chiselMain(mainArgs, () => Module(new RavV()))
    }
  }
}
