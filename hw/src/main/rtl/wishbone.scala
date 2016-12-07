// See LICENSE for license details.

package wishbone

import chisel3._
import chisel3.util._
import chisel3.testers._
import chisel3.core.SeqUtils
import scala.language.postfixOps

trait WishboneIp {
  def io: WishboneIO
}

trait WishboneSlave  extends WishboneIp {  def inAddressSpace(address: UInt): Bool }
trait WishboneMaster extends WishboneIp {                                          }

trait BusType
case object SharedBus extends BusType
case object Crossbar  extends BusType

object Config {
  // TODO: How can I do this cleanly without a global variable?

  /**
   * This config variable determines whether or not simulation-only
   * assertions for input validation should be included.
   */
  var pruneSimulationAssertions = false
}

class WishboneIO(portSize: Int = 32, granularity: Int = 8) extends Bundle {
/**
 * See wbspec_b4.pdf Chapter 2. Interface Specification.
 * */

  override def cloneType: this.type =
    new WishboneIO(portSize, granularity).asInstanceOf[this.type]

  val address      = Output(UInt.width(portSize))
  val dataToSlave  = Output(UInt.width(portSize)) // DAT_O on master, DAT_I on slave
  val dataToMaster = Input (UInt.width(portSize)) // DAT_I on master, DAT_O on slave
  val writeEnable  = Output(Bool())
  val select       = Output(Vec( portSize / granularity, Bool() ))
  val strobe       = Output(Bool())
  val ack          = Input (Bool())
  val cycle        = Output(Bool())

  // TODO: Understand and then support tag's
  // val tgd = UInt(OUTPUT, tagSize)
  // val tga = UInt(OUTPUT, tagSize)
  // val tgc = UInt(OUTPUT, tagSize)
}

object WishboneInterconnection
{
  // Convenience functions to be able to call this function with either a list or an element
  def apply(bus: BusType, master :     WishboneMaster , slave :     WishboneSlave ){ apply(bus, List(master), List(slave)) }
  def apply(bus: BusType, master :     WishboneMaster , slaves: Seq[WishboneSlave]){ apply(bus, List(master),      slaves) }
  def apply(bus: BusType, masters: Seq[WishboneMaster], slave :     WishboneSlave ){ apply(bus,      masters, List(slave)) }
  def apply(bus: BusType, masters: Seq[WishboneMaster], slaves: Seq[WishboneSlave]): Unit = {
    if(masters.isEmpty || slaves.isEmpty){
      // We need at least one master and at least one slave to
      // interconnect masters and slaves
      return
    }
    bus match {
      case SharedBus => WishboneSharedBusInterconnection(masters, slaves)
      case Crossbar  => WishboneCrossbarInterconnection (masters, slaves)
    }
  }
}

