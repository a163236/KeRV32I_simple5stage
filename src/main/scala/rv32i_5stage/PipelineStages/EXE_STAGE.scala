package rv32i_5stage.PipelineStages

import chisel3._
import rv32i_5stage.PipelineRegisters._

class EXE_STAGE_IO extends Bundle{
  val in = Flipped(new IDEX_REGS_Output)
  val out = new EXMEM_REGS_Output
}

class EXE_STAGE extends Module{
  val io = IO(new EXE_STAGE_IO)

  io := DontCare

}
