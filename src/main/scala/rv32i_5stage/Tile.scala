package rv32i_5stage

import chisel3._
import common._

class Tile(implicit val conf: Configurations) extends Module{
  val io = IO(new Bundle() {
    //val debug = new DebugIO()
    val led = new LEDDebugIO()
  })
  io := DontCare

  val bram = Module(new SyncMemScala) // 命令データ共有BRAM
  bram.io := DontCare
  val core = Module(new Core())

  // メモリとコア
  bram.io.instmport <> core.io.imem
  bram.io.datamport <> core.io.dmem

  // デバッグ
  //io.debug <> core.io.debug
  io.led.out := core.io.led.out
}
