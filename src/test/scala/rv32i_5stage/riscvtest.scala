package rv32i_5stage

import chisel3._
import chiseltest._
import org.scalatest._
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.{VerilatorBackendAnnotation, WriteVcdAnnotation}
import common._
import common.CommonPackage._

import scala.Console._
import scala.io.Source
import scala.reflect.io._

class riscvtest() extends FlatSpec with ChiselScalatestTester with Matchers{
  implicit val conf = Configurations()

  def entrymemory(filename: String): Unit = {
    test(new Tile(filename)).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) { c =>
      println(BLUE+filename+RESET)  // ファイル名表示
      c.clock.setTimeout(10000)     // タイムアウト 設定
      for (i <- 1 to 1000) {
        c.clock.step(1)
      }
      c.io.led.out.expect(90.U) // gpレジスタが ならパス
    }
  }


  behavior of "rv32ui-"
  val rv32ui_path = "testfolder/hexfile/rv32ui/"

  "temp_keita" should "pass" in {entrymemory(rv32ui_path+"temp_keita.hex")}
  "add" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-add.hex")}
  "addi" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-addi.hex")}
  "and" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-and.hex")}
  "andi" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-andi.hex")}
  "auipc" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-auipc.hex")}


  "beq" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-beq.hex")}
  "bge" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-bge.hex")}
  "bgeu" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-bgeu.hex")}
  "blt" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-blt.hex")}
  "bltu" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-bltu.hex")}
  "bne" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-bne.hex")}

  "fence_i" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-fence_i.hex")}

  "jal" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-jal.hex")}
  "jalr" should "pass" in {entrymemory(rv32ui_path+"rv32ui-p-jalr.hex")}
  "lb" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lb.hex")}
  "lbu" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lbu.hex")}
  "lh" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lh.hex")}
  "lhu" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lhu.hex")}
  "lui" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lui.hex")}
  "lw" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-lw.hex")}

  "or" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-or.hex")}
  "ori" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-ori.hex")}

  "sb" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sb.hex")}
  "sh" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sh.hex")}
  "simple" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-simple.hex")}
  "sll" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sll.hex")}
  "slli" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-slli.hex")}
  "slt" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-slt.hex")}
  "slti" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-slti.hex")}
  "sltiu" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sltiu.hex")}
  "sltu" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sltu.hex")}
  "sra" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sra.hex")}
  "srai" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-srai.hex")}
  "srl" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-srl.hex")}
  "srli" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-srli.hex")}
  "sub" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sub.hex")}
  "sw" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-sw.hex")}

  "xor" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-xor.hex")}
  "xori" should "pass" in{entrymemory(rv32ui_path+"rv32ui-p-xori.hex")}


  behavior of "rv32mi-"
  val rv32mi_path = "testfolder/hexfile/rv32mi/"
  //"csr" should "pass" in {entrymemory(rv32mi_path+"rv32mi-p-csr.hex")}
  //"scall" should "pass" in {entrymemory(rv32mi_path+"rv32mi-p-scall.hex")}


}
