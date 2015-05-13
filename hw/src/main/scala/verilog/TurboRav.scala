package verilog

import Chisel._
import TurboRav._
import org.apache.commons.io.FilenameUtils;

object TurboRav {
  def main(args: Array[String]): Unit = {
    val Array( // Parse argument list
      module,
      target_dir,
      rom,
      num_pin_inputs,
      num_pin_outputs
    ) = args

    val res = chiselMain(
      Array(
        "--genHarness",
        "--backend", "v",
        "--targetDir", target_dir
      ),
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
        case "ravv"         => () => Module(new RavV(rom))
        case "regbank"      => () => Module(new RegBank())
        case "rom"          => () => Module(new Rom(rom))
        case "RRApbAdapter" => () => Module(new RRApbAdapter())
        case "Soc"          => () => Module(new Soc(rom, num_pin_inputs.toInt, num_pin_outputs.toInt))
        case "spi"          => () => Module(new Spi())
        case "writeback"    => () => Module(new Writeback())
        case _              => () => throw new Exception(
          "You forgot to add the module to turborav.scala"
        )
      }
    )
  }
}
