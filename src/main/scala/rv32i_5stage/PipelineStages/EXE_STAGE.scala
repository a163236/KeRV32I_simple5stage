package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage.PipelineRegisters._

class EXE_STAGE_IO extends Bundle{
  val in = Flipped(new IDEX_REGS_Output)
  val out = new EXMEM_REGS_Output
}

class EXE_STAGE(implicit val conf: Configurations) extends Module{
  val io = IO(new EXE_STAGE_IO)

  io.out.rs2 := io.in.rs2
  io := DontCare
  val ALU = Module(new ALU())
  val BranchComp = Module(new BranchComp())
  val ImmGen = Module(new ImmGen())

  // 入力
  BranchComp.io.rs1_data := io.in.inst(RS1_MSB, RS1_LSB)
  BranchComp.io.rs2_data := io.in.inst(RS2_MSB, RS2_LSB)
  ImmGen.io.inst := io.in.inst
  ImmGen.io.imm_sel := io.in.ctrlEX.imm_sel

  ALU.io.op1 := io.in.ctrlEX.op1_sel
  ALU.io.op1 := Mux(io.in.ctrlEX.op1_sel===OP1_RS1, io.in.rs1, io.in.pc)
  ALU.io.op2 := Mux(io.in.ctrlEX.op2_sel===OP2_RS2, io.in.rs2, ImmGen.io.out)
  ALU.io.fun := io.in.ctrlEX.alu_fun

  // 出力
  // branchの出力はまだ
  io.out.pc := io.in.pc
  io.out.alu := ALU.io.out
  io.out.rs2 := io.in.rs2
  io.out.inst := io.in.inst
  io.out.ctrlMEM <> io.in.ctrlMEM
  io.out.ctrlWB <> io.in.ctrlWB
}
