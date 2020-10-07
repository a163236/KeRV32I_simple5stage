package common
import CommonPackage._

import chisel3._
import chisel3.util._

class ImmGenIO(implicit val conf:Configurations) extends Bundle{
  val inst    = Input(UInt(conf.xlen.W))
  val imm_sel  = Input(UInt(IMM_X.getWidth.W))
  val out = Output(UInt(conf.xlen.W))
}

class ImmGen(implicit val conf:Configurations) extends Module{
  val io=IO(new ImmGenIO())

  // immediates and sign-extended
  val imm_i = Cat(Fill(20, io.inst(31)), io.inst(31,20))
  val imm_s = Cat(Fill(20, io.inst(31)), io.inst(31,25), io.inst(11,7))
  val imm_b = Cat(Fill(19,io.inst(31)),io.inst(7),io.inst(31,25),io.inst(11,8),0.U(1.W))
  val imm_u = Cat(io.inst(31, 12), Fill(12, 0.U))
  val imm_j = Cat(Fill(12,io.inst(31)),io.inst(19,12), io.inst(20), io.inst(30,21), 0.U(1.W))

  io.out := MuxLookup(io.imm_sel, 0.U(32.W), Array(
    IMM_X -> 0.U(conf.xlen.W),
    IMM_I -> imm_i,
    IMM_S -> imm_s,
    IMM_B -> imm_b,
    IMM_U -> imm_u,
    IMM_J -> imm_j,
  ))

}
