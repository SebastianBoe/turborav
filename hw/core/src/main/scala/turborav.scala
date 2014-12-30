package TurboRav

import Chisel._
import Common._

object TurboRav {
  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "ravv"

    val mainArgs = args.slice(1, args.length)

    val res = chiselMain(
      mainArgs,
      module match
      {
        case "alu"          => () => Module(new Alu())
        case "bru"          => () => Module(new BranchUnit())
        case "regbank"      => () => Module(new RegBank())
        case "rom"          => () => Module(new Rom())
        case "mult"         => () => Module(new Mult())
        case "cache"        => () => Module(new Cache())
        case "fetch"        => () => Module(new Fetch())
        case "decode"       => () => Module(new Decode())
        case "execute"      => () => Module(new Execute())
        case "memory"       => () => Module(new Memory())
        case "writeback"    => () => Module(new Writeback())
        case "ravv"         => () => Module(new RavV())
        case "RRApbAdapter" => () => Module(new RRApbAdapter())
        case "soc"          => () => Module(new Soc())
        case "ram"          => () => Module(new Ram())
        case _              => () => throw new Exception(
          "You forgot to add the module to turborav.scala"
        )
      }
    )
  }
}
