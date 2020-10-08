package rv32i_5stage

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._

class FWWB_IO extends Bundle{
  val rd_addr = Input(UInt(32.W))
  val bypass_data = Input(UInt(32.W))
}

class FWMEM_IO extends Bundle{
  val rd_addr = Input(UInt(32.W))
  val bypass_data = Input(UInt(32.W))
}

class FWEXE_IO extends Bundle{
  val rs1_addr = Input(UInt(32.W))
  val rs2_addr = Input(UInt(32.W))

  val mem_bypassdata = Output(UInt(32.W))
  val wb_bypassdata = Output(UInt(32.W))
  val rs1_sel = Output(UInt(FW1_X.getWidth.W))
  val rs2_sel = Output(UInt(FW2_X.getWidth.W))
}


class ForwardingUnitIO extends Bundle{
  val fwEXE = new FWEXE_IO
  val fwfromMEM = new FWMEM_IO
  val fwfromWB = new FWWB_IO
}

// フォワーディング判定
class ForwardingUnit extends Module{
  val io = IO(new ForwardingUnitIO)

  // fw1_selの選択
  when((io.fwfromMEM.rd_addr === io.fwEXE.rs1_addr) && io.fwfromMEM.rd_addr=/=0.U){  // MEMからが優先
    io.fwEXE.rs1_sel := FW1_MEM
  }.elsewhen((io.fwfromWB.rd_addr === io.fwEXE.rs1_addr) && io.fwfromWB.rd_addr=/=0.U){
    io.fwEXE.rs1_sel := FW1_WB
  }.otherwise{
    io.fwEXE.rs1_sel := FW1_X
  }


  // fw2_selの選択
  when((io.fwfromMEM.rd_addr === io.fwEXE.rs2_addr) && io.fwfromMEM.rd_addr=/=0.U){  // MEMからが優先
    io.fwEXE.rs2_sel := FW2_MEM
  }.elsewhen((io.fwfromWB.rd_addr === io.fwEXE.rs2_addr) && io.fwfromWB.rd_addr=/=0.U){
    io.fwEXE.rs2_sel := FW2_WB
  }.otherwise{
    io.fwEXE.rs2_sel := FW2_X
  }

  // バイパスデータはそのまま出力
  io.fwEXE.mem_bypassdata := io.fwfromMEM.bypass_data
  io.fwEXE.wb_bypassdata := io.fwfromWB.bypass_data

}
