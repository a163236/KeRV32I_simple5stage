package rv32i_5stage

import chisel3._
import chisel3.util._
import common.CommonPackage._

class Hazard_EXE_StageIO extends Bundle{
  val rs1_addr = Input(UInt(32.W))
  val rs2_addr = Input(UInt(32.W))
}

class Hazard_MEM_StageIO extends Bundle{
  val mem_wr = Input(UInt(M_XRD.getWidth.W))
  val mem_en = Input(Bool())
  val mem_mask = Input(UInt(MT_X.getWidth.W))
  val mem_addr = Input(UInt(32.W))
}

class StallorFlushIO extends Bundle{
  val if_stage = Output(UInt(PIPE_X.getWidth.W))
  val ifid = Output(UInt(PIPE_X.getWidth.W))
  val idexe = Output(UInt(PIPE_X.getWidth.W))
  val exemem = Output(UInt(PIPE_X.getWidth.W))

}

class HazardUnitIO extends Bundle{
  val mem = new Hazard_MEM_StageIO
  val exe = new Hazard_EXE_StageIO
  val stallorflush =new StallorFlushIO
}

class HazardUnit extends Module{
  val io = IO(new HazardUnitIO)

  // ロードのあとのストール
  when(io.mem.mem_wr===M_XRD && io.mem.mem_en===MEN_1 &&
  (io.mem.mem_addr === io.exe.rs1_addr || io.mem.mem_addr === io.exe.rs2_addr)) { // 読み込み＆アドレスが等しいとき
    // exememをフラッシュ 他はストール
    io.stallorflush.if_stage := PIPE_STALL
    io.stallorflush.ifid := PIPE_STALL
    io.stallorflush.idexe := PIPE_STALL
    io.stallorflush.exemem := PIPE_FLUSH  // ?
    //printf("Hazard-stall! ")
  }.otherwise{
    io.stallorflush.if_stage := PIPE_X
    io.stallorflush.ifid := PIPE_X
    io.stallorflush.idexe := PIPE_X
    io.stallorflush.exemem := PIPE_X
  }
}
