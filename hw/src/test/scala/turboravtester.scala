package TurboRav

import Chisel._
import org.apache.commons.io.FilenameUtils;

object TurboRavTestRunner{
  def main(args: Array[String]): Unit = {
    val Array( // Parse argument list
      module,
      rom,
      num_pin_inputs,
      num_pin_outputs
    ) = args

    // Only use the basename when determining where the target
    // directory of the test is. This is needed to ensure all tests
    // end up in the generated directory.
    val target_dir = "generated/ctd/%s" format(
      FilenameUtils.getName(rom)
    )

    val test_args = Array(
      "--genHarness",
	  "--backend", "c",
	  "--targetDir", target_dir,
	  "--compile",
	  "--test",
	  "--vcd",
	  "--debug"
    )

    // En pils til førstemann som kan fjerne redundansen.
    // Hvis du prøvde og feilet inkrementer følgende:
    // 2
    val res = module match {
      case "alutest" =>
        chiselMainTest(test_args, () => Module(new Alu())){
          c => new AluTest(c)
        }
      case "brutest" =>
        chiselMainTest(test_args, () => Module(new BranchUnit())){
          c => new BranchUnitTest(c)
        }
      case "fwutest" =>
        chiselMainTest(test_args, () => Module(new ForwardingUnit())){
          c => new ForwardingUnitTest(c)
        }
      case "regbanktest" =>
        chiselMainTest(test_args, () => Module(new RegBank())){
          c => new RegBankTest(c)
        }
      case "multtest" =>
        chiselMainTest(test_args, () => Module(new Mult())){
          c => new MultTest(c)
        }
      case "timertest" =>
        chiselMainTest(test_args, () => Module(new Timer())){
          c => new TimerTest(c)
        }
      case "decodetest" =>
        chiselMainTest(test_args, () => Module(new Decode())){
          c => new DecodeTest(c)
        }
      case "executetest" =>
        chiselMainTest(test_args, () => Module(new Execute())){
          c => new ExecuteTest(c)
        }
      case "memorytest" =>
        chiselMainTest(test_args, () => Module(new Memory())){
          c => new MemoryTest(c)
        }
      case "writebacktest" =>
      chiselMainTest(test_args, () => Module(new Writeback())){
        c => new WritebackTest(c)
      }
      case "ravvtest" =>
        chiselMainTest(test_args, () => Module(new RavV())){
          c => new RavVTest(c)
        }
      case "soctest" =>
        chiselMainTest(test_args, () => Module(new Soc(num_pin_inputs.toInt, num_pin_outputs.toInt))){
          c => new SocTest(c)
        }
      case "riscvtest" =>
        chiselMainTest(
          test_args,
          () => Module(new Soc(num_pin_inputs.toInt, num_pin_outputs.toInt))
        )
        {
          c => new RiscvTest(c, target_dir)
        }
    }
  }
}
