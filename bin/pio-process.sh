#!/bin/bash -eu

usage="Usage: pio-process.sh (start|stop)"

if [ $# -lt 1 ]; then
  echo $usage
  exit 1
fi

sh ./bin/node.sh server process $1
