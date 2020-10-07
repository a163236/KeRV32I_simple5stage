package common

import chisel3._
import chisel3.util._
import CommonPackage._

class ALUIO(implicit val conf: Configurations) extends Bundle{
  val op1 = Input(UInt(conf.xlen.W))
  val op2 = Input(UInt(conf.xlen.W))
  val fun  = Input(UInt(ALU_X.getWidth.W))
  val out  = Output(UInt(conf.xlen.W))
}

class ALU(implicit val conf: Configurations) extends Module{
  val io = IO(new ALUIO())
  io := DontCare
  val alu_shamt = io.op2(4,0).asUInt()   // aluでのシフト命令のシフト量

  io.out := MuxLookup(io.fun, 0.U, Array(
    ALU_ADD -> (io.op1 + io.op2).asUInt(),
    ALU_SUB -> (io.op1 - io.op2).asUInt(),
    ALU_AND -> (io.op1 & io.op2).asUInt(),
    ALU_OR  -> (io.op1 | io.op2).asUInt(),
    ALU_XOR -> (io.op1 ^ io.op2).asUInt(),
    ALU_SLT -> (io.op1.asSInt() < io.op2.asSInt()).asUInt(),  // 符号付き　set less than (1 or 0 をセット)
    ALU_SLTU-> (io.op1 < io.op2).asUInt(),                    // 符号無し　set less than (1 or 0 をセット)
    ALU_SLL -> ((io.op1 << alu_shamt)(conf.xprlen-1,0)).asUInt(),// 左にシフト
    ALU_SRA -> (io.op1.asSInt() >> alu_shamt).asUInt(),        // 右にシフト 符号付で
    ALU_SRL -> (io.op1 >> alu_shamt).asUInt(),                 // 右にシフト 0埋め
    ALU_COPYrs1-> io.op1,
    ALU_COPYrs2-> io.op2
  ))


}
