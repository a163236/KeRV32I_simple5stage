package LEDdebug
import common._

object ElaborateLED extends App {
  implicit val conf = Configurations()

  val verilogString = (new chisel3.stage.ChiselStage).emitVerilog(new TileAndLED())

  println(verilogString) // verilog のprint

}
