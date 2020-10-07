package rv32i_5stage.PipelineStages

import chisel3._
import common.CommonPackage._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class IF_STAGE_Input extends Bundle{
}

class IF_STAGE_IO extends Bundle{
  val in = new IF_STAGE_Input
  val out = new IFID_REGS_Output
  val imem = new InstMemPortIO
}

class IF_STAGE extends Module{
  val io = IO(new IF_STAGE_IO)

  io := DontCare
  val pc_reg  = RegInit(START_ADDR.U(conf.xlen.W))
  val next_pc = WireInit(0.U(32.W))

  // 動作
  io.imem.req.renI := true.B
  next_pc := pc_reg + 4.U
  when(reset.asBool()===true.B){
    io.imem.req.raddrI := 0.U
  }.otherwise{
    io.imem.req.raddrI := next_pc
  }
  pc_reg := next_pc

  // 出力
  io.out.pc := pc_reg
  io.out.inst := io.imem.resp.rdata

  /*
  printf("%x ", io.out.pc)
  printf("%x ", io.imem.resp.rdata)
  printf("\n")

   */
}
