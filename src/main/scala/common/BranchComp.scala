package common

import chisel3._

class BranComOut(implicit val conf:Configurations) extends Bundle{
  val br_eq   = Output(Bool())
  val br_lt   = Output(Bool())
  val br_ltu  = Output(Bool())
}

class BranchCompIO(implicit val conf: Configurations) extends Bundle{
  val rs1_data    = Input(UInt(conf.xlen.W))
  val rs2_data    = Input(UInt(conf.xlen.W))
  val branComOut  = new BranComOut
}

class BranchComp(implicit val conf: Configurations) extends Module{
  val io = IO(new BranchCompIO())

  io.branComOut.br_eq  := (io.rs1_data === io.rs2_data)
  io.branComOut.br_lt  := (io.rs1_data.asSInt() < io.rs2_data.asSInt())
  io.branComOut.br_ltu := (io.rs1_data.asUInt() < io.rs2_data.asUInt())
}
