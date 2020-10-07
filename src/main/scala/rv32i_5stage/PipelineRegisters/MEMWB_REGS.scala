package rv32i_5stage.PipelineRegisters

import chisel3._
import common.CommonPackage._
import rv32i_5stage._

class MEMWB_REGS_Output extends Bundle{
  val pc_plus4 = Output(UInt(32.W))
  val alu = Output(UInt(32.W))
  val rdataD = Output(UInt(32.W))
  val ctrlWB = new CtrlWB
  val inst = Output(UInt(32.W))
}

class MEMWB_REGS_IO extends Bundle{
  val in = Flipped(new MEMWB_REGS_Output)
  val out = new MEMWB_REGS_Output
}

class MEMWB_REGS extends Module{
  val io = IO(new MEMWB_REGS_IO)

  val pc_plus4 = Reg(UInt(32.W))
  val alu = Reg(UInt(32.W))
  val rf_wdata = Reg(UInt(32.W))
  val ctrl_wb_regs = new WB_Ctrl_Regs
  val inst = Reg(UInt(32.W))

  // 入力
  pc_plus4 := io.in.pc_plus4
  alu := io.in.alu
  rf_wdata := io.in.alu
  ctrl_wb_regs.wb_sel := io.in.ctrlWB.wb_sel
  ctrl_wb_regs.rf_wen := io.in.ctrlWB.rf_wen
  inst := io.in.inst

  // 出力
  io.out.pc_plus4 := pc_plus4
  io.out.alu := alu
  io.out.rdataD := io.in.rdataD
  io.out.ctrlWB.wb_sel := ctrl_wb_regs.wb_sel
  io.out.ctrlWB.rf_wen := ctrl_wb_regs.rf_wen
  io.out.inst := inst

}


class WB_Ctrl_Regs{
  val wb_sel = Reg(UInt(WB_X.getWidth.W))
  val rf_wen  = Reg(Bool())
}