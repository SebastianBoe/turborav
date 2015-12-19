# The riscv-tests files committed at the moment are from revision
# 95f0b55eac15921f2133630bc6748131d8f1c0af

# This script extracts the files we use from a RISC-V repo. This is a
# bit hacky, but we don't update the RISC-V tests very often, and we
# don't have to deal with submodules if we do it manually like this ...

cp $RISCV_TESTS_REPO/LICENSE \
   $RISCV_TESTS_REPO/isa/macros/scalar/test_macros.h \
   $RISCV_TESTS_REPO/env/{encoding,hwacha_xcpt}.h \
   .

cp $RISCV_TESTS_REPO/env/p/riscv_test.h p


cp -r $RISCV_TESTS_REPO/isa/rv32ui/ .
