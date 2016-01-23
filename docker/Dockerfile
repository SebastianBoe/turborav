FROM sebomux/turborav_riscv
# turborav_riscv is an Arch Linux docker image with the specific riscv
# gcc toolchain that is compatible with the TurboRav project

RUN pacman --noconfirm -S \
    clang \
    gtkwave \
    graphviz \
    java-commons-io \
    java-environment \
    libftdi \
    mercurial \
    python-pint \
    sbt \
    scala \
    scons \
    tcl \
    && \
    pacman --noconfirm -Scc # Clean pacman cache before committing

# Tool usage

# clang             # Speeds up the simulator compilation
# graphviz          # Needed by Yosys to generate schematics
# java-commons-io   # Makes it easier to manipulate files from Scala
# java-environment  # Creates jars
# libftdi           # Needed by icestorm to flash netlists
# mercurial         # Needed by Yosys when installing abc
# python-pint       # Does unit conversion for statistics like slice usage
# sbt               # Builds Chisel
# scala             # Runs Chisel programs
# scons             # Builds turborav
# tcl               # Needed by Yosys

RUN useradd -m -G wheel turbo
# Install scalastyle from the AUR
USER turbo
WORKDIR /home/turbo
RUN git clone https://aur.archlinux.org/scalastyle.git \
    && cd scalastyle \
    && makepkg
USER root
RUN pacman -U --noconfirm scalastyle/scalastyle*pkg*

# Install chisel from Maven
# USER root
# ENV CHISEL_VERSION 2.2.31
# ENV SCALA_VERSION 2.11
# ENV CHISEL_JAR chisel_$SCALA_VERSION-$CHISEL_VERSION.jar
# RUN curl http://central.maven.org/maven2/edu/berkeley/cs/chisel_$SCALA_VERSION/$CHISEL_VERSION/$CHISEL_JAR > \
#     $CHISEL_JAR \
#     && install -Dm644 $CHISEL_JAR /usr/share/scala/chisel/chisel.jar

# Install chisel from the AUR
USER turbo
ENV CHISEL_REVISION 560d5f37ca60e629def7fc3cae7b3d343893b561
WORKDIR /home/turbo
RUN git clone https://aur.archlinux.org/chisel-git.git \
    && cd chisel-git \
    && sed -i "s chisel\.git chisel\.git#commit=$CHISEL_REVISION " PKGBUILD \
    && makepkg --clean
USER root
RUN pacman -U --noconfirm chisel-git/chisel-git*pkg*

# Install FPGA synthesis tools from the AUR
WORKDIR /home/turbo

# Build and install icestorm
RUN pacman -S --noconfirm libftdi-compat

ENV ICESTORM_REVISION 7a1e662a3f676f408f79127459be95b37e959f47
USER turbo
RUN git clone https://aur.archlinux.org/icestorm-git.git \
    && cd icestorm-git \
    && sed -i "s icestorm\.git icestorm\.git#commit=$ICESTORM_REVISION " PKGBUILD \
    && makepkg --clean
USER root
RUN pacman -U --noconfirm icestorm-git/icestorm-git*pkg*

# Build Yosys
ENV YOSYS_REVISION ab0c44d3ed81f71cb0c6ff844679110cc27b38ad
USER turbo
RUN git clone https://aur.archlinux.org/yosys-git.git \
    && cd yosys-git \
    && sed -i "s yosys\.git yosys\.git#commit=$YOSYS_REVISION " PKGBUILD \
    && makepkg --clean

# Build Arachne-pnr
ENV ARACHNE_PNR_REVISION eb7876bcfa20508075760723e7afc170e06abab6
RUN git clone https://aur.archlinux.org/arachne-pnr-git.git \
    && cd arachne-pnr-git \
    && sed -i "s arachne-pnr\.git arachne-pnr\.git#commit=$ARACHNE_PNR_REVISION " PKGBUILD \
    && makepkg --clean

# Install Yosys and Arachne-pnr
USER root
RUN pacman -U --noconfirm yosys-git/yosys-git*pkg*
RUN pacman -U --noconfirm arachne-pnr-git/arachne-pnr-git*pkg*
#RUN pacman --noconfirm -Scc

# Assume user is going to be mounting his local repo at /mnt/turborav
WORKDIR /mnt/turborav/hw
USER turbo
CMD ["/bin/bash"]
