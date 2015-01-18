package TurboRav

import Chisel._

// This module contains constructs that are not specific to the
// TurboRav project but could be useful in any Chisel project.

// This seems to be a common pattern, but I need a better name for
// it I think. Surely a name for something this generic should
// already exist.
object clearIfDisabled {
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
