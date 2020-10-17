package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class MEMtoIFjumpsignalsIO extends Bundle{  //csr
  val eret = Output(Bool())  // 例外です！
  val outPC = Output(UInt(32.W))  // 例外pc
}

class BypassFromMem extends Bundle{
  val rdata = Output(UInt(32.W))
  val raddr = Output(UInt(32.W))
}

class MEM_STAGE_IO extends Bundle{
  val in = Flipped(new EXMEM_REGS_Output)
  val out = new MEMWB_REGS_Output
  val dmem = new DataMemPortIO
  val memtoifjumpsignals = new MEMtoIFjumpsignalsIO
  val fwfromMEM = Flipped(new FWMEM_IO)
  val hazard = Flipped(new Hazard_MEM_StageIO)
}

class MEM_STAGE extends Module{
  val io = IO(new MEM_STAGE_IO)

  io := DontCare

  val csrFile = Module(new CSRFile())

  csrFile.io.inPC := io.in.pc
  csrFile.io.inst := io.in.inst
  csrFile.io.csr_cmd := io.in.ctrlMEM.csr_cmd
  csrFile.io.rs1 := io.in.alu // rs1はALUからもらっている

  // dmem接続
  io.dmem.req.mask := io.in.ctrlMEM.dmem_mask
  io.dmem.req.wr := io.in.ctrlMEM.dmem_wr
  io.dmem.req.addrD := io.in.alu
  io.dmem.req.wdataD := io.in.rs2
  io.dmem.req.enD := io.in.ctrlMEM.dmem_en

  // 出力
  io.out.pc_plus4 := io.in.pc + 4.U
  io.out.alu := io.in.alu
  io.out.rdataD := io.dmem.resp.rdata
  io.out.wdataCSR := csrFile.io.wdata
  io.out.inst := io.in.inst
  io.out.ctrlWB := io.in.ctrlWB

  // フォワーディング
  io.fwfromMEM.bypass_data := io.in.alu
  io.fwfromMEM.rd_addr := io.in.inst(RD_MSB, RD_LSB)
  // ハザード
  io.hazard.mem_addr := io.in.inst(RD_MSB, RD_LSB)
  io.hazard.mem_wr := io.in.ctrlMEM.dmem_wr
  io.hazard.mem_en := io.in.ctrlMEM.dmem_en
  io.hazard.mem_mask := io.in.ctrlMEM.dmem_mask

  // 例外のIFへの出力
  io.memtoifjumpsignals.outPC := csrFile.io.outPC
  io.memtoifjumpsignals.eret := csrFile.io.eret

}
