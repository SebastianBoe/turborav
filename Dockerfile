FROM pritunl/archlinux

RUN pacman --noconfirm -S \
    git \
    scala \
    scons \
    java-commons-io \
    clang \
    python-pint \
    && \
    pacman -Scc # Clean pacman cache before committing
RUN pacman -S --noconfirm --needed base-devel

# Install the RISC-V toolchain from github and build from source

# Using the latest RISC-V toolchain causes a compilation error when
# building the RISC-V tests, but this revision is known to work. TODO:
# debug compilation error.
ENV TOOLCHAIN_REVISION f0addb7
RUN git clone https://github.com/riscv/riscv-gnu-toolchain.git
RUN pushd riscv-gnu-toolchain \
	&& git checkout $TOOLCHAIN_REVISION \
	&& ./configure --prefix=/home/docker/riscv \
	&& make -j8 \
	&& popd \
	&& rm -rf riscv-gnu-toolchain/
ENV PATH $PATH:/home/docker/riscv/bin

# Install scalastyle from the AUR
RUN git clone https://aur.archlinux.org/scalastyle.git \
    && pushd scalastyle \
    && makepkg -sri \
    && popd \
    && rm -rf scalastyle

ENV CHISEL_VERSION 2.2.30
ENV CHISEL_JAR chisel_2.11-$CHISEL_VERSION.jar
RUN curl http://central.maven.org/maven2/edu/berkeley/cs/chisel_2.11/$CHISEL_VERSION/$CHISEL_JAR \
    && install -Dm644 $CHISEL_JAR /usr/share/scala/chisel/chisel.jar \
    && rm $CHISEL_JAR

CMD ["/bin/bash"]
