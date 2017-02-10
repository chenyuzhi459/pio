#!/bin/bash -eu

usage="Usage: pio-server.sh <broker|overlord|middleManager|process> (start|stop)"

if [ $# -lt 2 ]; then
  echo $usage
  exit 1
fi

nodeType=$1
shift

sh ./bin/node.sh server $nodeType $1
