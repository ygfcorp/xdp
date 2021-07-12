#!/bin/sh

docker build -t 175329446102.dkr.ecr.ap-northeast-2.amazonaws.com/rptf-base:$1 .

docker push 175329446102.dkr.ecr.ap-northeast-2.amazonaws.com/rptf-base:$1
