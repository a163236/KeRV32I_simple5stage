package rv32i_5stage.PipelineStages

import chisel3._
import common.RegisterFile
import rv32i_5stage.PipelineRegisters._

class WB_STAGE_IO extends Bundle{
  val in = Flipped(new MEMWB_REGS_Output)
  val out = Output(UInt(32.W))
}

class WB_STAGE extends Module{
  val io = IO(new WB_STAGE_IO)

  io := DontCare

}
