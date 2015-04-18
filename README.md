This is the TurboRav full computer stack hobby project.

![](https://docs.google.com/drawings/d/1ULG8MfWGiZmn_45winMJ4qbs6qOa7lMet6E6i03F7Mk/pub?w=1875&h=1077 "")

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

### Install packaged dependencies

```
yaourt -S sbt texlive-most
```
Or for Debian based distro's
```
apt-get install sbt texlive-most autoconf automake autotools-dev libmpc-dev libmpfr-dev libgmp-dev gawk build-essential bison flex texinfo patchutils
```

### Install (yet) un-packaged dependencies

Install the GNU toolchain for RISC-V from our git submodule

```
cd hw/core/riscv-tools
git submodule init
git submodule update

cd hw/core/riscv-tools/riscv-gnu-toolchain
git submodule init
git submodule update

cd hw/core/riscv-tools/riscv-tests/env
git submodule init
git submodule update

cd hw/core/riscv-tools/riscv-gnu-toolchain
./configure --prefix=/opt/riscv # make sure you have access rights
make
```

### Test your environment

```
cd hw/core && make alu.test
```

Peruse the issue-tracker to see if there is anything that interests
you or create your own issue based on what you want to contribute
with. But most importantly; have fun!

## Development

The below screenshot demonstrates what the development environment might look
like when debugging TurboRav. On the top right hand side there is an assembly
program that is assembled to the machine code seen below. This machine code is
synthesized into the ROM and when simulated generates the waveform. The waveform
is used to find out where things are going wrong and then the Chisel code is
edited with the powerful IntelliJ IDE. Sbt continuously regenerates a new
waveform and we start all over again! Pretty neat huh?

![](/hw/doc/development_environment.jpg?raw=true)
