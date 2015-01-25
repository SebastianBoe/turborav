use std::io::fs::File;
use std::path::posix::Path;
use std::io::fs::PathExtensions;
use instructions::Instruction;
use instructions::Instruction::*;

pub trait InstructionWriter {
    fn write_instructions(&self, instructions: &Vec<Instruction>);
}

pub struct ProgramFileWriter {
    path: Path
}

impl InstructionWriter for ProgramFileWriter {
    fn write_instructions(&self, instructions: &Vec<Instruction>) {
        let mut file = File::create(&self.path).unwrap();
        for instr in instructions.iter() {
            let hex = format!("{:08X}", instr.to_binary());
            file.write_line(hex.as_slice());
        }
        file.fsync();
    }
}

impl ProgramFileWriter {
    pub fn new(file_name: &str) -> ProgramFileWriter {
        ProgramFileWriter{path: Path::new(file_name)}
    } 
}

#[test]
fn verify_writing_instructions_to_file() {
    let file_name = "target/swag.hex";
    let instructions = vec![Nop, Nop];
    let writer = ProgramFileWriter::new(file_name);
    writer.write_instructions(&instructions);

    let mut file = File::open(&Path::new(file_name)).unwrap();
    let mut expectedFile = File::open(&Path::new("resources/test1.hex")).unwrap();

    assert_eq!(file.read_to_end().unwrap(), expectedFile.read_to_end().unwrap());
}


