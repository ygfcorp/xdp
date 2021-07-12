#!/usr/bin/env bash

./gradlew :flow-server:clean :flow-server:shadowJar -x test

docker build -t 175329446102.dkr.ecr.ap-northeast-2.amazonaws.com/flow-server:$1 .

docker push 175329446102.dkr.ecr.ap-northeast-2.amazonaws.com/flow-server:$1