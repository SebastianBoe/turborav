#![feature(plugin)]
#[plugin] extern crate peg_syntax_ext;
peg_file! grammar("grammar.rustpeg");

use std::os;
use writer::*;
use reader::*;

mod instructions;
mod writer;
mod reader;
mod grammar_tests;

fn main() {
    let args = os::args();  
    match args.len() {
        0 => unreachable!(),
        3 => assemble(args[1].as_slice(), args[2].as_slice()),
        _ => println!("Usage: {} input-file output-file", args[0])
    };
}

fn assemble(input_file: &str, output_file: &str) {
    let reader = AssemblyFileReader::new(input_file);
    let writer = ProgramFileWriter::new(output_file);
    let instructions = reader.read_instructions();
    writer.write_instructions(&instructions);
}