private object WishboneSharedBusInterconnection
{
  def apply(masters: Seq[WishboneMaster], slaves: Seq[WishboneSlave]): Unit = {
    // Determine which master should be granted the bus.
    def arbitrate(busRequests: Vec[Bool]) : UInt = {
      val grant       = Wire(UInt())
      val grant_valid = Wire(Bool())

      val grant_prev       = RegNext(grant)
      val grant_prev_valid = RegNext(grant_valid)

      val bus_is_requested = busRequests.contains(Bool(true))

      // HW for choosing a new master. The signal is unused when a
      // multi-cycle transfer is underway or when no masters are
      // requesting the bus. The HW is currently choosing the lowest
      // index master that is requesting the bus, so it is
      // implementing a priority arbiter, but it could easily be
      // modifed to do a safer strategy like round-robin.
      val highest_priority_master = busRequests.indexWhere(Bool(true) === _)

      when(grant_prev_valid) {
        // The bus was granted in the previous cycle
        when(busRequests(grant_prev)){
          // The bus is still being requested by the same master
          grant_valid := Bool(true)
          grant       := grant_prev
        } otherwise {
          // The bus is no longer being requested by the previous master

          grant       := highest_priority_master
          grant_valid := bus_is_requested
        }
      } otherwise {
        // The bus was free last cycle
        when(bus_is_requested) {
          // A new master has started requesting the bus

          grant       := highest_priority_master
          grant_valid := Bool(true)
        } otherwise {
          // No-one is requesting the bus
          grant := 0.U
          grant_valid := Bool(false)
        }
      }

      grant
    }

    // Utility structures
    val masterIos = masters.map(_.io)
    val masterRequestsVec = Vec(masterIos.map(_.cycle))

    val bus = Wire(new WishboneIO)
    val masterIndex = arbitrate(masterRequestsVec)
    bus := Vec(masterIos)(masterIndex)

    for (slave <- slaves) {
      // Default to connecting all of the slave's signals to the bus
      slave.io.address     := bus.address
      slave.io.dataToSlave := bus.dataToSlave
      slave.io.writeEnable := bus.writeEnable
      slave.io.select      := bus.select
      slave.io.cycle       := bus.cycle
      slave.io.strobe      := bus.strobe && slave.inAddressSpace(bus.address)
    }

    val slaveBus = Mux1H(
      for ( slave <- slaves)
        yield (
        slave.inAddressSpace(bus.address),
        slave.io
      )
    )

    for ( (masterIo, i) <- masterIos zipWithIndex){
      val master_has_been_granted = UInt(i) === masterIndex
      masterIo.ack := Mux(
        master_has_been_granted,
        slaveBus.ack,
        Bool(false)
      )
    }

    ////////////////////////////////////////////////////////////////
    //                  Input validation                          //
    ////////////////////////////////////////////////////////////////

    if(! wishbone.Config.pruneSimulationAssertions) {
      // The bus address triggers an address match for at most 1 slave
      val matches : Seq[Bool] = slaves.map(_.inAddressSpace(bus.address))
      val num_matches = PopCount(matches)
      Chisel.assert(num_matches <= 1.U, "The address space of slaves must not overlap.")

      // Slaves have to negate ACK_O when their STB_I is negated.
      for (slave <- slaves) {
        Chisel.assert(
          Mux(
            slave.io.strobe,
            Bool(true),
            slave.io.ack === Bool(false)
          ),
          "Slaves must negate ack when their strobe is negated."
        )
      }
    }
  }
}

private object WishboneCrossbarInterconnection
{
  def apply(masters: Seq[WishboneMaster], slaves: Seq[WishboneSlave]): Unit = {
    // To implement a crossbar between m masters and s slaves we use
    // the most naive implementation conceivable. We create a shared
    // bus for each master and a shared bus for each slave. And then
    // we hook up every masters shared bus to every slaves shared bus.

    // TODO: Read up on how to efficiently implement a crossbar.

    val master_to_slave_buses_map = masters map {
      m => (
        m,
        List.tabulate(slaves.length)(slaveIndex =>
          new WishboneSlave {
            val my_io = Wire(Flipped(new WishboneIO()))
            def io = my_io
            def inAddressSpace(address: UInt): Bool = slaves(slaveIndex).inAddressSpace(address)
          }
        )
      )
    } toMap

    val slave_to_master_buses_map = slaves map {
      s => (
        s,
        List.fill(masters.length)(
          new WishboneMaster {
            val my_io = Wire(new WishboneIO())
            def io = my_io
          }
        )
      )
    } toMap

    for (i <- 0 until masters.length ){
      for (j <- 0 until slaves.length ){
        val master = masters(i)
        val slave = slaves(j)
        val bus_master_slave = master_to_slave_buses_map(master)(j)
        val bus_slave_master = slave_to_master_buses_map(slave )(i)

        // The below code creates an inverted connection between a
        // slave and a master. In this case the slave controls the
        // master. The slave is only supposed to respond when it is
        // strobed, so we AND together the strobe and cycle signals.
        val b_m_s = bus_master_slave
        val b_s_m = bus_slave_master

        b_s_m.io.address      := b_m_s.io.address
        b_s_m.io.dataToSlave  := b_m_s.io.dataToSlave
        b_s_m.io.writeEnable  := b_m_s.io.writeEnable
        b_s_m.io.select       := b_m_s.io.select

        val valid = b_m_s.io.strobe && b_m_s.io.cycle
        b_s_m.io.strobe       := valid
        b_s_m.io.cycle        := valid

        b_m_s.io.dataToMaster := b_s_m.io.dataToMaster
        b_m_s.io.ack          := b_s_m.io.ack
      }
    }

    for (master <- masters)
      WishboneInterconnection(
        SharedBus,
        master,
        master_to_slave_buses_map(master)
      )

    for (slave <- slaves)
      WishboneInterconnection(
        SharedBus,
        slave_to_master_buses_map(slave),
        slave
      )
  }
}
