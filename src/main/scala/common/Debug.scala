package common

import chisel3._

class DebugIO(implicit val conf: Configurations) extends Bundle{

  val reg_a0 = Output(UInt(conf.xlen.W))
  /*
  val pc = Output(UInt(conf.xlen.W))
  val pc_decode = Output(UInt(conf.xlen.W))
  val inst = Output(UInt(conf.xlen.W))

  val pc_execute = Output(UInt(conf.xlen.W))
  val rs1_execute = Output(UInt(conf.xlen.W))
  val rs2_execute = Output(UInt(conf.xlen.W))
  val inst_execute = Output(UInt(conf.xlen.W))

  val alu_out=Output(UInt(conf.xlen.W))

   */
}

class LEDDebugIO extends Bundle{
  val out = Output(UInt(32.W))
}