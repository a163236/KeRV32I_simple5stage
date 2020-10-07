package ゴミ箱

import chisel3._
import chisel3.util._
import chisel3.util.experimental._
import common.CommonPackage._
import firrtl.annotations.MemoryLoadFileType

class InstMemory extends Module {
  val io = IO(new Bundle() {
    val raddr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))

    val waddr = Input(UInt(32.W))
    val wdata = Input(UInt(32.W))

    val fcn = Input(UInt(M_X.getWidth.W)) // 書き込みか読み込み
    val typ = Input(UInt(MT_X.getWidth.W)) // 8bit?16bit?32bit
  })

  val raddr = io.raddr
  val waddr = io.waddr
  val wdata = io.wdata

  val mem = SyncReadMem(64 * 1024, UInt(32.W))
  loadMemoryFromFile(memory = mem, fileName = "testfolder/hexfile/rv32ui/rv32ui-p-add.hex",MemoryLoadFileType.Hex)

  // ===============================================読み込み
  val readtmp = WireInit(0.U(32.W))
  when(io.fcn === M_XRD) {
    readtmp := mem.read(raddr(31, 2))
  }
  when(io.typ === MT_B) {
    switch(raddr(1,0)){
      is(0.U){io.rdata := readtmp(7,0)}
      is(1.U){io.rdata := readtmp(15,8)}
      is(2.U){io.rdata := readtmp(23,16)}
      is(3.U){io.rdata := readtmp(31,24)}
    }
  }.elsewhen(io.typ === MT_H){
    switch(raddr(1,0)){
      is(0.U){io.rdata := readtmp(15,0)}
      is(1.U){io.rdata := readtmp(23,8)}
      is(2.U){io.rdata := readtmp(31,16)}
      is(3.U){io.rdata := readtmp(31,24)}
    }
  }.elsewhen(io.typ === MT_W){
    switch(raddr(1,0)){
      is(0.U){io.rdata := readtmp(31,0)}
      is(1.U){io.rdata := readtmp(31,8)}
      is(2.U){io.rdata := readtmp(31,16)}
      is(3.U){io.rdata := readtmp(31,24)}
    }
  }

  io.rdata := readtmp
  printf("%x ", io.rdata)

}
