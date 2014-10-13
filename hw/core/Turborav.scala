package TurboRav

import Chisel._

object TurboRav {
    def main(args: Array[String]): Unit = { 
        val mainArgs = args.slice(1, args.length)
        chiselMainTest(mainArgs, () => Module(new Alu())) {
            c => new AluTests(c)
        }
    }
}
