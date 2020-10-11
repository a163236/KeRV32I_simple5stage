package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class ID_STAGE_IO extends Bundle {
  val in = Flipped(new IFID_REGS_Output)
  val out = new IDEX_REGS_Output
  val registerFileIO = Flipped(new RegisterFileIO)
}

class ID_STAGE extends Module{
  val io = IO(new ID_STAGE_IO)
  io := DontCare
  io.registerFileIO := DontCare

  val ctrlUnit = Module(new Decoder())
  val rs1_addr = io.in.inst(RS1_MSB, RS1_LSB)
  val rs2_addr = io.in.inst(RS2_MSB, RS2_LSB)

  ctrlUnit.io.inst := io.in.inst
  // 外部のレジスタへアドレス送信
  io.registerFileIO.rs1_addr := rs1_addr
  io.registerFileIO.rs2_addr := rs2_addr


  // 出力
  io.out.pc := io.in.pc
  io.out.rs1 := io.registerFileIO.rs1_data
  io.out.rs2 := io.registerFileIO.rs2_data
  io.out.inst := io.in.inst

  io.out.ctrlEX <> ctrlUnit.io.ctrlEX
  io.out.ctrlMEM <> ctrlUnit.io.ctrlMEM
  io.out.ctrlWB <> ctrlUnit.io.ctrlWB

}
