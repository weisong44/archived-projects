#!/bin/sh

cd `dirname $0`/..
mvn clean install

cd target
mkdir -p portfolio/target
cp -rf ../bin portfolio/
cp -rf ../data portfolio/
cp -rf ../config portfolio/
cp ../target/*-jar-with-dependencies.jar portfolio/target/
tar cvfz portfolio.tar.gz portfolio

