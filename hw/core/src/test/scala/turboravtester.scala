package TurboRav

import Chisel._
import Common._

object TurboRavTestRunner{
  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "decode"
    if (args.length >= 1) args + "--backend c" + "--genHarness"

    val mainArgs = args.slice(1, args.length)
    implicit val conf = TurboravConfig()

    // En pils til førstemann som kan fjerne redundansen.
    // Hvis du prøvde og feilet inkrementer følgende: 1
    val res = args(0) match {
      case "alutest" =>
        chiselMainTest(mainArgs, () => Module(new Alu())){
          c => new AluTest(c, conf)
        }
      case "regbanktest" =>
        chiselMainTest(mainArgs, () => Module(new RegBank())){
          c => new RegBankTest(c, conf)
        }
      case "romtest" =>
        chiselMainTest(mainArgs, () => Module(new Rom())){
          c => new RomTest(c, conf)
        }
      case "multtest" =>
        chiselMainTest(mainArgs, () => Module(new Mult(32))){
          c => new MultTest(c)
        }
      case "decodetest" =>
        chiselMainTest(mainArgs, () => Module(new Decode())){
          c => new DecodeTest(c)
        }
    }
  }
}
