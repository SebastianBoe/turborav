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
            file.write_le_u32(instr.to_binary().unwrap());
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
    let file_name = "target/swag.bin";
    let instructions = vec![Nop, Nop];
    let writer = ProgramFileWriter::new(file_name);
    writer.write_instructions(&instructions);

    let path = Path::new(file_name);
    assert!(path.exists());
    assert!(path.is_file());
    assert_eq!(8u64, path.stat().unwrap().size);

    let mut file = File::open(&path).unwrap();
    let mut expectedFile = File::open(&Path::new("resources/test1.bin")).unwrap();

    assert_eq!(file.read_to_end().unwrap(), expectedFile.read_to_end().unwrap());
}


