FROM pritunl/archlinux

RUN pacman --noconfirm -S \
    base-devel \
    git \
    gtkwave \
    java-environment \
    scala \
    scons \
    java-commons-io \
    python-pint \
    clang \
    && \
    pacman --noconfirm -Scc # Clean pacman cache before committing

# Install the RISC-V toolchain from github and build from source

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
WORKDIR /home/turbo
USER turbo

# Install scalastyle from the AUR
RUN git clone https://aur.archlinux.org/scalastyle.git
WORKDIR scalastyle
USER turbo
RUN makepkg
USER root
RUN pacman -U --noconfirm scalastyle*pkg*
USER turbo

# Install chisel from Maven
USER root
ENV CHISEL_VERSION 2.2.28
ENV SCALA_VERSION 2.11
ENV CHISEL_JAR chisel_$SCALA_VERSION-$CHISEL_VERSION.jar
RUN curl http://central.maven.org/maven2/edu/berkeley/cs/chisel_$SCALA_VERSION/$CHISEL_VERSION/$CHISEL_JAR > \
    $CHISEL_JAR \
    && install -Dm644 $CHISEL_JAR /usr/share/scala/chisel/chisel.jar

# Assume user is going to be mounting his local repo at /mnt/turborav
WORKDIR /mnt/turborav/hw

USER root

CMD ["/bin/bash"]
