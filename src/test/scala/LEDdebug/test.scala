package LEDdebug

import chisel3._
import chisel3.util._
import chiseltest._
import common._
import org.scalatest._


class test() extends FlatSpec with ChiselScalatestTester with Matchers {
  implicit val conf = Configurations()


  behavior of "TileAndLED"
  it should "TileAndLED" in {
    test(new TileAndLED()){ c =>
      for (i <- 1 to 50) {
        //println(c.io.LEDout.anodes.peek())
        //println(c.io.LEDout.cathodes.peek())
        c.clock.step(1)
        println()
      }
    }
  }



  behavior of "Seg7LED"
  it should "Seg7LED" in {
    test(new Seg7LED()){ c =>

      for (i <- 1 to 1000000) {

        c.clock.step(1)
        println(c.io.seg7led.cathodes)
        println(c.io.seg7led.anodes)

      }

    }
  }

}
