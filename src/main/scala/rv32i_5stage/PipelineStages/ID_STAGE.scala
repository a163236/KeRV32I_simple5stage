package rv32i_5stage.PipelineStages

import chisel3._
import chisel3.util._
import common._
import common.CommonPackage._
import rv32i_5stage._
import rv32i_5stage.PipelineRegisters._

class IDtoIFjumpsignalsIO extends Bundle{
  val pcPlusImm = Output(UInt(32.W))
  val branchbool = Output(Bool())
}


class ID_STAGE_IO extends Bundle {
  // 入力
  val in = Flipped(new IFID_REGS_Output)
  val hazard_IDEX_IO = new Hazard_IDEX_IO
  val flush = Input(Bool())

  // 出力
  val out = new IDEX_REGS_Output
  val registerFileIO = Flipped(new RegisterFileIO)
  val hazard_Stall = Output(Bool())
  val idtoIFjumpsignalsIO = new IDtoIFjumpsignalsIO() // idステージへのジャンプ命令

}

class ID_STAGE extends Module{
  val io = IO(new ID_STAGE_IO)
  io := DontCare
  io.registerFileIO := DontCare

  val ctrlUnit = Module(new Decoder())
  val HazardUnit = Module(new HazardUnit())
  val ImmGen = Module(new ImmGen())

  val rs1_addr = io.in.inst(RS1_MSB, RS1_LSB)
  val rs2_addr = io.in.inst(RS2_MSB, RS2_LSB)
  val rd_addr = io.in.inst(RD_MSB, RD_LSB)

  // 出力
  io.out.pc := io.in.pc
  io.out.rs1 := io.registerFileIO.rs1_data
  io.out.rs2 := io.registerFileIO.rs2_data
  io.out.rs1_addr := rs1_addr
  io.out.rs2_addr := rs2_addr
  io.out.rd_addr := rd_addr
  io.out.inst := io.in.inst
  io.out.csr_addr := io.in.inst(CSR_ADDR_MSB, CSR_ADDR_LSB)
  io.out.immediates <> ImmGen.io.out   // 即値出力

  // フラッシュストールのときの信号
  when(io.flush || HazardUnit.io.stall){
    io.out.ctrlEX.br_type := 0.U
    io.out.ctrlEX.op1_sel := 0.U
    io.out.ctrlEX.op2_sel := 0.U
    io.out.ctrlEX.imm_sel := 0.U
    io.out.ctrlEX.alu_fun := 0.U
    io.out.ctrlMEM.dmem_en := 0.U
    io.out.ctrlMEM.dmem_wr := 0.U
    io.out.ctrlMEM.dmem_mask := 0.U
    io.out.ctrlMEM.csr_cmd := 0.U
    io.out.ctrlWB.wb_sel := 0.U
    io.out.ctrlWB.rf_wen := 0.U

  }.otherwise{
    io.out.ctrlEX <> ctrlUnit.io.ctrlEX
    io.out.ctrlMEM <> ctrlUnit.io.ctrlMEM
    io.out.ctrlWB <> ctrlUnit.io.ctrlWB
  }


  // 即値生成
  ImmGen.io.inst := io.in.inst
  when(ctrlUnit.io.ctrlEX.br_type === BR_J){  // j,jal 命令
    io.idtoIFjumpsignalsIO.branchbool := true.B
    io.idtoIFjumpsignalsIO.pcPlusImm := io.in.pc + ImmGen.io.out.j_type_imm
    io.out.ctrlEX.br_type := BR_N  // コントロールを無効に
  }.elsewhen(ctrlUnit.io.ctrlEX.br_type === BR_JR){ // jalr命令
    io.idtoIFjumpsignalsIO.branchbool := true.B
    io.idtoIFjumpsignalsIO.pcPlusImm := io.in.pc + ImmGen.io.out.i_type_imm
    io.out.ctrlEX.br_type := BR_N
  }.otherwise{    // 分岐命令 他
    io.idtoIFjumpsignalsIO.branchbool := false.B
    io.idtoIFjumpsignalsIO.pcPlusImm := 0.U
  }


  // 制御への送信
  ctrlUnit.io.inst := io.in.inst
  // ハザードユニットへの送信
  HazardUnit.io.ifid.rs1_addr := rs1_addr
  HazardUnit.io.ifid.rs2_addr := rs2_addr
  HazardUnit.io.idex.rd_addr := io.hazard_IDEX_IO.rd_addr
  HazardUnit.io.idex.mem_en := io.hazard_IDEX_IO.mem_en
  HazardUnit.io.idex.mem_wr := io.hazard_IDEX_IO.mem_wr

  // 外部のレジスタへアドレス送信
  io.registerFileIO.rs1_addr := rs1_addr
  io.registerFileIO.rs2_addr := rs2_addr

  // ハザードからの受信 ストール情報
  io.hazard_Stall := HazardUnit.io.stall
}
