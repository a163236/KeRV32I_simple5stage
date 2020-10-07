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

  when(io.wen && (io.waddr=/=0.U)){ // 書き込み有効かつ0レジスタ以外なら
    regfile(io.waddr) := io.wdata
  }

  io.rs1_data := Mux((io.rs1_addr =/= 0.U), regfile(io.rs1_addr), 0.U)
  io.rs2_data := Mux((io.rs2_addr =/= 0.U), regfile(io.rs2_addr), 0.U)

  io.reg_a0 := regfile(10)

}
