find_tests(){
    find isa/rv32ui \
         -name *.S \
         -exec basename \
         {} .S \; \
        | sort \
        | grep -v -E '(amo|lrsc|fence)' \
        | grep -v -E '(divuw|divw|mulw|lwu|ld)'

}
# Don't include A-extension (atomic operations) and rv64ui tests.

for t in $(find_tests); do
    timeout 10m make ROM=generated/rv32ui-p-${t} riscv.test
done
