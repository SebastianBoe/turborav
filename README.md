This is the TurboRav full computer stack hobby project.

Status master branch ![](https://travis-ci.org/SebastianBoe/turborav.svg?branch=master "")

Status dev branch ![](https://travis-ci.org/SebastianBoe/turborav.svg?branch=dev "")

![](https://docs.google.com/drawings/d/1yiRfiubTfP55u9E995-KuAjrXQi68SzCdmgo3fDCfAA/pub?w=1934&h=1368 "")

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

Install Docker.
```
// Download a container with all the tools from Docker hub
docker pull sebomux/turborav
// Start the container with your local repository mounted from the container.
docker run -v /absolute/path/to/turborav/repo/on/your/machine:/mnt/turborav -it sebomux/turborav
// Run a RISC-V test for addition, and see build system usage.
scons build/test/riscv/add && scons --help
```

Video demonstration
[![asciicast](https://asciinema.org/a/eenhl1vytb77pel6jrmg9qhn5.png)](https://asciinema.org/a/eenhl1vytb77pel6jrmg9qhn5)

Peruse the issue-tracker to see if there is anything that interests
you or create your own issue based on what you want to contribute
with. But most importantly; have fun!

## Tool flow
The tool flow is quite involved, luckily how to use the tools is encoded into the build system, and all the tools are found pre-installed in a docker container.
![](https://docs.google.com/drawings/d/1R1S3EaMNbQhiivbtGhVuwwE5PzFvSRuUj1LCNBwp3wo/pub?w=1884&h=1553)

## Verification

Verification is done using automated tests that generate junit.xml
files for consumption by CI tools. The SaaS CI tool Travis runs all
tests on every commit and the status can be seen
[here](https://travis-ci.org/SebastianBoe/turborav).

### SoC

The SoC is verified at the unit and system level. Unit testing of HW
modules is done with testbenches written in the very powerful scala
language. These testbenches are located [here](hw/src/test/scala/tb/).

System testing is done with C-code firmware running on the simulated
SoC. When no external stimuli from outside the SoC is needed the test
code consists solely of C-code. These are known as C-tests and can be
found [here](hw/src/test/c/modules/). These firmwares can use printf's
and assertions to report the test status. These firmware's will
hopefully be portable to the FPGA platform once it is up and running.

When external stimuli is needed, e.g. a GPIO peripheral test, the test
code consists of C-code running on the SoC and scala testbench code
that wraps the SoC. These tests are known as hybrid tests because they
are a hybrid of C-tests and Unit tests and can be found
[here](hw/src/test/hybrid).

### CPU verification

The custom RISC-V CPU is verified using assembly tests from
https://github.com/riscv/riscv-tests. And also through the SoC system
tests that run C-code programs.

### FPGA

Verification using the FPGA platform is not supported yet (currently
[2016 easter] working on some clock frequency performance bugs). But
automatic synthesis using only free and open source tools is supported
through the scons build target build/synth/yosys/icoboard.

## Git workflow

Development happens on the dev branch. When travis verifies that dev
is green it will [automatically update the master branch with the
known good commits from dev.](.travis.yml). Master is not updated
directly by developers, but it can be branched off from if a developer
wants a known good revision.

Topic branches are optional and will be tested by
[travis](https://travis-ci.org/SebastianBoe/turborav/builds) when
pushed to github.

## Development

The below screenshot demonstrates what the development environment might look
like when debugging TurboRav. On the top right hand side there is an assembly
program that is assembled to the machine code seen below. This machine code is
synthesized into the ROM and when simulated generates the waveform. The waveform
is used to find out where things are going wrong and then the Chisel code is
edited with the powerful IntelliJ IDE. Pretty neat huh?

![](/hw/doc/development_environment.jpg?raw=true)

Copyright (C) 2015 Sebastian Bøe, Joakim Andersson

License: BSD 2-Clause (see LICENSE for details)
