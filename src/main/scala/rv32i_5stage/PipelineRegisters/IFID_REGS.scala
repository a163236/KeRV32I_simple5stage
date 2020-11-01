package rv32i_5stage.PipelineRegisters

import chisel3._
import chisel3.util._
import rv32i_5stage._
import common._
import common.CommonPackage._

class IFID_REGS_Output extends Bundle{
  val pc = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class IFID_REGS_IO extends Bundle{
  val in = Flipped(new IFID_REGS_Output)
  val out = new IFID_REGS_Output
  val pipe_flush = Input(Bool())
  val pipe_stalll = Input(Bool())
}

class IFID_REGS extends Module{
  val io = IO(new IFID_REGS_IO)

  val pc = Reg(UInt(32.W))
  val inst = Reg(UInt(32.W))

  // 入力
  when(io.pipe_flush){
    pc := BUBBLE
    inst := BUBBLE
  }.elsewhen(io.pipe_stalll){
    pc := pc
    inst := inst
  }.otherwise{
    pc := io.in.pc
    inst := io.in.inst
  }

  //出力
  io.out.pc := pc
  io.out.inst := inst

}
