This is the TurboRav full computer stack hobby project.

The end goal is a self-contained system consisting of a RISC-V
processor core, assembler, compiler, and some graphical demo apps
running on top. This can be considered an exercise in extreme Not
Invented Here Syndrome.

Although third-party code-reuse is non-existant in this project, the
use of external tools strives to be bleeding edge. Haskell is the
chosen language for the compiler and Chisel as a Hardware Description
Language.

## Getting started

As of november 2014 we are still building the hw infrastructure
necessary to run a software stack on top of. So here we present only
how to get started with hw-development.

# Install packaged dependencies

   yaourt -S sbt texlive-most

# Install (yet) un-packaged dependencies

  Install the GNU toolchain for RISC-V from
  https://github.com/ucb-bar/riscv-gnu-toolchain

# Enter the core directory

   cd hw/core

# Test your environment

   make alu.test

# Peruse the issue-tracker to see if there is anything that interests
you or create your own issue based on what you want to contribute
with. But most importantly; have fun!
