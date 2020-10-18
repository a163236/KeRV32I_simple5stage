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

  val enD = Output(UInt(MEN_X.getWidth.W))
  val mask = Output(UInt(MT_X.getWidth.W))
  val wr  = Output(UInt(M_X.getWidth.W))
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
  val wdata = WireInit(0.U(32.W)) // 書き込むデータ

  val syncmemblackbox = Module(new SyncMem)
  syncmemblackbox.io.clk := clock
  // 命令メモリ接続
  syncmemblackbox.io.raddrI := io.instmport.req.raddrI
  syncmemblackbox.io.renI := io.instmport.req.renI
  // データメモリ接続
  syncmemblackbox.io.addrD := io.datamport.req.addrD
  syncmemblackbox.io.renD := Mux(io.datamport.req.wr===M_XRD && io.datamport.req.enD===MEN_1, true.B, false.B)
  syncmemblackbox.io.wenD := Mux(io.datamport.req.wr===M_XWR && io.datamport.req.enD===MEN_1, true.B, false.B)
  syncmemblackbox.io.wdataD := wdata //io.datamport.req.wdataD
  syncmemblackbox.io.MaskD := mask
  //io.datamport.resp.rdata := syncmemblackbox.io.rdataD


  // 書き込みのとき用
  when(io.datamport.req.mask===MT_B){ // バイトのとき
    mask := "b1".U << io.datamport.req.addrD(1,0)
  }.elsewhen(io.datamport.req.mask===MT_H) { // ハーフワードのとき
    mask := "b11".U << io.datamport.req.addrD(1,0)
  }.elsewhen(io.datamport.req.mask===MT_W){
    mask := "b1111".U << io.datamport.req.addrD(1,0)
  }

  //書き込みデータも1ずらしておく
  wdata := MuxLookup(io.datamport.req.addrD(1,0), io.datamport.req.wdataD, Array(
    0.U -> io.datamport.req.wdataD,
    1.U -> (io.datamport.req.wdataD << 8.U),
    2.U -> (io.datamport.req.wdataD << 16.U),
    3.U -> (io.datamport.req.wdataD << 24.U),
  ))

  // 出力 メモリ読み出し
  val rdataD = syncmemblackbox.io.rdataD
  val tmpans = WireInit(0.U(32.W))
  // 状態遷移　１クロック遅いので
  val reg_addrD = Reg(UInt(2.W)); reg_addrD:=io.datamport.req.addrD(1,0)
  val reg_mask = Reg(UInt(4.W));  reg_mask:=io.datamport.req.mask

  io.instmport.resp.rdata := syncmemblackbox.io.rdataI
  io.datamport.resp.rdata := tmpans
  /*
  printf("mask=%b ", mask)
  printf("regaddr=%x ", reg_addrD)
  printf("%x ", reg_mask)
  printf("wdata=%x ", wdata)
  printf("rdataD=%x ", rdataD)
  printf("tempans=%x ", tmpans)
   */

  switch(reg_mask){
    is(MT_B){
      tmpans := MuxLookup(reg_addrD,rdataD(7,0),Array(
        0.U -> Cat(Fill(24, rdataD(7)), rdataD(7,0)),
        1.U -> Cat(Fill(24, rdataD(15)), rdataD(15,8)),
        2.U -> Cat(Fill(24, rdataD(23)), rdataD(23,16)),
        3.U -> Cat(Fill(24, rdataD(31)), rdataD(31,24)),
      ))
    }
    is(MT_BU){
      tmpans := MuxLookup(reg_addrD,rdataD(7,0),Array(
        0.U -> rdataD(7,0),
        1.U -> rdataD(15,8),
        2.U -> rdataD(23,16),
        3.U -> rdataD(31,24)
      ))
    }
    is(MT_H){
      tmpans := MuxLookup(reg_addrD, rdataD(15,0),Array(
        0.U -> Cat(Fill(16, rdataD(15)), rdataD(15,0)),
        1.U -> Cat(Fill(16, rdataD(23)), rdataD(23,8)),
        2.U -> Cat(Fill(16, rdataD(31)), rdataD(31,16)),
        3.U -> Cat(Fill(24, rdataD(31)), rdataD(31,24)),
      ))
    }
    is(MT_HU){
      tmpans := MuxLookup(reg_addrD,rdataD(7,0),Array(
        0.U -> rdataD(15,0),
        1.U -> rdataD(23,8),
        2.U -> rdataD(31,16),
        3.U -> rdataD(31,24)
      ))
    }
    is(MT_W) {
      tmpans := MuxLookup(reg_addrD, rdataD(31, 0), Array(
        0.U -> rdataD(31,0),
        1.U -> rdataD(31,8),
        2.U -> rdataD(31,16),
        3.U -> rdataD(31,24)
      ))
    }
    is(MT_WU){
      tmpans := MuxLookup(reg_addrD, rdataD(31,0),Array(
        0.U -> rdataD(31, 0),
        1.U -> rdataD(31, 8),
        2.U -> rdataD(31, 16),
        3.U -> rdataD(31, 24)
      ))
    }
  }

}