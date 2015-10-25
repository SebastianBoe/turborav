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

### Install dependencies

Install docker.
```
Assuming your current directory is . and your repo is one level below you at turborav.
docker build -t turboimage - < turborav/Dockerfile
docker run -it turboimage -v turborav:/mnt/turborav
cd /mnt/turborav/hw && scons build/test && scons --help
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
edited with the powerful IntelliJ IDE. Pretty neat huh?

![](/hw/doc/development_environment.jpg?raw=true)
