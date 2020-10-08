package rv32i_5stage

import chisel3._
import chisel3.util._

class Hazard_EXE_StageIO extends Bundle
}

class Hazard_MEM_StageIO extends Bundle{
}

class Hazard_WB_StageIO extends Bundle{
}

class HazardUnitIO extends Bundle{

}

class HazardUnit extends Module{
  val io = IO(new HazardUnitIO)
}
