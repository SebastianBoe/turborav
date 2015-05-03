from subprocess import check_output

def find_riscv_tests():
    cmd = ["sh", "src/build_system/find_riscv_tests.sh", "isa/rv32ui"]
    return check_output(cmd).split()
