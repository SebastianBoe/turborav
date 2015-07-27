package TurboRav

import Chisel._
import org.apache.commons.io.FilenameUtils;

object TurboRavTestRunner{
  def main(args: Array[String]): Unit = {
    val Array( // Parse argument list
      module,
      target_dir,
      rom,
      num_pin_inputs,
      num_pin_outputs
    ) = args

    val test_args = Array(
      "--genHarness",
	  "--backend", "c",
	  "--targetDir", target_dir,
	  "--compile",
	  "--test",
	  "--vcd",
	  "--debug"
    )

    // NB: The below list of modules is duplicated in
    // turborav/hw/SConstruct. Respect if you can remove this
    // duplication.

    // En pils til førstemann som kan fjerne redundansen.
    // Hvis du prøvde og feilet inkrementer følgende:
    // 2
    val res = module match {
      case "Alutest" =>
        chiselMainTest(test_args, () => Module(new Alu())){
          c => new AluTest(c)
        }
      case "BranchUnittest" =>
        chiselMainTest(test_args, () => Module(new BranchUnit())){
          c => new BranchUnitTest(c)
        }
      case "ForwardingUnittest" =>
        chiselMainTest(test_args, () => Module(new ForwardingUnit())){
          c => new ForwardingUnitTest(c)
        }
      case "RegBanktest" =>
        chiselMainTest(test_args, () => Module(new RegBank())){
          c => new RegBankTest(c)
        }
      case "Multtest" =>
        chiselMainTest(test_args, () => Module(new Mult())){
          c => new MultTest(c)
        }
      case "Timertest" =>
        chiselMainTest(test_args, () => Module(new Timer())){
          c => new TimerTest(c)
        }
      case "Decodetest" =>
        chiselMainTest(test_args, () => Module(new Decode())){
          c => new DecodeTest(c)
        }
      case "Executetest" =>
        chiselMainTest(test_args, () => Module(new Execute())){
          c => new ExecuteTest(c)
        }
      case "Memorytest" =>
        chiselMainTest(test_args, () => Module(new Memory())){
          c => new MemoryTest(c)
        }
      case "Writebacktest" =>
      chiselMainTest(test_args, () => Module(new Writeback())){
        c => new WritebackTest(c)
      }
      case "Apbtest" =>
        chiselMainTest(test_args, () => Module(new ApbController())){
          c => new ApbControllerTest(c)
        }
      case "dvi_tmds_encodertest" =>
        chiselMainTest(test_args, () => Module(new dvi_tmds_encoder())){
          c => new dvi_tmds_encoderTest(c)
        }
      case "dvi_tmds_transmittertest" =>
        chiselMainTest(test_args, () => Module(new dvi_tmds_transmitter())){
          c => new dvi_tmds_transmitterTest(c)
        }
      case "Serializertest" =>
        chiselMainTest(test_args, () => Module(new Serializer(3))) {
          c => new SerializerTest(c)
        }
      case "Soctest" =>
        chiselMainTest(test_args, () => Module(new Soc(
          rom,
          num_pin_inputs.toInt,
          num_pin_outputs.toInt
        ))){
          c => new SocTest(c)
        }
      case "Riscvtest" =>
        chiselMainTest(test_args, () => Module(new Soc(
          rom,
          num_pin_inputs.toInt, num_pin_outputs.toInt
        ))){
          c => new RiscvTest(c, target_dir)
        }
    }
  }
}
