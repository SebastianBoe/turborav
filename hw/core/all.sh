for t in $(find isa/rv32ui -name *.S -exec basename {} .S \; | sort); do
    timeout 1m make ROM=isa/rv32ui-p-${t} riscv.test
done
