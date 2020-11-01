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
  val ForwardingUnit = Module(new ForwardingUnit())


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

  // パイプライン 数珠つなぎにつないでるだけ
  ifid_regs.io.in := if_stage.io.out
  id_stage.io.in := ifid_regs.io.out
  idex_regs.io.in := id_stage.io.out
  exe_stage.io.in := idex_regs.io.out
  exmem_regs.io.in := exe_stage.io.out
  mem_stage.io.in := exmem_regs.io.out
  memwb_regs.io.in := mem_stage.io.out
  wb_stage.io.in := memwb_regs.io.out

  // ジャンプ命令   -> IFステージへの信号
  if_stage.io.idtoIFjumpsignalsIO <> id_stage.io.idtoIFjumpsignalsIO
  if_stage.io.exetoifjumpsignals <> exe_stage.io.exetoifjumpsignals
  if_stage.io.memtoifjumpsignals <> mem_stage.io.memtoifjumpsignals
  // ジャンプ命令に付随するフラッシュ
  ifid_regs.io.pipe_flush := exe_stage.io.exetoifjumpsignals.branchbool
  id_stage.io.flush := (exe_stage.io.exetoifjumpsignals.branchbool || mem_stage.io.memtoifjumpsignals.eret)
  exe_stage.io.flush_in := mem_stage.io.memtoifjumpsignals.eret

  // ハザードユニットへの入力
  id_stage.io.hazard_IDEX_IO.rd_addr := idex_regs.io.out.rd_addr
  id_stage.io.hazard_IDEX_IO.mem_en := idex_regs.io.out.ctrlMEM.dmem_en
  id_stage.io.hazard_IDEX_IO.mem_wr := idex_regs.io.out.ctrlMEM.dmem_wr

  // ハザードユニットからの出力 ストール!
  if_stage.io.stall := id_stage.io.hazard_Stall // pcのストール？
  ifid_regs.io.pipe_stalll := id_stage.io.hazard_Stall

  // 命令メモリ接続
  io.imem <> if_stage.io.imem

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

  // CSRの接続

  // フォワーディング
  ForwardingUnit.io.fwfromWB <> wb_stage.io.fwfromWB
  ForwardingUnit.io.fwfromMEM <> mem_stage.io.fwfromMEM
  ForwardingUnit.io.fwEXE <> exe_stage.io.fwUnit

  // *** DEBUG ************************************************************************************
  io.led.out := regFile.io.reg_a0
  io.debug.pc := ifid_regs.io.out.pc


  // debugの信号線を増やさないとなぜか正しく表示されない。。。
  printf("PC=[%x] ||" +
    "pc_IFID=[%x] inst_IFID=[%x] || " +
    "pc_IDEX=[%x] rs1_IDEX=[%x] rs2_IDEX=[%x] inst_IDEX=[%x]  || " +
    "pc_EXMEM=[%x] alu_out=[%x] rs2_EXMEM=[%x] inst_EXMEM=[%x]  || " +
    "pc_MEMWB=[%x] MEMWB_out=[%x] inst_MEMWB=[%x]  || " +
    "refwen=[%x] regwaddr=[%x] regwdata=[%x] reg_a0=[%x] " +
    " || "

    , if_stage.io.out.pc
    , ifid_regs.io.out.pc, ifid_regs.io.out.inst
    , idex_regs.io.out.pc, idex_regs.io.out.rs1, idex_regs.io.out.rs2, idex_regs.io.out.inst
    , exmem_regs.io.out.pc, exmem_regs.io.out.alu, exmem_regs.io.out.rs2, exmem_regs.io.out.inst
    , memwb_regs.io.out.pc, memwb_regs.io.out.alu, memwb_regs.io.out.inst
    , regFile.io.wen, regFile.io.waddr, regFile.io.wdata, regFile.io.reg_a0
    ,
  )

  printf("\n")

}
