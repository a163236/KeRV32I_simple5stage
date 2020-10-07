package rv32i_5stage


import chisel3._
import common._


class CoreIO(implicit val conf: Configurations) extends Bundle {
  val imem = new InstMemPortIO()  // 外部メモリとの接続
  val dmem = new DataMemPortIO()
  val debug = new DebugIO()
  val led = new LEDDebugIO()
}

class Core(implicit val conf:Configurations) extends Module{
  val io = IO(new CoreIO())
  io := DontCare

  val dpath = Module(new Dpath())
  dpath.io := DontCare

  // データパスとメモリ
  io.imem <> dpath.io.imem
  io.dmem <> dpath.io.dmem

  // デバッグ
  io.debug <> dpath.io.debug
  io.led.out := dpath.io.led.out
}