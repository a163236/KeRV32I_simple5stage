package rv32i_5stage.PipelineRegisters

import chisel3._
import chiseltest._
import org.scalatest._

class test extends FlatSpec with ChiselScalatestTester with Matchers {

  "IDEX_REGS" should "" in{
    test(new IDEX_REGS){ c=>

      c.io.in.pc.poke(0.U)
      c.clock.step(1)

      c.io.in.pc.poke(1.U)
      c.clock.step(1)

      c.clock.step(1)

    }
  }

}
