#[cfg(Test)]
use instructions::Instruction;
use instructions::Instruction::*;
use grammar::*;

#[test]
fn verify_that_single_nop_is_parsed() {
    let result = instructions("nop");
    assert!(result.is_ok());
    let instructions = result.unwrap();
    assert_eq!(1, instructions.len());
    assert_eq!(instructions[0], Nop);
}

#[test]
fn verify_that_multiple_nops_are_parsed() {
    let result = instructions("nop\nnop\nnop");
    assert!(result.is_ok());
    let instructions = result.unwrap();
    assert_eq!(3, instructions.len());
    assert_eq!(instructions[0], Nop);
    assert_eq!(instructions[1], Nop);
    assert_eq!(instructions[2], Nop);
}

// TODO Make parser handling dangling newline
