package rv32i_5stage

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._

/*
  命令メモリとデータメモリの共用のBRAM
  1サイクル遅れででてくる?
 */

class InstMemPortIO extends Bundle{
  val req = new InstMemReqIO()         // パス->メモリ output
  val resp = Flipped(new MemRespIO())  // メモリ->パス input
}

class DataMemPortIO extends Bundle{
  val req = new DataMemReqIO()         // パス->メモリ output
  val resp = Flipped(new MemRespIO())  // メモリ->パス input
}

class InstMemReqIO extends Bundle{
  val renI = Output(Bool())   // 読み込み有効
  val raddrI = Output(UInt(conf.xlen.W))   // アドレス
}

class DataMemReqIO extends Bundle{
  val addrD = Output(UInt(32.W))   // アドレス
  val wdataD = Output(UInt(32.W))   // 書き込みデータ

  val mask  = Output(UInt(M_X.getWidth.W)) //  half?byte?
  val wr = Output(UInt(MT_X.getWidth.W)) //  書き込みか読み込みか
}

class MemRespIO extends Bundle{
  val rdata = Output(UInt(32.W))   // 読み込みデータ
}

class SyncMemBlackBoxIO extends Bundle{
  val clk = Input(Clock())

  // 命令メモリ
  val raddrI = Input(UInt(32.W))
  val renI = Input(Bool())
  val rdataI = Output(UInt(32.W))

  val addrD = Input(UInt(32.W))
  // データメモリ 読み込み
  val renD = Input(Bool())
  val rdataD = Output(UInt(32.W))
  // データメモリ 書き込み
  val wenD = Input(Bool())
  val wdataD = Input(UInt(32.W))
  val MaskD = Input(UInt(4.W))
}

class SyncMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new SyncMemBlackBoxIO)
  addResource("/SyncMem.v")

}

class SyncMemScala extends Module {
  val io = IO(new Bundle() {
    val instmport = Flipped(new InstMemPortIO())
    val datamport = Flipped(new DataMemPortIO())
  })
  val mask = WireInit(0.U(4.W))

  val syncmemblackbox = Module(new SyncMem)
  syncmemblackbox.io.clk := clock
  // 命令メモリ接続
  syncmemblackbox.io.raddrI := io.instmport.req.raddrI
  syncmemblackbox.io.renI := io.instmport.req.renI
  // データメモリ接続
  syncmemblackbox.io.addrD := io.datamport.req.addrD
  syncmemblackbox.io.renD := Mux(io.datamport.req.mask===M_XRD, true.B, false.B)
  syncmemblackbox.io.wenD := Mux(io.datamport.req.mask===M_XWR, true.B, false.B)
  syncmemblackbox.io.wdataD := io.datamport.req.wdataD
  syncmemblackbox.io.MaskD := mask
  io.datamport.resp.rdata := syncmemblackbox.io.rdataD

  // 書き込みのとき用
  when(io.datamport.req.wr===MT_B){ // バイトのとき
    mask := 1.U << io.datamport.req.addrD(1,0)
    printf("%x ",mask)
  }.elsewhen(io.datamport.req.wr===MT_H) { // ハーフワードのとき
    mask := 3.U << io.datamport.req.addrD(1,0)
  }.elsewhen(io.datamport.req.wr===MT_W){
    mask := 7.U << io.datamport.req.addrD(1,0)
  }

  // 出力 メモリ読み出し
  val rdataD = syncmemblackbox.io.rdataD
  val tmpans = WireInit(0.U(32.W))
  io.instmport.resp.rdata := syncmemblackbox.io.rdataI
  io.datamport.resp.rdata := tmpans

  switch(io.datamport.req.wr){
    is(MT_B){
      tmpans := MuxLookup(io.datamport.req.addrD(1,0),rdataD(7,0),Array(
        0.U -> rdataD(7,0),
        1.U -> rdataD(15,8),
        2.U -> rdataD(23,16),
        3.U -> rdataD(31,24)
      ))
    }
    is(MT_BU){
      tmpans := MuxLookup(io.datamport.req.addrD(1,0),rdataD(7,0),Array(
        0.U -> Cat(Fill(24, rdataD(7)), rdataD(7,0)),
        1.U -> Cat(Fill(24, rdataD(15)), rdataD(15,8)),
        2.U -> Cat(Fill(24, rdataD(23)), rdataD(23,16)),
        3.U -> Cat(Fill(24, rdataD(31)), rdataD(31,24)),
      ))
    }
    is(MT_H){
      tmpans := MuxLookup(io.datamport.req.addrD(1,0), rdataD(15,0),Array(
        0.U -> rdataD(15,0),
        1.U -> rdataD(23,8),
        2.U -> rdataD(31,16),
        3.U -> rdataD(31,24)
      ))
    }
    is(MT_HU){
      tmpans := MuxLookup(io.datamport.req.addrD(1,0),rdataD(7,0),Array(
        0.U -> Cat(Fill(24, rdataD(15)), rdataD(15,0)),
        1.U -> Cat(Fill(24, rdataD(23)), rdataD(23,8)),
        2.U -> Cat(Fill(24, rdataD(31)), rdataD(31,16)),
        3.U -> Cat(Fill(24, rdataD(31)), rdataD(31,24)),
      ))
    }
    is(MT_W) {
      tmpans := MuxLookup(io.datamport.req.addrD(1, 0), rdataD(15, 0), Array(
        0.U -> rdataD(31, 0),
        1.U -> rdataD(31, 8),
        2.U -> rdataD(31, 16),
        3.U -> rdataD(31, 24)
      ))
    }
    is(MT_WU){
      tmpans := MuxLookup(io.datamport.req.addrD(1,0), rdataD(15,0),Array(
        0.U -> rdataD(31,0),
        1.U -> rdataD(31,8),
        2.U -> rdataD(31,16),
        3.U -> rdataD(31,24)
      ))
    }
  }

}