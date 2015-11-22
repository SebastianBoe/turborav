Copyright (C) 2015 Sebastian Bøe, Joakim Andersson

License: BSD 2-Clause (see LICENSE for details)

This is the TurboRav full computer stack hobby project.

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

Peruse the issue-tracker to see if there is anything that interests
you or create your own issue based on what you want to contribute
with. But most importantly; have fun!

## Tool flow
The tool flow is quite involved, luckily how to use the tools is encoded into the build system, and all the tools are found pre-installed in a docker container.
![](https://docs.google.com/drawings/d/1R1S3EaMNbQhiivbtGhVuwwE5PzFvSRuUj1LCNBwp3wo/pub?w=1884&h=1553)

## Development

The below screenshot demonstrates what the development environment might look
like when debugging TurboRav. On the top right hand side there is an assembly
program that is assembled to the machine code seen below. This machine code is
synthesized into the ROM and when simulated generates the waveform. The waveform
is used to find out where things are going wrong and then the Chisel code is
edited with the powerful IntelliJ IDE. Pretty neat huh?

![](/hw/doc/development_environment.jpg?raw=true)
