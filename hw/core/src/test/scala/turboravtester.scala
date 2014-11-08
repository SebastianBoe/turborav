package TurboRav

import Chisel._
import Common._

object TurboRavTestRunner{
  def main(args: Array[String]): Unit = {

    val module = if (args.length > 0) args(0) else "ravvtest"

    val mainArgs = args.slice(1, args.length)

    // En pils til førstemann som kan fjerne redundansen.
    // Hvis du prøvde og feilet inkrementer følgende:
    // 2
    val res = module match {
      case "alutest" =>
        chiselMainTest(mainArgs, () => Module(new Alu())){
          c => new AluTest(c)
        }
      case "brutest" =>
        chiselMainTest(mainArgs, () => Module(new BranchUnit())){
          c => new BranchUnitTest(c)
        }
      case "regbanktest" =>
        chiselMainTest(mainArgs, () => Module(new RegBank())){
          c => new RegBankTest(c)
        }
      case "romtest" =>
        chiselMainTest(mainArgs, () => Module(new Rom())){
          c => new RomTest(c)
        }
      case "multtest" =>
        chiselMainTest(mainArgs, () => Module(new Mult())){
          c => new MultTest(c)
        }
      case "decodetest" =>
        chiselMainTest(mainArgs, () => Module(new Decode())){
          c => new DecodeTest(c)
        }
      case "executetest" =>
        chiselMainTest(mainArgs, () => Module(new Execute())){
          c => new ExecuteTest(c)
        }
      case "memorytest" =>
        chiselMainTest(mainArgs, () => Module(new Memory())){
          c => new MemoryTest(c)
        }
      case "writebacktest" =>
      chiselMainTest(mainArgs, () => Module(new Writeback())){
        c => new WritebackTest(c)
      }
      case "ravvtest" =>
        chiselMainTest(mainArgs, () => Module(new RavV())){
          c => new RavVTest(c)
        }
    }
  }
}
