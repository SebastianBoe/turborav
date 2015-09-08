package TurboRav

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
      num_pin_outputs,
      fpga
    ) = args

    val res = chiselMain(
      Array(
        "--genHarness",
        "--Wall",
        "--vcdMem", //TODO: Understand how to view this.
        "--backend", "v",
        "--targetDir", target_dir
      ),
      module match
      {
        case "Alu"          => () => Module(new Alu())
        case "Bru"          => () => Module(new BranchUnit())
        case "Cache"        => () => Module(new Cache())
        case "Decode"       => () => Module(new Decode())
        case "Execute"      => () => Module(new Execute())
        case "Fetch"        => () => Module(new Fetch())
        case "Memory"       => () => Module(new Memory())
        case "Mult"         => () => Module(new Mult())
        case "Ram"          => () => Module(new Ram())
        case "Ravv"         => () => Module(new RavV(rom, fpga.toBoolean))
        case "Regbank"      => () => Module(new RegBank())
        case "Rom"          => () => Module(new Rom(rom))
        case "Soc"          => () => Module(new Soc(rom, num_pin_inputs.toInt, num_pin_outputs.toInt, fpga.toBoolean))
        case "Spi"          => () => Module(new Spi())
        case "Writeback"    => () => Module(new Writeback())
        case "apb"          => () => Module(new ApbController())
        case "dvi_tmds_encoder"    => () => Module(new dvi_tmds_encoder())
        case _              => () => throw new Exception(
          "You forgot to add the module to turborav.scala"
        )
      }
    )
  }
}
