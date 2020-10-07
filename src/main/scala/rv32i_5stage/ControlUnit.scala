package rv32i_5stage

import chisel3._
import chisel3.util._
import common._
import common.Instructions._
import CommonPackage._

class CtrlEX extends Bundle{  // 実行ステージの制御信号
  val op1_sel = Output(UInt(OP1_X.getWidth.W))
  val op2_sel = Output(UInt(OP2_X.getWidth.W))
  val imm_sel = Output(UInt(IMM_X.getWidth.W))
  val alu_fun = Output(UInt(ALU_X.getWidth.W))
}

class CtrlMEM extends Bundle{ // メモリステージの制御信号
  val dmem_en = Output(Bool())
  val dmem_wr = Output(Bool())
  val dmem_mask = Output(UInt(MT_X.getWidth.W))
}

class CtrlWB extends Bundle{  // ライトバックステージの制御信号
  val wb_sel = Output(UInt(WB_X.getWidth.W))
  val rf_wen  = Output(Bool())
}

class ControlUnitIO extends Bundle{
  val inst = Input(UInt(32.W))
  val ctrlEX = new CtrlEX
  val ctrlMEM = new CtrlMEM
  val ctrlWB = new CtrlWB
}

class ControlUnit extends Module{
  val io = IO(new ControlUnitIO)
  io := DontCare

  val csignals =
    ListLookup(io.inst,
                 List(N, BR_N,   OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
      Array(    /* val  |  BR  |  op1   | op2  | imm  |   ALU   | wb  |  rf  |  mem  |  mem | mask | csr | fence.i*/
                /* inst | type |  sel   | sel  | sel  |   fcn   | sel |  wen |   en  |  wr  | type | cmd |        */
        LW    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_MEM, REN_1, MEN_1, M_XRD, MT_W, CSR.N, N),
        LB    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_MEM, REN_1, MEN_1, M_XRD, MT_B, CSR.N, N),
        LBU   -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_MEM, REN_1, MEN_1, M_XRD, MT_BU, CSR.N, N),
        LH    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_MEM, REN_1, MEN_1, M_XRD, MT_H, CSR.N, N),
        LHU   -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_MEM, REN_1, MEN_1, M_XRD, MT_HU, CSR.N, N),
        SW    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_S, ALU_ADD, WB_X, REN_0, MEN_1, M_XWR, MT_W, CSR.N, N),
        SB    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_S, ALU_ADD, WB_X, REN_0, MEN_1, M_XWR, MT_B, CSR.N, N),
        SH    -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_S, ALU_ADD, WB_X, REN_0, MEN_1, M_XWR, MT_H, CSR.N, N),

        AUIPC -> List(Y, BR_N, OP1_PC, OP2_IMM, IMM_U, ALU_ADD, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        LUI   -> List(Y, BR_N, OP1_X, OP2_IMM, IMM_U, ALU_COPYrs2, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),

        // I形式
        ADDI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_ADD, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        ANDI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_AND, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        ORI   -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_OR, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        XORI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_XOR, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SLTI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_SLT, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SLTIU -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_SLTU, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SLLI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_SLL, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SRAI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_SRA, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SRLI  -> List(Y, BR_N, OP1_RS1, OP2_IMM, IMM_I, ALU_SRL, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),

        // R形式
        SLL   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SLL, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        ADD   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_ADD, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SUB   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SUB, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SLT   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SLT, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SLTU  -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SLTU, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        AND   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_AND, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        OR    -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_OR, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        XOR   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_XOR, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SRA   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SRA, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        SRL   -> List(Y, BR_N, OP1_RS1, OP2_RS2, IMM_X, ALU_SRL, WB_ALU, REN_1, MEN_0, M_X, MT_X, CSR.N, N),

        // J形式
        JAL   -> List(Y, BR_J, OP1_PC, OP2_IMM, IMM_J, ALU_ADD, WB_PC4, REN_1, MEN_0, M_X, MT_X, CSR.N, N),
        JALR  -> List(Y, BR_JR, OP1_RS1, OP2_IMM, IMM_J, ALU_ADD, WB_PC4, REN_1, MEN_0, M_X, MT_X, CSR.N, N),

        // B形式
        BEQ   -> List(Y, BR_EQ, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
        BNE   -> List(Y, BR_NE, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
        BGE   -> List(Y, BR_GE, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
        BGEU  -> List(Y, BR_GEU, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
        BLT   -> List(Y, BR_LT, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),
        BLTU  -> List(Y, BR_LTU, OP1_PC, OP2_IMM, IMM_B, ALU_ADD, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N),

        // CSR
        CSRRWI -> List(Y, BR_N, OP1_IMZ, OP2_X, IMM_Z, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.W, N),
        CSRRSI -> List(Y, BR_N, OP1_IMZ, OP2_X, IMM_Z, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.S, N),
        CSRRCI -> List(Y, BR_N, OP1_IMZ, OP2_X, IMM_Z, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.C, N),
        CSRRW  -> List(Y, BR_N, OP1_RS1, OP2_X, IMM_X, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.W, N),
        CSRRS  -> List(Y, BR_N, OP1_RS1, OP2_X, IMM_X, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.S, N),
        CSRRC  -> List(Y, BR_N, OP1_RS1, OP2_X, IMM_X, ALU_COPYrs1, WB_CSR, REN_1, MEN_0, M_X, MT_X, CSR.C, N),

        ECALL  -> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.I, N),
        MRET   -> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.I, N),
        //DRET    -> List(Y, BR_N  , OP1_X  , OP2_X  ,  ALU_X    , WB_X  , REN_0, MEN_0, M_X  , MT_X,  CSR.I),
        EBREAK -> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.I, N),
        WFI    -> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, N), // implemented as a NOP

        FENCE_I-> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_0, M_X, MT_X, CSR.N, Y),
        FENCE  -> List(Y, BR_N, OP1_X, OP2_X, IMM_X, ALU_X, WB_X, REN_0, MEN_1, M_X, MT_X, CSR.N, N)

      ))

  // 上のリストで選ばれたものを変数にぶち込む
  val (cs_val_inst: Bool) :: cs_br_type         :: cs_op1_sel            :: cs_op2_sel            :: cs0 = csignals
  val cs_imm_sel          :: cs_alu_fun         :: cs_wb_sel             :: (cs_rf_wen: Bool)     :: cs1 = cs0
  val (cs_mem_en: Bool)   :: cs_mem_fcn         :: cs_msk_sel            :: cs_csr_cmd            :: (cs_fencei: Bool) :: Nil = cs1

  // 実行ステージ
  io.ctrlEX.alu_fun := cs_alu_fun
  io.ctrlEX.imm_sel := cs_imm_sel
  io.ctrlEX.op1_sel := cs_op1_sel
  io.ctrlEX.op2_sel := cs_op2_sel

  // メモリステージ


  // ライトバック
  io.ctrlWB.rf_wen := cs_rf_wen


}
