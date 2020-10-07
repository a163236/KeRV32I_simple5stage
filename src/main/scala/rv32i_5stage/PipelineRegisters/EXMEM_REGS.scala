package rv32i_5stage.PipelineRegisters

import chisel3._
import common.CommonPackage._
import rv32i_5stage._

class EXMEM_REGS_Output extends Bundle{
  val ctrlMEM = new CtrlMEM
  val ctrlWB = new CtrlWB

  val pc = Output(UInt(32.W))
  val alu = Output(UInt(32.W))
  val rs2 = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class EXMEM_REGS_IO extends Bundle{
  val in = Flipped(new EXMEM_REGS_Output)
  val out = new EXMEM_REGS_Output
}

class EXMEM_REGS extends Module{
  val io = IO(new EXMEM_REGS_IO)

  val pc = Reg(UInt(32.W))
  val rs2 = Reg(UInt(32.W))
  val alu = Reg(UInt(32.W))
  val inst = Reg(UInt(32.W))
  val ctrl_mem_regs = new MEM_Ctrl_Regs
  val ctrl_wb_regs = new WB_Ctrl_Regs

  // 入力
  pc := io.in.pc
  alu := io.in.alu
  rs2 := io.in.rs2
  inst := io.in.inst
  ctrl_mem_regs.dmem_en := io.in.ctrlMEM.dmem_en
  ctrl_mem_regs.dmem_wr := io.in.ctrlMEM.dmem_wr
  ctrl_mem_regs.dmem_mask := io.in.ctrlMEM.dmem_mask
  ctrl_wb_regs.rf_wen := io.in.ctrlWB.rf_wen

  // 出力
  io.out.pc := pc
  io.out.alu := alu
  io.out.rs2 := rs2
  io.out.inst := inst
  io.out.ctrlMEM.dmem_en := ctrl_mem_regs.dmem_en
  io.out.ctrlMEM.dmem_wr := ctrl_mem_regs.dmem_wr
  io.out.ctrlMEM.dmem_mask := ctrl_mem_regs.dmem_mask
  io.out.ctrlWB.rf_wen := ctrl_wb_regs.rf_wen

}

class MEM_Ctrl_Regs{
  val dmem_en = Reg(Bool())
  val dmem_wr = Reg(Bool())
  val dmem_mask = Reg(UInt(MT_X.getWidth.W))
}