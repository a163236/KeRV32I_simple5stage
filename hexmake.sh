#!/usr/bin/bash
 
echo hello-world
target_dir=./testfolder
target_dumpdir=$target_dir/dumpfile
target_hexdir=$target_dir/hexfile
rv32mi_dir=$target_hexdir/rv32mi/
rv32ui_dir=$target_hexdir/rv32ui/
src_dir=./riscv-tests/isa/

# testfolerがなければ作る
if [ ! -d $target_dir ]; then
    mkdir $target_dir
fi

# dumpdirがなければ作る
if [ ! -d $target_dumpdir ]; then
    mkdir $target_dumpdir
fi

# hexdirがなければ作る
if [ ! -d $target_hexdir ]; then
    mkdir $target_hexdir
fi

# rv32uiがなければ作る
if [ ! -d $rv32ui_dir ]; then
    mkdir $rv32ui_dir
fi
# rv32miがなければ作る
if [ ! -d $rv32mi_dir ]; then
    mkdir $rv32mi_dir
fi

# riscv-tests/isa make
cd $src_dir
make
cd ../../

# コンパイル
cd $src_dir
for i in $(find -executable -type f);do
    riscv32-unknown-elf-objcopy -O binary $i $i.bin && hexdump -v -e '1/4 "%08x" "\n"' $i.bin > $i.hex    
done
cd ../../

# dumpファイルを移動
for i in $(find $src_dir*.dump); do
    mv ${i} $target_dumpdir
done 

# rv32uiに移動
for i in $(find $src_dir/rv32ui*.hex); do
    mv ${i} $rv32ui_dir
done 

# rv32miに移動
for i in $(find $src_dir/rv32mi*.hex); do
    mv ${i} $rv32mi_dir
done 

# 最後にきれいにする
cd $src_dir
make clean
cd ../../



# hexファイルを移動
#for i in $(find $src_dir*.hex); do
#    mv ${i} $target_hexdir
#done 


