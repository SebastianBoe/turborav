package TurboRav

import Chisel._

object TurboRav {
    def main(args: Array[String]): Unit = { 
        val mainArgs = args.slice(1, args.length)
        val res = args(0) match {
            case "alu" =>
                chiselMain(mainArgs, () => Module(new Alu(32))) 
        }
        
    }
}

