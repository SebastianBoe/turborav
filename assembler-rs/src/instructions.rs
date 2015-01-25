#[derive(Show, PartialEq)]
pub enum Instruction {
    Nop
}

impl Instruction {
    pub fn to_binary(&self) -> Result<u32, String> {
        match self {
            Nop => Ok(0x13)
        }
    }
}

#[test]
fn verify_nop_binary_conversion() {
    assert_eq!(0x13, Instruction::Nop.to_binary().unwrap());
}
