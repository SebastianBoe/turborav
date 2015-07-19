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
    else RightRotate(RightRotate(word), shiftAmount - 1)
  }

  def apply(word: UInt): UInt = word(0) ## word(word.getWidth() - 1, 1)
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
