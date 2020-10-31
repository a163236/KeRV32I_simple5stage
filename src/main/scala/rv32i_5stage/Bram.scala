package rv32i_5stage

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile
// 使用してない。ロードメモリでメモリを読み込んでもverilogソースには反映されていない。。？
class Bram extends Module {
  val io = IO(new Bundle() {
    val addr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))
  })
  val bram = SyncReadMem(1024, UInt(32.W))
  loadMemoryFromFile(bram, "./testfolder/hexfile/rv32ui/temp_keita.hex")
  io.rdata := bram.read(io.addr)

}
