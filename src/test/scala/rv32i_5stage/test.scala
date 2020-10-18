package rv32i_5stage

import chisel3._
import chiseltest._
import common._
import CommonPackage._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.VerilatorBackendAnnotation
import firrtl.stage.RunFirrtlTransformAnnotation
import ゴミ箱.{InstMemory, SyncReadMEM}

class test() extends FlatSpec with ChiselScalatestTester with Matchers {
  implicit val conf = Configurations()

  "Dpath" should "" in{
    test(new Dpath()){c=>

    }
  }

  "Tile" should "" in{
    test(new Tile()).withAnnotations(Seq(VerilatorBackendAnnotation)){c=>

      for(i <- 0 to 500){
        /*
        println(
          c.io.debug.pc.peek()
        )
         */
        c.clock.step(1)
      }
      c.io.debug.reg_a0.expect(0.U)
    }
  }

  "SyncMemScala" should "" in{

    test(new SyncMemScala()).withAnnotations(Seq(VerilatorBackendAnnotation)){c=>

      c.clock.step(1)

      c.io.datamport.req.wr.poke(M_XRD)
      c.io.datamport.req.mask.poke(MT_W)
      c.io.datamport.req.enD.poke(MEN_1)
      c.io.datamport.req.addrD.poke(0.U)
      //println(c.io.datamport.resp.rdata.peek())
      c.io.datamport.resp.rdata.expect(0.U)
      c.clock.step(1)

      c.io.datamport.req.wr.poke(M_XWR)
      c.io.datamport.req.mask.poke(MT_W)
      c.io.datamport.req.enD.poke(MEN_1)
      c.io.datamport.req.addrD.poke(0.U)
      c.io.datamport.req.wdataD.poke(3.U)
      c.clock.step(1)
      //println(c.io.datamport.resp.rdata.peek())

      c.io.datamport.req.wr.poke(M_XRD)
      c.io.datamport.req.mask.poke(MT_W)
      c.io.datamport.req.enD.poke(MEN_1)
      c.io.datamport.req.addrD.poke(0.U)
      c.clock.step(1)
      c.io.datamport.resp.rdata.expect(3.U)

      c.io.datamport.req.wr.poke(M_XRD)
      c.io.datamport.req.mask.poke(MT_W)
      c.io.datamport.req.enD.poke(MEN_1)
      c.io.datamport.req.addrD.poke(0.U)
      c.clock.step(1)
      c.io.datamport.resp.rdata.expect(3.U)

      c.clock.step(1)
      c.io.datamport.resp.rdata.expect(3.U)
      //println(c.io.datamport.resp.rdata.peek())


    }
  }

  "instSyncMemScala" should "" in{

    test(new SyncMemScala()).withAnnotations(Seq(VerilatorBackendAnnotation)){c=>


      c.io.instmport.req.renI.poke(MEN_1)
      c.io.instmport.req.raddrI.poke(0.U)
      c.clock.step(1)



      c.clock.step(1)


    }
  }


  "InstMemory" should "" in{
    test(new InstMemory()){c=>
      c.io.raddr.poke(5.U)
      c.io.fcn.poke(M_XRD)
      c.io.typ.poke(MT_B)
      c.clock.step(1)


      c.clock.step(1)
      //println(c.io.rdata.peek())

    }
  }


}


