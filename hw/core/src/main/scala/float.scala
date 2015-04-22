package TurboRav

import Chisel._
import Constants._
import Common._

class Float() extends Module {
  val xlen      = Config.xlen // Extract xlen for convenience
  val len_fract = 23
  val len_exp   = 8 
  require(isPow2(xlen))

  val io = new Bundle(){
    // D: val in_fmt = UInt(INPUT, xlen)
    val in_func       = UInt(INPUT, 5)
    val in_fmt        = UInt(INPUT, 2)
    val in_rm         = UInt(INPUT, 3) // rounding mode
    val in_a_sign     = UInt(INPUT, 1)
    val in_a_exp      = UInt(INPUT, 8)
    val in_a_fract    = UInt(INPUT, 23)
    val in_b_sign     = UInt(INPUT, 1)
    val in_b_exp      = UInt(INPUT, 8)
    val in_b_fract    = UInt(INPUT, 23)
    val out_res       = UInt(INPUT, 32)
    val out_done      = Bool(OUTPUT)
  }

  val exp_diff_a_b      = UInt(width=len_exp) // represents small ALU
  val a_exp_gt_b_exp    = Bool(false)    // To for MUX input
  val sr_in             = UInt(width = 23)
  val ctrl_shamt        = UInt(width = 3) // TODO do logarithm do dynamically decide width
  val ctrl_exp_sel_b    = Bool(false)
  val ctrl_fract_sel_b  = Bool(false)
  val ctrl_round        = UInt(width = 5) // TODO do logarithm do dynamically decide width
  val ctrl_exp_inc      = Bool(false) 
  val ctrl_inc_sel_ro   = Bool(false) // Select output from rounding hardware for Inc device
  val ctrl_pst_alu_shlft= Bool(false)
  val ctrl_sel_rounded_for_shift = Bool(false)
  val ctrl_fract_shift_in_sel_b  = Bool(false)

  exp_diff_a_b   := io.in_a_exp - io.in_b_exp
  a_exp_gt_b_exp := exp_diff_a_b < UInt(0)

  when (exp_diff_a_b < UInt(0)) {
    exp_diff_a_b := exp_diff_a_b * SInt(-1)
  }

//  sr_in = Mux(a_exp_gt_b_exp, io.in_a_fract, io.in_b_fract)
  


}
