#!/bin/sh

function printUsageAndExit() {
  echo
  echo "Usage:"
  echo "    mk-template.sh <TS/MC file>"
  echo "Parameter:"
  echo "    file	Source file to be converted to a template"
  echo
  exit
}

if [ $# -ne 1 ]; then
  printUsageAndExit
fi

if [ ! -f $1 ]; then
  echo; echo "ERROR: file $1 not found"
  printUsageAndExit
fi

TYPE=
grep "Session(0," $1 > /dev/null
if [ $? -eq 0 ]; then
  TYPE="TS"
else 
  grep -Ei "opens\(|highs\(|lows\(|closes\(" $1 > /dev/null
  if [ $? -eq 0 ]; then
    TYPE="MC"
  fi
fi

if [ $TYPE == "TS" ]; then
  cat $1 \
    | sed  's/OpenSession(0,//g' \
    | sed  's/HighSession(0,//g' \
    | sed   's/LowSession(0,//g' \
    | sed 's/CloseSession(0,//g' \
    | grep -Ei "opensession\(|highsession\(|lowsession\(|closesession\(" \
    > /dev/null
    if [ $? -eq 0 ]; then
      echo "ERROR: OpenSession(), HighSession(), LowSession(), CloseSession() are case sensitive, and must be written exactly like this."
      exit
    fi

  cat $1 \
    | sed  's/OpenSession(0,/${SESSION}/g' \
    | sed  's/HighSession(0,/${SESSION}/g' \
    | sed   's/LowSession(0,/${SESSION}/g' \
    | sed 's/CloseSession(0,/${SESSION}/g'
elif [ $TYPE == "MC" ]; then
  cat $1 \
    | sed  's/OpenS(//g' \
    | sed  's/HighS(//g' \
    | sed   's/LowS(//g' \
    | sed 's/CloseS(//g' \
    | grep -Ei "opens\(|highs\(|lows\(|closes\(" 
#    > /dev/null
    if [ $? -eq 0 ]; then
      echo "ERROR: OpenS(), HighS(), LowS(), CloseS() are case sensitive, and must be written exactly like this."
      exit
    fi

  cat $1 \
    | sed  's/OpenS(/${SESSION}/g' \
    | sed  's/HighS(/${SESSION}/g' \
    | sed  's/LowS(/${SESSION}/g' \
    | sed  's/CloseS(/${SESSION}/g'
else
  cat $1
fi

