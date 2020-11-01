package common
import CommonPackage._

import chisel3._
import chisel3.util._

class ImmGenOut extends Bundle{
  val j_type_imm = Output(UInt(32.W))
  val b_type_imm = Output(UInt(32.W))
  val s_type_imm = Output(UInt(32.W))
  val u_type_imm = Output(UInt(32.W))
  val i_type_imm = Output(UInt(32.W))
}

class ImmGenIO(implicit val conf:Configurations) extends Bundle{
  val inst    = Input(UInt(conf.xlen.W))
  val out = new ImmGenOut()
}

class ImmGen(implicit val conf:Configurations) extends Module{
  val io=IO(new ImmGenIO())

  // immediates and sign-extended
  val imm_i = Cat(Fill(20, io.inst(31)), io.inst(31,20))
  val imm_s = Cat(Fill(20, io.inst(31)), io.inst(31,25), io.inst(11,7))
  val imm_b = Cat(Fill(19,io.inst(31)),io.inst(7),io.inst(31,25),io.inst(11,8),0.U(1.W))
  val imm_u = Cat(io.inst(31, 12), Fill(12, 0.U))
  val imm_j = Cat(Fill(12,io.inst(31)),io.inst(19,12), io.inst(20), io.inst(30,21), 0.U(1.W))

  io.out.i_type_imm := imm_i
  io.out.s_type_imm := imm_s
  io.out.b_type_imm := imm_b
  io.out.u_type_imm := imm_u
  io.out.j_type_imm := imm_j


}
