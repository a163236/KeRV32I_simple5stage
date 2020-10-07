package rv32i_5stage

import common.Configurations
import rv32i_5stage.PipelineRegisters._
import rv32i_5stage.PipelineStages._

object ElaborateVerilog extends App{

  //val dir = new File(args(0)) // args(0)のディレクトリのオブジェクトを作って
  //dir.mkdirs                  // args(0)ディレクトリを作成する

  implicit val conf = Configurations()

  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new Tile())
  //val verilog = new FileWriter(new File(dir, s"main.v"))
  //verilog write verilogString
  //verilog.close()

  println(verilogString) // verilog のprint

}