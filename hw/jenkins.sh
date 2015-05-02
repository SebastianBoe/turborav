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
    # WARNING: This can break easily. Feel free to improve.
    source /opt/Xilinx/14.7/ISE_DS/settings64.sh
    make bitfile
    cat generated/design.{mrp,map} # Print reports
}

parse_slices(){
    awk '/Number of Slices/{ print $4 }' generated/design_routed.par > generated/slices.txt
}

parse_timing(){
    awk -F "|" '/NET "clk_/{ print $4 }' generated/design_routed.par | \
        python src/synthesis/period_to_frequency.py > generated/timing.txt
}

parse_synthesis_reports(){
    parse_slices
    parse_timing
}

main(){
    make clean
    run_tests # Generates RiscV test results for Jenkins
    synthesize
    parse_synthesis_reports # Data for plots
}

main
