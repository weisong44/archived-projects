#!/bin/bash

for f in *.txt; do 
	echo $f; 
	echo "Date,Equity,Open,Gap,TrueRange,Trades" > tmp/$f.csv; 
	cat $f \
		| sed 's/ /,/g' \
		| sed 's|/| |g' \
		| awk '{ print $2 "/" $1 "/" $3 }' \
		>> tmp/$f.csv; 
done
