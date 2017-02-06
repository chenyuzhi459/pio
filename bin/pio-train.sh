#!/bin/bash -eu

usage="Usage: pio-train.sh <trainingSpec.json>"

if [ $# -lt 1 ]; then
  echo $usage
  exit 1
fi

sh ./bin/node.sh train trainer start $1
