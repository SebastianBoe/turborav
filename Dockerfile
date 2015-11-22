FROM pritunl/archlinux

RUN pacman --noconfirm -S \
    base-devel \
    git \
    java-environment \
    scala \
    scons \
    java-commons-io \
    python-pint \
    clang \
    sbt \
    && \
    pacman -Scc # Clean pacman cache before committing

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

# Install chisel from the AUR
WORKDIR /home/turbo
RUN git clone https://aur.archlinux.org/chisel.git
WORKDIR /home/turbo/chisel
RUN makepkg
USER root
RUN pacman -U --noconfirm chisel*pkg*

# Assume user is going to be mounting his local repo at /mnt/turborav
WORKDIR /mnt/turborav/hw

USER root

CMD ["/bin/bash"]
