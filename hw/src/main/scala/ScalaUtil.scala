// Copyright (C) 2015 Sebastian Bøe, Joakim Andersson
// License: BSD 2-Clause (see LICENSE for details)

package TurboRav

// This module contains constructs that are not specific to the
// TurboRav project but could be useful in any Scala project.

// http://stackoverflow.com/a/5765278
trait ScalaUtil {
  def hex2dec(hex: String): BigInt = {
    // There has to be a better way to do this in scala, i don't
    // understand why BigInt("81923ba", 16) didn't work.
    hex
      .toLowerCase()
      .toList
      .filter(_ != '_') // Ignore _ characters
      .map("0123456789abcdef".indexOf(_))
      .map(BigInt(_))
      .reduceLeft( _ * 16 + _)
  }

  def b(digits: String) = Integer.parseInt(digits, 2)
}
object ScalaUtil extends ScalaUtil
