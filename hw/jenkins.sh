#!/bin/bash

# http://redsymbol.net/articles/unofficial-bash-strict-mode/
set -euo pipefail
IFS=$'\n\t'

IMAGE=jenkins_$BUILD_NUMBER
docker build \
       --tag=$IMAGE \
       - < ../Dockerfile

docker run \
       -v $WORKSPACE:/mnt/turborav \
       --rm=true \
       $IMAGE \
       scons \
       build/test \
       build/synthesis/Soc.blif
