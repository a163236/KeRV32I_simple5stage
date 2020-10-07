package ゴミ箱

import chisel3._
import chisel3.util.experimental._
import rv32i_5stage.{DataMemPortIO, InstMemPortIO}

class SyncReadMEM extends Module{
  val io = IO(new Bundle() {
    val instmport = Flipped(new InstMemPortIO())
    val datamport = Flipped(new DataMemPortIO())
  })
  io:=DontCare
  val mem = SyncReadMem(1024*64, UInt(32.W))
  loadMemoryFromFile(mem, "./testfolder/hexfile/rv32ui/temp_keita.hex")

  when(io.instmport.req.renI){
    io.instmport.resp.rdata := mem(io.instmport.req.raddrI(31,2))
  }
  printf("%x ", io.instmport.resp.rdata)

}
