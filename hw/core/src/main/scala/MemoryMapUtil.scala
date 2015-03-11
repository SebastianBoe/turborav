package TurboRav

import Chisel._
import Common._
import Constants._

// Utility functions for working with the memory map.

// The 4 most significant bits divide the memory map into 16 segments.
// Only the three lowest segments have defined meaning. The lowest
// segment is ROM, followed by RAM, and then memory mapped IO (AKA
// peripherals).

// Bit 31,30,29,28
//      0, 0, 0, 0 // ROM memory map
//      0, 0, 0, 1 // RAM memory map
//      0, 0, 1, 0 // MMIO // Coming in the future.
//      otherwise  // Reserved for future use

// See the following 32 bit addresses as an example
// 0x0000_0000 // Rom address
// 0x1000_0000 // Ram address
// 0x2000_0000 // Peripheral hardware register address
// 0x3000_0000 // Reserved

// Get the Memory map offsets from Constants.BASE_ADDR_XXX

object getMemorySegment {
  def apply(address: UInt):UInt = {
    address(31, 28)
  }
}

object isApbAddress {
  def apply(address: UInt):Bool = {
    getMemorySegment(address) === MEMORY_SEGMENT_APB
  }
}

object isRamAddress {
  def apply(address: UInt):Bool = {
    getMemorySegment(address) === MEMORY_SEGMENT_RAM
  }
}

object isSpiAddress {
  def apply(address: UInt):Bool = {
    getMemorySegment(address) === MEMORY_SEGMENT_SPI
  }
}

