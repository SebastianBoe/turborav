package TurboRav

import Chisel._

object TurboRavTestRunner{

  def main(args: Array[String]): Unit = {
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
    }
  }

}
