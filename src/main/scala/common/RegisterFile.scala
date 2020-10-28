package common

import chisel3._

class RegisterFileIO extends Bundle{
  val rs1_addr, rs2_addr = Input(UInt(5.W))
  val rs1_data, rs2_data = Output(UInt(32.W))

  val waddr = Input(UInt(5.W))          // 書き込みアドレス
  val wdata = Input(UInt(32.W))  // 書き込みデータ
  val wen   = Input(Bool())             // 書き込み有効

  val reg_a0 = Output(UInt(32.W)) // debug用 gpレジスタの値
}

class RegisterFile extends Module{
  val io = IO(new RegisterFileIO)

  val regfile = Mem(32, UInt(32.W))

  // 書き込み
  when(io.wen && (io.waddr=/=0.U)){ // 書き込み有効かつ0レジスタ以外なら
    regfile(io.waddr) := io.wdata
  }

  // rs1の読み込み
  when(io.rs1_addr===0.U){  // アドレス0の読み出し
    io.rs1_data := 0.U
  }.elsewhen(io.rs1_addr===io.waddr){ // 書き込みアドレスと同じ時
    io.rs1_data := io.wdata
  }.otherwise{
    io.rs1_data := regfile(io.rs1_addr)
  }

  // rs2の読み込み
  when(io.rs2_addr===0.U){  // アドレス0の読み出し
    io.rs2_data := 0.U
  }.elsewhen(io.rs2_addr===io.waddr){ // 書き込みアドレスと同じ時
    io.rs2_data := io.wdata
  }.otherwise{
    io.rs2_data := regfile(io.rs2_addr)
  }

  // デバッグ
  io.reg_a0 := regfile(10)
  printf("rega5=%x ",regfile(15))
}
