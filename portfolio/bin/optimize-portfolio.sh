#!/bin/sh

BASEDIR=`dirname $0`/..
java -cp $BASEDIR/target/portfolio-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.weisong.trading.portfolio.app.PortfolioOptimizer $*

