package rv32i_5stage

import chisel3._
import chisel3.util._

class ForwardingUnitIO extends Bundle{

}

class ForwardingUnit extends Module{
  val io = IO(new ForwardingUnitIO)
}
