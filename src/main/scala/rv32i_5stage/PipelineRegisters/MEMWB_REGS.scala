package rv32i_5stage.PipelineRegisters

import chisel3._
import rv32i_5stage._

class MEMWB_REGS_Output extends Bundle{
  val rf_wdata = Output(UInt(32.W))
  val ctrlWB = new CtrlWB
  val rs2 = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class MEMWB_REGS_IO extends Bundle{
  val in = Flipped(new MEMWB_REGS_Output)
  val out = new MEMWB_REGS_Output
}

class MEMWB_REGS extends Module{
  val io = IO(new MEMWB_REGS_IO)

  val rf_wdata = Reg(UInt(32.W))
  val ctrl_wb_regs = new WB_Ctrl_Regs
  val inst = Reg(UInt(32.W))

  // 入力
  rf_wdata := io.in.rf_wdata
  ctrl_wb_regs.rf_wen := io.in.ctrlWB.rf_wen
  inst := io.in.inst

  // 出力
  io.out.rf_wdata := rf_wdata
  io.out.ctrlWB.rf_wen := ctrl_wb_regs.rf_wen
  io.out.rs2 := io.in.rs2
  io.out.inst := inst

}


class WB_Ctrl_Regs{
  val rf_wen  = Reg(Bool())
}