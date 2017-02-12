#!/bin/bash -eu

usage="Usage: pio-train.sh <deploymentSpec.json>"

if [ $# -lt 1 ]; then
  echo $usage
  exit 1
fi

sh ./bin/node.sh engine deployer start $1
