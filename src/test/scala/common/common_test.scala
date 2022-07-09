package common

import chisel3._
import chiseltest._
import common._
import CommonPackage._
import org.scalatest._
import firrtl.stage.RunFirrtlTransformAnnotation

class common_test extends FlatSpec with ChiselScalatestTester with Matchers {
  implicit val conf = Configurations()

  "CSR" should "" in {
    test(new CSRFile()){c=>

      c.clock.step(1)
    }
  }

}
