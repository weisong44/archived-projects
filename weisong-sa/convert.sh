#!/bin/sh

function printUsageAndExit() {
  echo
  echo "Usage:"
  echo "    convert.sh <type> <file>"
  echo "Parameter:"
  echo "    type	TS or MC"
  echo "    file	File to be converted"
  echo
  exit
}

if [ $# -ne 2 ]; then
  printUsageAndExit
fi

if [ ! -f $2 ]; then
  echo; echo "ERROR: file $2 not found"
  printUsageAndExit
fi

REPLACE1=

if [ $1 == "TS" ]; then
  REPLACE1="Session(0,"
elif [ $1 == "MC" ]; then
  REPLACE1="S("
else
  echo; echo "ERROR: type must be ether TS or MC"
  printUsageAndExit
fi

cat $2 | \
    sed "s/\${SESSION}/$REPLACE1/g"
    
