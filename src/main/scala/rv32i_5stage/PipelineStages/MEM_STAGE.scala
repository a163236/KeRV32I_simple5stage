package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class MEM_STAGE_IO extends Bundle{
  val in = Flipped(new EXMEM_REGS_Output)
  val out = new MEMWB_REGS_Output
  val dmem = new DataMemPortIO
}

class MEM_STAGE extends Module{
  val io = IO(new MEM_STAGE_IO)

  io := DontCare

  io.dmem.req.mask := io.in.ctrlMEM.dmem_mask
  io.dmem.req.wr := io.in.ctrlMEM.dmem_wr
  io.dmem.req.addrD := io.in.alu
  io.dmem.req.wdataD := io.in.rs2

  // 出力
  io.out.pc_plus4 := io.in.pc + 4.U
  io.out.alu := io.in.alu
  io.out.rdataD := io.dmem.resp.rdata
  io.out.inst := io.in.inst
  io.out.ctrlWB := io.in.ctrlWB

}
