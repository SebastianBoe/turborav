type Register = u32;
type Immediate = i32;


#[derive(Show, PartialEq)]
pub enum Instruction {
    Nop,
    Addi(Register, Register, Immediate)
}

impl Instruction {
    pub fn to_binary(&self) -> u32 {
        match *self {
            Instruction::Nop => 0x13,
            Instruction::Addi(reg, dest, imm) => (0b0010011 | (dest << 7) 
                | (0b11 << 12) | (reg << 15) | ((imm as u32) << 20))
        }
    }
}

#[test]
fn verify_nop_binary_conversion() {
    assert_eq!(0x13, Instruction::Nop.to_binary());
}

#[test]
fn verify_add_binary_conversion() {
    assert_eq!(0x7ff30193, Instruction::Addi(3, 6, 2047).to_binary());
}
