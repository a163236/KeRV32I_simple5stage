package LEDdebug

import chisel3._
import common._
import rv32i_5stage.Tile

class TileAndLED(filename:String)(implicit val conf:Configurations) extends Module{
  val io = IO(new Bundle() {
    val LEDout = new Seg7LEDBundle
  })

  val Tile = Module(new Tile(filename))
  val Seg7LED = Module(new Seg7LED())

  // メモリ初期化を無効
  Tile.io := DontCare // メモリ初期化を無効

  Seg7LED.io.in := Tile.io.led.out
  io.LEDout := Seg7LED.io.seg7led
}
