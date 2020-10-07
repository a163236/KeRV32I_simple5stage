package common {

  import chisel3._

  case class Configurations() {
    val xlen = 32 // 整数レジスタbit長
    val xprlen = 32 // レジスタ本数
  }

}