package TurboRav

import Chisel._

object TurboRavTestRunner{

  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "decode"
    if (args.length >= 1) args + "--backend c" + "--genHarness"

    val mainArgs = args.slice(1, args.length)
    val res = args(0) match {
      case "alutest" =>
        chiselMainTest(mainArgs, () => Module(new Alu(32))){
          c => new AluTest(c)
        }
      case "regbanktest" =>
        chiselMainTest(mainArgs, () => Module(new RegBank(32))){
          c => new RegBankTest(c)
        }
      case "multtest" =>
        chiselMainTest(mainArgs, () => Module(new Mult(32))){
          c => new MultTest(c)
        }
      case "decodetest" =>
        chiselMainTest(mainArgs, () => Module(new Decode(32))){
          c => new DecodeTest(c)
        }
    }
  }

}
