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
        case "cache"        => () => Module(new Cache())
        case "decode"       => () => Module(new Decode())
        case "execute"      => () => Module(new Execute())
        case "fetch"        => () => Module(new Fetch())
        case "memory"       => () => Module(new Memory())
        case "mult"         => () => Module(new Mult())
        case "ram"          => () => Module(new Ram())
        case "RavVMemoryRequestArbiter" => () => Module(new RavVMemoryRequestArbiter())
        case "ravv"         => () => Module(new RavV())
        case "regbank"      => () => Module(new RegBank())
        case "rom"          => () => Module(new Rom())
        case "RRApbAdapter" => () => Module(new RRApbAdapter())
        case "soc"          => () => Module(new Soc())
        case "spi"          => () => Module(new Spi())
        case "writeback"    => () => Module(new Writeback())
        case _              => () => throw new Exception(
          "You forgot to add the module to turborav.scala"
        )
      }
    )
  }
}
