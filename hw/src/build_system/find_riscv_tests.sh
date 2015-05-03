# Usage: sh find_riscv_tests.sh isa/rv32ui

# Don't include A-extension (atomic operations) or rv64ui tests.
find $1 \
     -name *.S \
     -exec basename \
     {} .S \; \
    | sort \
    | grep -v -E '(amo|lrsc|fence)' \
    | grep -v -E '(divuw|divw|mulw|lwu|ld)'

