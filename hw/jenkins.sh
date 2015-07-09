source /opt/Xilinx/14.7/ISE_DS/settings64.sh
scons \
    build/test/ \
    build/synthesis/slices.txt \
    build/synthesis/timing.txt
