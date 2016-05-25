package TurboRav

import Chisel._
import org.apache.commons.io.FilenameUtils;

object TurboRavTestRunner {

  val default_gpio_input_pins  = 4
  val default_gpio_output_pins = 4
  val default_fpga             = true
  val default_max_cycles       = Int.MaxValue

  val help = s"""
Usage: scala TurboRav.TurboRavTestRunner [OPTIONS] COMMAND

COMMAND:
  See Turboravtester.scala for available commands.

Options:
  -i <int>, --gpio-input-pins <int>
    The number of GPIO input pins to instantiate
    Default: $default_gpio_input_pins

  -o <int>, --gpio-output-pins <int>
    The number of GPIO output pins to instantiate
    Default: $default_gpio_output_pins

  -r <file>, --rom <file>
    Synthesize the elf file <file> into the boot ROM. The size of the
    HW ROM will dynamically increase (at elaboration time) to fit the
    elf program.

  -t <dir>, --target-directory <dir>
    Put generated files in <dir>.

  --fpga, --no-fpga
    Configure the build for FPGA synthesis.
    Default: --fpga

  -m <int>, --max-cycles <int>
    The maximum number of cycles to be simulated. (Only applies to
    Riscvtest).
    Default $default_max_cycles
"""
  def main(args: Array[String]) {
    if (args.length == 0) {
      println(help)
      sys.exit(1)
    }

    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      list match {
        case Nil => map

        case "-i"                 :: value :: tail => nextOption(map ++ Map('i          -> value.toInt ), tail)
        case "--gpio-input-pins"  :: value :: tail => nextOption(map ++ Map('i          -> value.toInt ), tail)
        case "-o"                 :: value :: tail => nextOption(map ++ Map('o          -> value.toInt ), tail)
        case "--gpio-output-pins" :: value :: tail => nextOption(map ++ Map('o          -> value.toInt ), tail)
        case "-m"                 :: value :: tail => nextOption(map ++ Map('m          -> value.toInt ), tail)
        case "--max-cycles"       :: value :: tail => nextOption(map ++ Map('m          -> value.toInt ), tail)
        case "-r"                 :: value :: tail => nextOption(map ++ Map('rom        -> value       ), tail)
        case "--rom"              :: value :: tail => nextOption(map ++ Map('rom        -> value       ), tail)
        case "-t"                 :: value :: tail => nextOption(map ++ Map('target_dir -> value       ), tail)
        case "--target-directory" :: value :: tail => nextOption(map ++ Map('target_dir -> value       ), tail)
        case "--fpga"             ::          tail => nextOption(map ++ Map('fpga       -> true        ), tail)
        case "--no-fpga"          ::          tail => nextOption(map ++ Map('fpga       -> false       ), tail)
        case string               :: Nil           => nextOption(map ++ Map('module     -> string      ), list.tail)

        case option               ::          tail => println(help + "Unknown option " + option); sys.exit(1)
      }
    }

    val options = nextOption(Map(), args.toList)

    val target_dir      = options.get('target_dir).orNull.toString
    val module          = options('module)
    val rom             = options('rom).asInstanceOf[String]
    val num_pin_inputs  = options.getOrElse('i, default_gpio_input_pins ).asInstanceOf[Int]
    val num_pin_outputs = options.getOrElse('o, default_gpio_output_pins).asInstanceOf[Int]
    val max_cycles      = options.getOrElse('m, default_max_cycles      ).asInstanceOf[Int]
    val fpga            = options.getOrElse('fpga, default_fpga).asInstanceOf[Boolean]

    val test_args = Array(
      "--genHarness",
	  "--backend", "c",
	  "--targetDir", target_dir,
	  "--compile",
	  "--test",
	  "--vcd",
	  "--vcdMem",
	  "--debug"
    )

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
      case "ApbControllertest" =>
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
          num_pin_inputs,
          num_pin_outputs,
          fpga
        ))){
          c => new SocTest(c)
        }
      case "Riscvtest" =>
        chiselMainTest(test_args, () => Module(new Soc(
          rom,
          num_pin_inputs,
          num_pin_outputs,
          fpga
        ))){
          c => new RiscvTest(c, target_dir, max_cycles)
        }
      case "FpgaRamtest" =>
        chiselMainTest(test_args, () => Module(new FpgaRam())) {
          c => new FpgaRamTest(c)
        }
      case "GpioHybridtest" =>
        chiselMainTest(test_args, () => Module(new Soc(
          rom,
          num_pin_inputs,
          num_pin_outputs,
          fpga
        ))){
          c => new GpioHybridTest(c, target_dir)
        }
      case "GpioHybridToggletest" =>
        chiselMainTest(test_args, () => Module(new Soc(
          rom,
          num_pin_inputs,
          num_pin_outputs,
          fpga
        ))){
          c => new GpioHybridToggleTest(c, target_dir)
        }
      case "Gpiotest" =>
        chiselMainTest(test_args, () => Module(new Gpio(
          num_pin_inputs,
          num_pin_outputs
        ))){
          c => new GpioTest(c)
        }
      case "Roamtest" =>
        chiselMainTest(test_args, () => Module(new Roam(
          rom,
          fpga
        ))){
          c => new RoamTest(c)
        }
      case "HazardDetectionUnittest" =>
        chiselMainTest(test_args, () => Module(new HazardDetectionUnit())){
          c => new HazardDetectionUnitTest(c)
        }
      case "Fetchtest" =>
        chiselMainTest(test_args, () => Module(new Fetch())){
          c => new FetchTest(c)
        }
    }
    //TODO: simplify scala command by doing waveform tricks here?
  }
}
