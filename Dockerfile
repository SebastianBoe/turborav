FROM pritunl/archlinux

RUN pacman --noconfirm -S \
    base-devel \
    clang \
    git \
    java-commons-io \
    java-environment \
    python-pint \
    sbt \
    scala \
    scons \
    && \
    pacman --noconfirm -Scc # Clean pacman cache before committing

# Tool usage

# base-devel        # Builds the RISC-V toolchain
# clang             # Speeds up the simulator compilation
# git               # Retrieves external dependencies
# java-commons-io   # Makes it easier to manipulate files from Scala
# java-environment  # Creates jars
# python-pint       # Does unit conversion for statistics like slice usage
# sbt               # Builds Chisel
# scala             # Runs Chisel programs
# scons             # Builds turborav


# Download the RISC-V toolchain from github and build from source

# Using the latest RISC-V toolchain causes a compilation error when
# building the RISC-V tests, but this revision is known to work. TODO:
# debug compilation error.
ENV TOOLCHAIN_REVISION f0addb7
RUN git clone https://github.com/riscv/riscv-gnu-toolchain.git
RUN pushd riscv-gnu-toolchain \
	&& git checkout $TOOLCHAIN_REVISION \
	&& ./configure --prefix=/opt/riscv \
	&& make -j8 \
	&& popd \
	&& rm -rf riscv-gnu-toolchain/
ENV PATH $PATH:/opt/riscv/bin

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
    && makepkg
USER root
RUN pacman -U --noconfirm chisel-git/chisel-git*pkg*


# Assume user is going to be mounting his local repo at /mnt/turborav
WORKDIR /mnt/turborav/hw
USER root
CMD ["/bin/bash"]
