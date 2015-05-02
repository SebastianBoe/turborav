find_tests(){
    # Don't include A-extension (atomic operations) and rv64ui tests.
    find isa/rv32ui \
         -name *.S \
         -exec basename \
         {} .S \; \
        | sort \
        | grep -v -E '(amo|lrsc|fence)' \
        | grep -v -E '(divuw|divw|mulw|lwu|ld)'

}

run_tests(){
    for t in $(find_tests); do
        timeout 10m make ROM=generated/rv32ui-p-${t} riscv.test
    done
}

synthesize(){
    make bitfile
}

parse_synthesis_reports(){
    awk '/Number of Slices/{ print $4 }' generated/design_routed.par > generated/slices.txt
}

main(){
    make clean
    run_tests # Generates RiscV test results for Jenkins
    synthesize
    parse_synthesis_reports # Data for plots
}

main
