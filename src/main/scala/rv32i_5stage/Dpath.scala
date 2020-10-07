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

  val aLU     = Module(new ALU())
  val immGen  = Module(new ImmGen())
  val regFile = Module(new RegisterFile())
  val branchComp = Module(new BranchComp())
  // 初期化
  aLU.io:=DontCare; immGen.io:=DontCare;
  regFile.io:=DontCare; branchComp.io:=DontCare

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
  exe_stage.io.in := id_stage.io.out
  exmem_regs.io.in := exe_stage.io.out
  mem_stage.io.in := exmem_regs.io.out
  memwb_regs.io.in := mem_stage.io.out
  wb_stage.io.in := memwb_regs.io.out

  // 命令メモリ接続
  io.imem <> if_stage.io.imem

  // レジスタファイル接続
  id_stage.io := DontCare
  regFile.io.rs1_addr := id_stage.io.registerFileIO.rs1_addr
  regFile.io.rs2_addr := id_stage.io.registerFileIO.rs2_addr

  io.led.out := regFile.io.reg_a0

  /*
  // *** DEBUG ************************************************************************************
  io.led.out := regFile.io.reg_a0
  io.debug.pc := pc_reg
  io.debug.pc_decode := pc_decode
  io.debug.reg_a0 := regFile.io.reg_a0
  io.debug.inst := inst
  io.debug.alu_out := aLU.io.out

  printf("pc_reg=[%x], " +
    "pc_decode=[%x], inst=[%x] " +
    "pc_execute=[%x], rs1_execute=[%x] rs2_execute=[%x] inst_execute=[%x] " +
    "pc_mem=[%x], alu_out=[%x], rs2_mem=[%x], inst_mem=[%x] " +
    "memStage_out=[%x], inst_wb=[%x] " +
    "reg_a0=[%x] " +
    "temp=[%x] " +
    "\n"

    , pc_reg
    , pc_decode, inst
    , pc_execute, rs1_execute, rs2_execute, inst_execute
    , pc_mem, alu_out_mem, rs2_mem, inst_mem
    , memStage_out, inst_wb
    , regFile.io.reg_a0
    , ctrlUnit.io.ctrlWB.rf_wen
  )

   */

}
