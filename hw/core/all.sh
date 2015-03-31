for t in $(find isa/rv32ui -name *.S -exec basename {} .S \; | sort); do
    timeout 30s make ROM=isa/rv32ui-p-${t} riscv.test
    sleep 0.2s
done
