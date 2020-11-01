package rv32i_5stage.PipelineRegisters

import chisel3._
import common._
import common.CommonPackage._
import rv32i_5stage._

// デコードと実行ステージの間に挟むためのレジスタ群

class IDEX_REGS_Output extends Bundle{
  val ctrlEX = new CtrlEX
  val ctrlMEM = new CtrlMEM
  val ctrlWB = new CtrlWB

  val pc = Output(UInt(32.W))
  val rs1 = Output(UInt(32.W))
  val rs2 = Output(UInt(32.W))
  val rd_addr = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
  val csr_addr = Output(UInt(32.W))
}

class IDEX_REGS_IO extends Bundle {
  val in = Flipped(new IDEX_REGS_Output)
  val out = new IDEX_REGS_Output
}

class IDEX_REGS extends Module{
  val io = IO(new IDEX_REGS_IO)

  // レジスタたち
  val pc = Reg(UInt(32.W))
  val rs1 = Reg(UInt(32.W))
  val rs2 = Reg(UInt(32.W))
  val rd_addr = Reg(UInt(32.W))
  val csr_addr = Reg(UInt(12.W))
  val inst = Reg(UInt(32.W))
  val ctrl_execute_regs = new EX_Ctrl_Regs
  val ctrl_mem_regs = new MEM_Ctrl_Regs
  val ctrl_wb_regs = new WB_Ctrl_Regs

    pc := io.in.pc
    rs1 := io.in.rs1
    rs2 := io.in.rs2
    rd_addr := io.in.rd_addr
    csr_addr := io.in.csr_addr
    inst := io.in.inst
    ctrl_execute_regs.br_type := io.in.ctrlEX.br_type
    ctrl_execute_regs.op1_sel := io.in.ctrlEX.op1_sel
    ctrl_execute_regs.op2_sel := io.in.ctrlEX.op2_sel
    ctrl_execute_regs.imm_sel := io.in.ctrlEX.imm_sel
    ctrl_execute_regs.alu_fun := io.in.ctrlEX.alu_fun
    ctrl_mem_regs.dmem_en := io.in.ctrlMEM.dmem_en
    ctrl_mem_regs.dmem_wr := io.in.ctrlMEM.dmem_wr
    ctrl_mem_regs.dmem_mask := io.in.ctrlMEM.dmem_mask
    ctrl_mem_regs.csr_cmd := io.in.ctrlMEM.csr_cmd
    ctrl_wb_regs.wb_sel := io.in.ctrlWB.wb_sel
    ctrl_wb_regs.rf_wen := io.in.ctrlWB.rf_wen

  // 出力
  io.out.pc := pc
  io.out.rs1 := rs1
  io.out.rs2 := rs2
  io.out.rd_addr := rd_addr
  io.out.csr_addr := csr_addr
  io.out.inst := inst

  io.out.ctrlEX.br_type := ctrl_execute_regs.br_type
  io.out.ctrlEX.op1_sel := ctrl_execute_regs.op1_sel
  io.out.ctrlEX.op2_sel := ctrl_execute_regs.op2_sel
  io.out.ctrlEX.imm_sel := ctrl_execute_regs.imm_sel
  io.out.ctrlEX.alu_fun := ctrl_execute_regs.alu_fun
  io.out.ctrlMEM.dmem_en := ctrl_mem_regs.dmem_en
  io.out.ctrlMEM.dmem_wr := ctrl_mem_regs.dmem_wr
  io.out.ctrlMEM.dmem_mask := ctrl_mem_regs.dmem_mask
  io.out.ctrlMEM.csr_cmd := ctrl_mem_regs.csr_cmd
  io.out.ctrlWB.wb_sel := ctrl_wb_regs.wb_sel
  io.out.ctrlWB.rf_wen := ctrl_wb_regs.rf_wen

}

// レジスタ宣言
class EX_Ctrl_Regs{
  val br_type = Reg(UInt(BR_N.getWidth.W))
  val op1_sel = Reg(UInt(OP1_X.getWidth.W))
  val op2_sel = Reg(UInt(OP2_X.getWidth.W))
  val imm_sel = Reg(UInt(IMM_X.getWidth.W))
  val alu_fun = Reg(UInt(ALU_X.getWidth.W))
}