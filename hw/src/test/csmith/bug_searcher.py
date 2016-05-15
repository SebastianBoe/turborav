#!/usr/bin/python3

# Search for the first seed where the host machine and TurboRav
# compute a different checksum.

from subprocess import call
import os

def main():
    # This script assumes it is located and run from src/test/csmith
    os.chdir("../../../build/test/csmith/")
    for seed in range(10000):
        return_code = call(
            [
                "scons", "-u",
                "csmith_seed={}".format(seed),
                str(seed)
            ]
        )
        assert(return_code == 0)

        os.chdir(str(seed))

        with open('stdout.comm.txt', 'r') as f:
            turborav_result = f.read()
            assert(turborav_result == "")

        print("bug_searcher.py: The checksums matched!")

        os.chdir("..")

if __name__ == "__main__":
    main()
