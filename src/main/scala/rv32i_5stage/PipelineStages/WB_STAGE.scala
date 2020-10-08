package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage.FWWB_IO
import rv32i_5stage.PipelineRegisters._


class WB_STAGE_IO extends Bundle{
  val in = Flipped(new MEMWB_REGS_Output)
  val out = Output(UInt(32.W))
  val registerFileIO = Flipped(new RegisterFileIO)
  val fwfromWB = Flipped(new FWWB_IO)
}

class WB_STAGE extends Module{
  val io = IO(new WB_STAGE_IO)

  io := DontCare
  val rf_wdata = Wire(UInt(32.W))
  io.registerFileIO.wdata := rf_wdata
  // 出力
  io.registerFileIO.wen := io.in.ctrlWB.rf_wen
  io.registerFileIO.waddr := io.in.inst(RD_MSB, RD_LSB)
  rf_wdata := MuxLookup(io.in.ctrlWB.wb_sel, io.in.alu, Array(
    WB_CSR -> io.in.wdataCSR,
    WB_PC4 -> io.in.pc_plus4,
    WB_ALU -> io.in.alu,
    WB_MEM -> io.in.rdataD
  ))

  // フォワーディング
  io.fwfromWB.rd_addr := io.in.inst(RD_MSB, RD_LSB)
  io.fwfromWB.bypass_data := rf_wdata


}
