use std::io::fs::File;
use std::path::posix::Path;
use std::io::fs::PathExtensions;
use instructions::Instruction;
use instructions::Instruction::*;
use grammar::instructions;

pub trait InstructionReader {
    fn read_instructions(&self) -> Vec<Instruction>;
}

pub struct AssemblyFileReader {
    path: Path
}

impl InstructionReader for AssemblyFileReader {
    fn read_instructions(&self) -> Vec<Instruction> {
        let mut file = File::open(&self.path).unwrap();
        let file_content = file.read_to_string().unwrap();
        instructions(file_content.as_slice()).unwrap()
    }
}

impl AssemblyFileReader {
    pub fn new(file_name: &str) -> AssemblyFileReader {
        AssemblyFileReader{path: Path::new(file_name)}
    }
}

#[test]
fn verify_file_parsing() {
    let reader = AssemblyFileReader::new("resources/test1.S");
    let instructions = reader.read_instructions();
    assert_eq!(2, instructions.len());
    assert_eq!(vec![Nop, Nop], instructions);
}