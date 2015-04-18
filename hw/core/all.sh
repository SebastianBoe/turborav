find_tests(){
    find isa/rv32ui \
         -name *.S \
         -exec basename \
         {} .S \; \
        | sort \
        | grep --invert-match amo
}

for t in $(find_tests); do
    timeout 10m make ROM=generated/rv32ui-p-${t} riscv.test
done
