#!/bin/sh

cd `dirname $0`

for TYPE in "TS" "MC"; do
  echo "Converting for $TYPE"
  for f in `find . -name "*.template.txt"`; do
    f2=`echo $f | sed 's/\.template\./\./g'`
    echo "    $f => $TYPE/$f2"
    ./convert.sh $TYPE $f > $TYPE/$f2
  done
done

