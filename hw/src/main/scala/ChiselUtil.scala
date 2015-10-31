package TurboRav

import Chisel._
import scala.annotation.tailrec

// This module contains constructs that are not specific to the
// TurboRav project but could be useful in any Chisel project.

// This seems to be a common pattern, but I need a better name for
// it I think. Surely a name for something this generic should
// already exist.
object ClearIfDisabled {
  def apply(data: UInt, enabled: Bool):UInt = {
    data & Fill(enabled, data.getWidth())
  }
}

// Has the same signature as Cat, except that it returns a
// Bool. Returns true iff any bit given as an argument was high.
object Any {
  def apply[T <: Data](mod: T, mods: T*): Bool = apply(mod :: mods.toList)
  def apply[T <: Data](mods: Seq[T]):     Bool = orR(Cat(mods))
}

/*
 * If you have a simple scala function with the type (Node => Node)
   and you want to verify that your function only creates a
   permutation of the input (it does not change width) then you can
   use this partially applied function to add error checking to your
   function.
 * */
object permutation {
  def apply[T <: Node](arg: T)(f: T => T): T = {
    val width_before = arg.getWidth()
    val result = f(arg)
    val width_after = result.getWidth()
    require(width_before == width_after)
    result
  }
}

object RightRotate {
  /**
   * Returns the circular right shift of "shiftAmount" bits of the UInt "word".
   * @param word The UInt to be right rotated.
   * @param shiftAmount The number of bits to be shifted.
   * @return A bitwise right rotation.
   */
  @tailrec
  def apply(word: UInt, shiftAmount: Int): UInt = {
    if (shiftAmount == 0) word
    else RightRotate(
      RightRotate(word),
      shiftAmount - 1
    )
  }

  def apply(word: UInt): UInt = permutation(word) {
    (w: UInt) => Cat(w(0), w(w.getWidth() - 1, 1))
  }
}

object LeftRotate {
  /**
   * Returns the circular left shift of "shiftAmount" bits of the UInt "word".
   * @param word The UInt to be left rotated.
   * @param shiftAmount The number of bits to be shifted.
   * @return A bitwise left rotation.
   */
  def apply(word: UInt, shiftAmount: Int): UInt = {
    // A left shift rotate is just a "negative" right shift rotate.
    RightRotate(word, word.getWidth() - shiftAmount)
  }

  def apply(word: UInt): UInt = LeftRotate(word, 1)
}

/**
  A serializer is used when you have a bus of values, but need to
  output each value in the bus bit by bit, AKA serially.

  Usage:

  val s = Module(new Serializer(bus.getWidth))
  s.io.in := bus
  s.io.cond := enable // Internal counter in Serializer ticks on this signal
  serial_output := s.io.out

  Future features:

  Ability to clock out a bus instead of just Bool's.
  */
class Serializer(w: Int) extends Module {
  val io = new Bundle {
    val in = UInt(INPUT, w)
    val cond = Bool(INPUT)
    val out = Bool(OUTPUT)
  }
  val cnt = Counter(cond = io.cond, n = w)
  io.out := io.in(cnt._1)
}

object Extend {
  def apply(word: UInt, extention_val: Bool, new_length: Int): UInt =
    Cat(
      Fill(extention_val, new_length - word.getWidth()),
      word
    )
}

object SignExtend {
  def apply(word: UInt, new_length: Int): UInt =
    Extend(word, extention_val = word(word.getWidth() - 1), new_length)
}

object ZeroExtend {
  def apply(word: UInt, new_length: Int): UInt =
    Extend(word, extention_val = Bool(false), new_length)
}

object splitBitsIntoVec {
  def apply(bits: UInt, vec_length: Int): Vec[UInt] = {
    val vec_element_width = bits.getWidth / vec_length
    assert(bits.getWidth % vec_length == 0)
    val indices = 0 to (bits.getWidth - vec_element_width) by vec_element_width
    Vec(for (i <- indices) yield bits(i + vec_element_width - 1, i))
  }
}
