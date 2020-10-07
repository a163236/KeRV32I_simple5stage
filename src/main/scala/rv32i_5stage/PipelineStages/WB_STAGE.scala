package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage.PipelineRegisters._

class WB_STAGE_IO extends Bundle{
  val in = Flipped(new MEMWB_REGS_Output)
  val out = Output(UInt(32.W))
  val registerFileIO = Flipped(new RegisterFileIO)
}

class WB_STAGE extends Module{
  val io = IO(new WB_STAGE_IO)

  io := DontCare

  // 出力
  io.registerFileIO.wen := io.in.ctrlWB.rf_wen
  io.registerFileIO.waddr := io.in.inst(RD_MSB, RD_LSB)
  io.registerFileIO.wdata := MuxLookup(io.in.ctrlWB.wb_sel, io.in.alu, Array(
    WB_PC4 -> io.in.pc_plus4,
    WB_ALU -> io.in.alu,
    WB_MEM -> io.in.rdataD
  ))
}
