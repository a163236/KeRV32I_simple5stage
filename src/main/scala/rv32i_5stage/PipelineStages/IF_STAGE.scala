package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common.CommonPackage._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class IF_STAGE_Input extends Bundle{
}

class IF_STAGE_IO extends Bundle{
  val in = new IF_STAGE_Input
  val out = new IFID_REGS_Output
  val imem = new InstMemPortIO
  val exetoifjumpsignals = Flipped(new EXEtoIFjumpsignalsIO)
  val memtoifjumpsignals = Flipped(new MEMtoIFjumpsignalsIO)
  val stallorFlush = Input(UInt(PIPE_X.getWidth.W))
}

class IF_STAGE extends Module{
  val io = IO(new IF_STAGE_IO)

  io := DontCare
  val pc_reg  = RegInit(START_ADDR.U(conf.xlen.W))
  val next_pc = WireInit(0.U(32.W))

  // 動作
  io.imem.req.renI := true.B


  when(io.memtoifjumpsignals.eret){ // 例外
    next_pc := io.memtoifjumpsignals.outPC
  }.elsewhen(io.exetoifjumpsignals.branchbool){ // 分岐命令のとき
    next_pc := io.exetoifjumpsignals.aluout
  }.elsewhen(io.stallorFlush===PIPE_STALL){ // stall でそのまま
    next_pc := pc_reg
  }.otherwise{
    next_pc := pc_reg + 4.U
  }

  when(reset.asBool()===true.B){  // 最初の初期化
    io.imem.req.raddrI := START_ADDR.U
  }.otherwise{
    io.imem.req.raddrI := next_pc
  }
  pc_reg := next_pc

  // 出力
  io.out.pc := pc_reg
  io.out.inst := io.imem.resp.rdata

}
