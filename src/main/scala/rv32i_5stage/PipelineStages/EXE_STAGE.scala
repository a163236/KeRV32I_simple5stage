package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage.{FWEXE_IO, Hazard_IDEX_IO}
import rv32i_5stage.PipelineRegisters._

class EXEtoIFjumpsignalsIO extends Bundle{
  val aluout = Output(UInt(32.W))
  val branchbool = Output(Bool())
}

class EXE_STAGE_IO extends Bundle{
  val in = Flipped(new IDEX_REGS_Output)
  val out = new EXMEM_REGS_Output
  val exetoifjumpsignals = new EXEtoIFjumpsignalsIO
  val flush_in = Input(Bool()) // フラッシュ
  val fwUnit = Flipped(new FWEXE_IO)  // フォワーディング
}


class EXE_STAGE(implicit val conf: Configurations) extends Module{
  val io = IO(new EXE_STAGE_IO)

  io := DontCare
  val ALU = Module(new ALU())
  val BranchComp = Module(new BranchComp())

  // フォワーディング
  io.fwUnit.rs1_addr := io.in.rs1_addr
  io.fwUnit.rs2_addr := io.in.rs2_addr
  val ex_rs1 = MuxLookup(io.fwUnit.rs1_sel, io.in.rs1, Array(
    FW1_X -> io.in.rs1,
    FW1_MEM -> io.fwUnit.mem_bypassdata,
    FW1_WB -> io.fwUnit.wb_bypassdata
  ))
  val ex_rs2 = MuxLookup(io.fwUnit.rs2_sel, io.in.rs2, Array(
    FW2_X -> io.in.rs2,
    FW2_MEM -> io.fwUnit.mem_bypassdata,
    FW2_WB -> io.fwUnit.wb_bypassdata
  ))
  // 入力
  BranchComp.io.rs1_data := ex_rs1
  BranchComp.io.rs2_data := ex_rs2
  ALU.io.op1 := Mux(io.in.ctrlEX.op1_sel===OP1_RS1, ex_rs1, io.in.pc)
  ALU.io.op2 := Mux(io.in.ctrlEX.op2_sel===OP2_RS2, ex_rs2, MuxLookup(
    io.in.ctrlEX.imm_sel, 0.U(32.W), Array(
      IMM_X -> 0.U(conf.xlen.W),
      IMM_I -> io.in.immediates.i_type_imm,
      IMM_S -> io.in.immediates.s_type_imm,
      IMM_B -> io.in.immediates.b_type_imm,
      IMM_U -> io.in.immediates.u_type_imm,
      IMM_J -> io.in.immediates.j_type_imm
    )
  ))
  ALU.io.fun := io.in.ctrlEX.alu_fun
  // 出力
  io.exetoifjumpsignals.aluout := ALU.io.out
  // 分岐するかどうか
  io.exetoifjumpsignals.branchbool := MuxLookup(io.in.ctrlEX.br_type, false.B, Array(
    BR_N  -> false.B,
    //BR_J  -> true.B,  絶対ジャンプ命令はすでにIDで実行済み
    //BR_JR -> true.B,
    BR_NE -> Mux(!BranchComp.io.branComOut.br_eq, true.B, false.B),
    BR_EQ -> Mux( BranchComp.io.branComOut.br_eq, true.B, false.B),
    BR_GE -> Mux(!BranchComp.io.branComOut.br_lt, true.B, false.B),
    BR_GEU-> Mux(!BranchComp.io.branComOut.br_ltu,true.B, false.B),
    BR_LT -> Mux( BranchComp.io.branComOut.br_lt, true.B, false.B),
    BR_LTU-> Mux( BranchComp.io.branComOut.br_ltu,true.B, false.B),
    ))

  io.out.pc := io.in.pc
  io.out.alu := ALU.io.out
  io.out.rs2 := ex_rs2
  io.out.csr_addr := io.in.csr_addr
  io.out.inst := io.in.inst
  when(io.flush_in){
    io.out.ctrlMEM := DontCare
    io.out.ctrlWB := DontCare
  }.otherwise{
    io.out.ctrlMEM <> io.in.ctrlMEM
    io.out.ctrlWB <> io.in.ctrlWB

  }

}
