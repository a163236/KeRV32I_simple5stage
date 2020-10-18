module SyncMem
    #(parameter INIT_HEX_FILE = "./testfolder/hexfile/rv32ui/rv32ui-p-and.hex")
    (
    // クロック
    input clk,

    // 命令メモリ================
    input [31:0] raddrI,
    input renI,
    output reg [31:0] rdataI,

    input [31:0] addrD,
    // データメモリ 読み込み
    input renD,
    output reg [31:0] rdataD,

    // データメモリ 書き込み
    input wenD,
    input [31:0] wdataD,
    input [3:0] MaskD


    );

    integer i;

    reg [31:0] mem[0:1024*32];

    // メモリの初期化
    initial begin
        if (INIT_HEX_FILE != "") begin
            $readmemh(INIT_HEX_FILE, mem);
        end
    end

    // 命令メモリ
    always @(posedge clk) begin
        // read のみ
        if (renI) begin
            rdataI <= mem[raddrI[31:2]];
        end
    end

    // データメモリ
    always @(posedge clk) begin
        // read
        if(renD)begin
            rdataD <= mem[addrD[31:2]];
        end

        // write
        if(wenD)begin
            for(i=0;i<4;i=i+1)begin
                if(MaskD[i])begin
                    mem[addrD[31:2]][i*8 +: 8] <= wdataD[i*8 +: 8];
                end
            end
        end

    end


endmodule
