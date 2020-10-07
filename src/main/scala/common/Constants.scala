package common


import chisel3._

trait RISCVConstants{

  implicit val conf = Configurations()

  // abstract out instruction decode magic numbers
  val RS1_MSB = 19  // instからレジスタ1への最上位ビット
  val RS1_LSB = 15  // instからレジスタ1への最下位ビット
  val RS2_MSB = 24
  val RS2_LSB = 20
  val RD_MSB  = 11  // 書き込みレジスタアドレス
  val RD_LSB  = 7
  val CSR_ADDR_MSB = 31
  val CSR_ADDR_LSB = 20


  // バブル命令 マシンではNOP
  // ソフトウェアコンパイラでは本来(ADDI x0, x0, 0)を入れるが
  // stat-trackers diffrentiateを可視化するために(XOR, x0,x0,x0)を入れている
  val BUBBLE = 0x4033.U(conf.xlen.W)
}

trait SodorProcConstants{
  // デバッグの場合は、コミット情報を表示します。
  // riscv-isa-run Spike ISA シミュレータのコミット・ロガーと比較することができます。
  val PRINT_COMMIT_LOG = false
}

trait ScalarOpConstants{
  //************************************
  // Control Signal

  val Y     = true.B
  val N     = false.B

  // PC Select
  val PC_4   = 0.asUInt(3.W)  // PC + 4
  val PC_ALU = 1.asUInt(3.W)  // ALUの結果
  val PC_CSR = 2.asUInt(3.W)  // CSR- 割り込みか例外

  // Branch Type
  val BR_N   = 0.asUInt(4.W)  // Next
  val BR_NE  = 1.asUInt(4.W)  // Branch on NotEqual
  val BR_EQ  = 2.asUInt(4.W)  // Branch on Equal
  val BR_GE  = 3.asUInt(4.W)  // Branch on Greater/Equal
  val BR_GEU = 4.asUInt(4.W)  // Branch on Greater/Equal Unsigned
  val BR_LT  = 5.asUInt(4.W)  // Branch on Less Than
  val BR_LTU = 6.asUInt(4.W)  // Branch on Less Than Unsigned
  val BR_J   = 7.asUInt(4.W)  // Jump
  val BR_JR  = 8.asUInt(4.W)  // Jump Register

  // Imm Select
  val IMM_X  = 0.U(3.W)
  val IMM_I  = 1.U(3.W)
  val IMM_S  = 2.U(3.W)
  val IMM_U  = 3.U(3.W)
  val IMM_J  = 4.U(3.W)
  val IMM_B  = 5.U(3.W)
  val IMM_Z  = 6.U(3.W)

  // RS1 Operand Select
  val OP1_RS1 = 0.asUInt(2.W) // Register Source #1
  val OP1_PC  = 1.asUInt(2.W)
  val OP1_X   = 0.asUInt(2.W)

  // RS2 Operand Select
  val OP2_RS2 = 0.asUInt(2.W) // Register Source #2
  val OP2_IMM = 1.asUInt(2.W) // 即値
  val OP1_IMZ = 2.asUInt(2.W) // Zero-extended rs1 field of inst, for CSRI instructions
  val OP2_X   = 0.asUInt(2.W)

  // Register File Write Enable Signal レジスタファイル書き込み有効信号
  val REN_0   = false.B
  val REN_1   = true.B
  val REN_X   = false.B

  // ALU
  val ALU_ADD = 1.asUInt(4.W)
  val ALU_SUB = 2.asUInt(4.W)
  val ALU_SLL = 3.asUInt(4.W)
  val ALU_SRL = 4.asUInt(4.W)
  val ALU_SRA = 5.asUInt(4.W)
  val ALU_AND = 6.asUInt(4.W)
  val ALU_OR  = 7.asUInt(4.W)
  val ALU_XOR = 8.asUInt(4.W)
  val ALU_SLT = 9.asUInt(4.W)
  val ALU_SLTU= 10.asUInt(4.W)
  val ALU_COPYrs1= 11.asUInt(4.W)
  val ALU_COPYrs2= 12.asUInt(4.W)
  val ALU_X   = 0.asUInt(4.W)

  // Writeback Select
  val WB_ALU  = 0.asUInt(2.W)
  val WB_MEM  = 1.asUInt(2.W)
  val WB_PC4  = 2.asUInt(2.W)
  val WB_CSR  = 3.asUInt(2.W)
  val WB_X    = 0.asUInt(2.W)

  // Memory Function Type (Read,Write,Fence) Signal
  val MWR_R   = 0.asUInt(2.W)
  val MWR_W   = 1.asUInt(2.W)
  val MWR_F   = 2.asUInt(2.W)
  val MWR_X   = 0.asUInt(2.W)

  // Memory Enable Signal
  val MEN_0   = false.B
  val MEN_1   = true.B
  val MEN_X   = false.B

  // Memory Mask Type Signal  word-halfword-など
  val MSK_B   = 0.asUInt(3.W)
  val MSK_BU  = 1.asUInt(3.W)
  val MSK_H   = 2.asUInt(3.W)
  val MSK_HU  = 3.asUInt(3.W)
  val MSK_W   = 4.asUInt(3.W)
  val MSK_X   = 4.asUInt(3.W)

  // Cache Flushes & Sync Primitives
  val M_N      = 0.asUInt(3.W)
  val M_SI     = 1.asUInt(3.W)   // synch instruction stream
  val M_SD     = 2.asUInt(3.W)   // synch data stream
  val M_FA     = 3.asUInt(3.W)   // flush all caches
  val M_FD     = 4.asUInt(3.W)   // flush data cache

  // Memory Functions (read, write, fence)
  val MT_READ  = 0.asUInt(2.W)
  val MT_WRITE = 1.asUInt(2.W)
  val MT_FENCE = 2.asUInt(2.W)
}

trait MemoryOpConstants
{
  // typ
  val MT_X  = 0.asUInt(3.W)
  val MT_B  = 1.asUInt(3.W) // byte?
  val MT_H  = 2.asUInt(3.W) // half-word
  val MT_W  = 3.asUInt(3.W) // wordfe5212
  val MT_D  = 4.asUInt(3.W)
  val MT_BU = 5.asUInt(3.W)
  val MT_HU = 6.asUInt(3.W)
  val MT_WU = 7.asUInt(3.W)
  // fcn
  val M_X   = "b0".asUInt(1.W)
  val M_XRD = "b0".asUInt(1.W) // int load
  val M_XWR = "b1".asUInt(1.W) // int store

  val DPORT = 0
  val IPORT = 1
}

trait PrivilegedConstants{

  // システムレジスタアドレス
  // マシンのトラップハンドリングのベースレジスタ
  // https://people.eecs.berkeley.edu/~krste/papers/riscv-privileged-v1.9.1.pdf
  val MTVEC = 0x100

  val START_ADDR = "h00000000"  // MTVEC + 0x100 ?
}
