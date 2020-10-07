package rv32i_5stage.PipelineStages

import chisel3._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class MEM_STAGE_IO extends Bundle{
  val in = Flipped(new EXMEM_REGS_Output)
  val out = new MEMWB_REGS_Output
}

class MEM_STAGE extends Module{
  val io = IO(new MEM_STAGE_IO)

  io := DontCare
}
