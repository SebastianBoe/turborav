#!/bin/bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker run \
       -v $DIR:/mnt/turborav \
       -it sebomux/turborav \
       "$@"
