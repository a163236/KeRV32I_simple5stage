package rv32i_5stage

import chisel3._
import chisel3.util._
import common.CommonPackage._

class Hazard_IFID_IO extends Bundle{
  val rs1_addr = Input(UInt(32.W))
  val rs2_addr = Input(UInt(32.W))
}

class Hazard_IDEX_IO extends Bundle{
  val rd_addr = Input(UInt(32.W))
  val mem_en = Input(UInt(MEN_X.getWidth.W))
  val mem_wr = Input(UInt(M_XWR.getWidth.W))
}

class HazardUnitIO extends Bundle{
  val ifid = new Hazard_IFID_IO
  val idex = new Hazard_IDEX_IO
  val stall =Output(Bool())
}

class HazardUnit extends Module{
  val io = IO(new HazardUnitIO)

  // ロードのあとのストール
  when(io.idex.mem_wr===M_XRD && io.idex.mem_en===MEN_1 &&
  (io.idex.rd_addr === io.ifid.rs1_addr || io.idex.rd_addr === io.ifid.rs2_addr)) { // 読み込み＆アドレスが等しいとき
    io.stall := true.B
  }.otherwise{
    io.stall := PIPE_X
  }
}
