FROM l3iggs/archlinux-aur

# Prefer to use the server at Samfundet, Trondheim.
RUN sudo sed -i '1s/^/Server = http:\/\/mirror.archlinux.no\/$repo\/os\/$arch /' /etc/pacman.d/mirrorlist

# Arch does not support "partial upgrades" so we must install every
# Arch package in the same docker RUN command.
RUN yaourt --noconfirm -Syua \
    scala \
    scons \
    chisel \
    jdk \
    java-commons-io \
    clang \
    python-pint \
    scalastyle

# Install the RISC-V toolchain from github and build from source
RUN git clone https://github.com/riscv/riscv-gnu-toolchain.git
RUN cd riscv-gnu-toolchain && git checkout f0addb7 && ./configure --prefix=/opt/riscv && sudo make -j8
ENV PATH $PATH:/opt/riscv/bin

CMD riscv64-unknown-elf-gcc --help
