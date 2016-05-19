#!/usr/bin/python3

# Search for the first seed where the host machine and TurboRav
# compute a different checksum.

from subprocess import call
import os
import argparse

def main():
    parser = argparse.ArgumentParser(description='Search for bugs with Csmith')
    parser.add_argument(
        '--start-seed',
        default = 1
    )

    args = parser.parse_args()

    # This script assumes it is located and run from src/test/csmith
    os.chdir("../../../build/test/csmith/")
    for seed in range(int(args.start_seed), 10000):
        scons_cmd = [
            "scons", "-u",
            "csmith_seed={}".format(seed)
        ]

        return_code = call( scons_cmd + ["{}/host_stdout.txt".format(seed)] )
        if return_code:
            print("Running on the host machine failed, so this seed is unusable")
            continue

        return_code = call( scons_cmd + ["{}".format(seed)] )
        assert(return_code == 0)

        os.chdir(str(seed))

        with open('stdout.comm.txt', 'r') as f:
            turborav_result = f.read()
            assert(turborav_result == "")

        print("bug_searcher.py: The checksums matched!")

        os.chdir("..")

if __name__ == "__main__":
    main()
