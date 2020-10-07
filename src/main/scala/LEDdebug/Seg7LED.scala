package LEDdebug
import chisel3._
import chisel3.util._

/** 7セグメントLED用のバンドル
 */
class Seg7LEDBundle extends Bundle {
  /** 各セグメントの点灯用。0〜7をCAからCGに対応させる事。0の時に点灯、1の時に消灯します。 */
  val cathodes = Output(UInt(7.W))
  /** 桁の選択用。0の桁が点灯、１の桁が消灯。 */
  val anodes = Output(UInt(8.W))
}

/** 7セグメントLED点灯モジュール(8桁版)
 */
class Seg7LED extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val seg7led = new Seg7LEDBundle
  })
  val digits = Wire(Vec(8, UInt(4.W))) // 4桁分の4ビットの数値をVecで確保する

  digits(7) := io.in(31,28)
  digits(6) := io.in(27,24)
  digits(5) := io.in(23,20)
  digits(4) := io.in(19,16)
  digits(3) := io.in(15,12)
  digits(2) := io.in(11,8)
  digits(1) := io.in(7,4)
  digits(0) := io.in(3,0)

  /* 各桁を切り替える時間のカウンタ
   * Counterは、引数にカウントアップする条件(cond)、カウントする数(n, 0〜n-1までカウントする)をとり、
   * 現在のカウント数の値の信号、n-1にカウントアップした時ににtrue.Bになる信号のタプルを返します。
   * カウントアップ条件にtrue.Bを渡すと、毎クロックカウントアップします。 */
  val (digitChangeCount, digitChange) = Counter(true.B, 100000)

  val (digitIndex, digitWrap) = Counter(digitChange, 8) // 何桁目を表示するか
  val digitNum = digits(digitIndex)        // 表示桁の数値

  io.seg7led.cathodes := ~MuxCase("b000_0000".U,
    Array(                   //  abc_defgの順序にcathodeが並ぶ
      (digitNum === "h0".U) -> "b111_1110".U,
      (digitNum === "h1".U) -> "b011_0000".U,
      (digitNum === "h2".U) -> "b110_1101".U,
      (digitNum === "h3".U) -> "b111_1001".U,
      (digitNum === "h4".U) -> "b011_0011".U,
      (digitNum === "h5".U) -> "b101_1011".U,
      (digitNum === "h6".U) -> "b101_1111".U,
      (digitNum === "h7".U) -> "b111_0000".U,
      (digitNum === "h8".U) -> "b111_1111".U,
      (digitNum === "h9".U) -> "b111_1011".U,
      (digitNum === "ha".U) -> "b111_0111".U,
      (digitNum === "hb".U) -> "b001_1111".U,
      (digitNum === "hc".U) -> "b100_1110".U,
      (digitNum === "hd".U) -> "b011_1101".U,
      (digitNum === "he".U) -> "b100_1111".U,
      (digitNum === "hf".U) -> "b100_0111".U))

  io.seg7led.anodes := ~MuxLookup(digitIndex, "b0000_0000".U,
    Array(
      0.U -> "b0000_0001".U,
      1.U -> "b0000_0010".U,
      2.U -> "b0000_0100".U,
      3.U -> "b0000_1000".U,
      4.U -> "b0001_0000".U,
      5.U -> "b0010_0000".U,
      6.U -> "b0100_0000".U,
      7.U -> "b1000_0000".U,
    ))

}