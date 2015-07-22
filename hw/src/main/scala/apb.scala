package TurboRav

// This package contains the constructs needed to connect to an APB
// bus. See ARM's documentation of APB for the signal meanings and
// protocol spec.

import Chisel._

class SlaveToApbIo() extends Bundle {
  val addr  =  UInt(INPUT, Config.apb_addr_len)
  val wdata =  UInt(INPUT, Config.apb_data_len)
  val write =  Bool(INPUT)
  val sel   =  Bool(INPUT)

  val enable = Bool(INPUT)
  val rdata  = UInt(OUTPUT, Config.apb_data_len)
  val ready  = Bool(OUTPUT)
}

class ApbController() extends Module {

  val io = new Bundle(){
    val rr = new RequestResponseIo().flip()

    val apb_addr   = UInt(OUTPUT, Config.apb_addr_len)
    val apb_wdata  = UInt(OUTPUT, Config.apb_data_len)
    val apb_write  = Bool(OUTPUT)
    val apb_selx   = Bits(OUTPUT, 16) // 16 peripherals

    val apb_enable = Bool(OUTPUT)
    val apb_rdata  = UInt(INPUT,  Config.apb_data_len)
    val apb_ready  = Bool(INPUT)
  }

  val s_idle :: s_setup :: s_transfer :: Nil = Enum(UInt(), 3)

  val state    = Reg(init = s_idle)
  val addr     = Reg(init = UInt(0))
  val wdata    = Reg(init = UInt(0))
  val selx     = Reg(init = UInt(0))
  val write    = Reg(init = Bool(false))

  private def setup() = {
    write := io.rr.request.bits.write
    addr  := io.rr.request.bits.addr(15, 0)
    selx  := io.rr.request.bits.addr(27, 16)
    wdata := io.rr.request.bits.wdata
  }

  val initiate = io.rr.request.valid
  val terminate = io.apb_ready

  when(state === s_idle && initiate){
    state := s_setup
    setup()
  }

  when(state === s_setup) {
    state := s_transfer
  }

  when (state === s_transfer && terminate){
    when(initiate && selx === io.rr.request.bits.addr(27, 16)){
      state := s_setup
      setup()
    }.otherwise{
      state := s_idle
    }
  }

  /* Output APB controller signals on clock edge */
  when(state === s_idle){
    io.apb_addr   := UInt(0)
    io.apb_wdata  := UInt(0)
    io.apb_enable := Bool(false)
    io.apb_write  := Bool(false)
    io.apb_selx   := Bits(0)
  } .otherwise {
    io.apb_addr   := addr
    io.apb_wdata  := Mux(write, wdata, UInt(0))
    io.apb_write  := write
    io.apb_enable := state === s_transfer
    io.apb_selx   := UIntToOH(selx)
  }

  val valid = state === s_transfer && terminate
  val read_valid = !write && valid

  io.rr.response.valid := valid
  io.rr.response.bits.word  := Mux(read_valid, io.apb_rdata, UInt(0))
}
