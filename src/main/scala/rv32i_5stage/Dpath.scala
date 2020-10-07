package rv32i_5stage

import chisel3._
import chisel3.util._
import common._
import CommonPackage._
import treadle.executable.MuxLongs
import rv32i_5stage.PipelineRegisters._
import rv32i_5stage.PipelineStages._

class DpathIO(implicit val conf:Configurations) extends Bundle(){
  val imem = new InstMemPortIO()  // 命令メモリIO
  val dmem = new DataMemPortIO()  // データメモリIO
  val debug = new DebugIO()        // デバッグIO
  val led = new LEDDebugIO()        // LED用
}

class Dpath(implicit val conf:Configurations) extends Module{
  val io = IO(new DpathIO())
  io := DontCare

  // https://inst.eecs.berkeley.edu/~cs61c/resources/su18_lec/Lecture13.pdf
  // まずは使うModule宣言
  val regFile = Module(new RegisterFile())

  //==============================================

  // ステージ
  val if_stage = Module(new IF_STAGE)
  val id_stage = Module(new ID_STAGE)
  val exe_stage = Module(new EXE_STAGE)
  val mem_stage = Module(new MEM_STAGE)
  val wb_stage = Module(new WB_STAGE)
  // パイプラインレジスタ
  val ifid_regs = Module(new IFID_REGS)
  val idex_regs = Module(new IDEX_REGS)
  val exmem_regs = Module(new EXMEM_REGS)
  val memwb_regs = Module(new MEMWB_REGS)

  ifid_regs.io.in := if_stage.io.out
  id_stage.io.in := ifid_regs.io.out
  idex_regs.io.in := id_stage.io.out
  exe_stage.io.in := idex_regs.io.out
  exmem_regs.io.in := exe_stage.io.out
  mem_stage.io.in := exmem_regs.io.out
  memwb_regs.io.in := mem_stage.io.out
  wb_stage.io.in := memwb_regs.io.out

  // 命令メモリ接続
  io.imem <> if_stage.io.imem
  //io.imem <> ifid_regs.io.imem

  // データメモリ接続
  io.dmem <> mem_stage.io.dmem

  // レジスタファイル接続
  id_stage.io.registerFileIO := DontCare
  wb_stage.io.registerFileIO := DontCare
  regFile.io.rs1_addr := id_stage.io.registerFileIO.rs1_addr
  regFile.io.rs2_addr := id_stage.io.registerFileIO.rs2_addr
  id_stage.io.registerFileIO.rs1_data := regFile.io.rs1_data
  id_stage.io.registerFileIO.rs2_data := regFile.io.rs2_data
  regFile.io.wen := wb_stage.io.registerFileIO.wen
  regFile.io.waddr := wb_stage.io.registerFileIO.waddr
  regFile.io.wdata := wb_stage.io.registerFileIO.wdata

  io.led.out := regFile.io.reg_a0


  // *** DEBUG ************************************************************************************
  io.led.out := regFile.io.reg_a0

  io.debug.pc := ifid_regs.io.out.pc

  printf("pc_IFID=[%x] inst_IFID=[%x] || " +
    "pc_IDEX=[%x] rs1_IDEX=[%x] rs2_IDEX=[%x] inst_IDEX=[%x]  || " +
    "pc_mem=[%x] alu_out=[%x] rs2_mem=[%x] inst_mem=[%x]  || " +
    "memStage_out=[%x] inst_wb=[%x]  || " +
    "refwen=[%x] regwaddr=[%x] regwdata=[%x] reg_a0=[%x] " +
    " || "

    , ifid_regs.io.out.pc, ifid_regs.io.out.inst
    , idex_regs.io.out.pc, idex_regs.io.out.rs1, idex_regs.io.out.rs2, idex_regs.io.out.inst
    , exmem_regs.io.out.pc, exmem_regs.io.out.alu, exmem_regs.io.out.rs2, exmem_regs.io.out.inst
    , memwb_regs.io.out.alu, memwb_regs.io.out.inst
    , regFile.io.wen, regFile.io.waddr, regFile.io.wdata, regFile.io.reg_a0
    ,
  )

  printf("\n")

}
