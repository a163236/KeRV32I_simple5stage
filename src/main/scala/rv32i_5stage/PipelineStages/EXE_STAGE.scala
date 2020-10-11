package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage.{FWEXE_IO, Hazard_EXE_StageIO}
import rv32i_5stage.PipelineRegisters._

class EXEtoIFjumpsignalsIO extends Bundle{
  val aluout = Output(UInt(32.W))
  val branchbool = Output(Bool())
}

class EXE_STAGE_IO extends Bundle{
  val in = Flipped(new IDEX_REGS_Output)
  val out = new EXMEM_REGS_Output
  val exetoifjumpsignals = new EXEtoIFjumpsignalsIO
  val pipe_flush = Output(Bool()) // フラッシュ
  val fwUnit = Flipped(new FWEXE_IO)  // フォワーディング
  val hazard = Flipped(new Hazard_EXE_StageIO) // ハザード
}


class EXE_STAGE(implicit val conf: Configurations) extends Module{
  val io = IO(new EXE_STAGE_IO)

  io.out.rs2 := io.in.rs2
  io := DontCare
  val ALU = Module(new ALU())
  val BranchComp = Module(new BranchComp())
  val ImmGen = Module(new ImmGen())

  // 入力
  BranchComp.io.rs1_data := io.in.rs1
  BranchComp.io.rs2_data := io.in.rs2
  ImmGen.io.inst := io.in.inst
  ImmGen.io.imm_sel := io.in.ctrlEX.imm_sel


  val rs1 = Wire(UInt(32.W))
  val rs2 = Wire(UInt(32.W))
  rs1 := MuxLookup(io.fwUnit.rs1_sel, io.in.rs1, Array(
    FW1_X -> io.in.rs1,
    FW1_MEM -> io.fwUnit.mem_bypassdata,
    FW1_WB -> io.fwUnit.wb_bypassdata
  ))
  rs2 := MuxLookup(io.fwUnit.rs2_sel, io.in.rs2, Array(
    FW2_X -> io.in.rs2,
    FW2_MEM -> io.fwUnit.mem_bypassdata,
    FW2_WB -> io.fwUnit.wb_bypassdata
  ))
  ALU.io.op1 := Mux(io.in.ctrlEX.op1_sel===OP1_RS1, rs1, io.in.pc)
  ALU.io.op2 := Mux(io.in.ctrlEX.op2_sel===OP2_RS2, rs2, ImmGen.io.out)
  ALU.io.fun := io.in.ctrlEX.alu_fun


  // フォワーディングのアドレス
  val rs1_addr = io.in.inst(RS1_MSB, RS1_LSB)
  val rs2_addr = io.in.inst(RS2_MSB, RS2_LSB)
  io.fwUnit.rs1_addr := rs1_addr
  io.fwUnit.rs2_addr := rs2_addr
  // ハザードのアドレス
  io.hazard.rs1_addr := rs1_addr
  io.hazard.rs2_addr := rs2_addr
  // 出力

  io.exetoifjumpsignals.aluout := ALU.io.out
  io.exetoifjumpsignals.branchbool := MuxLookup(io.in.ctrlEX.br_type, false.B, Array(
    BR_N  -> false.B,
    BR_J  -> true.B,
    BR_NE -> Mux(!BranchComp.io.branComOut.br_eq, true.B, false.B),
    BR_EQ -> Mux( BranchComp.io.branComOut.br_eq, true.B, false.B),
    BR_GE -> Mux(!BranchComp.io.branComOut.br_lt, true.B, false.B),
    BR_GEU-> Mux(!BranchComp.io.branComOut.br_ltu,true.B, false.B),
    BR_LT -> Mux( BranchComp.io.branComOut.br_lt, true.B, false.B),
    BR_LTU-> Mux( BranchComp.io.branComOut.br_ltu,true.B, false.B),
    BR_J  -> true.B,
    BR_JR -> true.B))
  io.pipe_flush := io.exetoifjumpsignals.branchbool // 分岐するならフラッシュするから
  io.out.pc := io.in.pc
  io.out.alu := ALU.io.out
  io.out.rs2 := rs2
  io.out.inst := io.in.inst
  io.out.ctrlMEM <> io.in.ctrlMEM
  io.out.ctrlWB <> io.in.ctrlWB


}
