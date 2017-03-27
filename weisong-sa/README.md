# Description

This repository contains 

1. Code from "Trading Systems Supremacy" course. 
   More information can be found at http://skilledacademy.com/courses/
2. Various tools for testing

All code are compatible with TradeStation 9.5 and MultiCharts 10.

There is a a few files that are written in template (*.template.txt) form and needs to be converted. The script "convert.sh" can be used to produce the correct version for either TradeStation or MultiCharts. For convenience, "convert-all.sh" would convert all template files in one shot (*.template.txt).

# File list

|Name				|Source|Type		|Description|
|-------------------------------|------|----------------|-----------|
|_OhlcMulti5.txt 		|SA    |Function 	| fast version to calculate session OHLC |
|_TestOhlcMulti5.template.txt	|Wei   |Indicator	| test code for _OhlcMulti5() |
|_AdxOnArray.txt        	|SA    |Function	| ADX indicator (fast version) |
|PtnBaseSA.template.txt      	|SA    |Function	| SA patterns  |
|PtnBaseSAFast.txt           	|SA    |Function	| SA patterns (fast version) |
|PtnBaseSAPair.txt           	|Wei   |Function	| get pattern pairs based on input number |
|PtnBaseAU.txt			|SA    |Function	| extended SA patterns |
|PtnBaseAUFast.txt      	|SA    |Function	| extended SA patterns (fast version) |
|PtnBaseNeutralFast.txt 	|SA    |Function	| neutral SA patterns (fast version) |
|WSTestExit.template.txt	|Wei   |Strategy	| test various exit types |
|convert.sh			|Wei   |Script		| conversion to TS or MC |
|convert-all.sh			|Wei   |Script		| conversion all TS/MC template files in one shot |
|mk-template.sh			|Wei   |Script		| create template file from an actual TS/MC file|
|TS/WSMultiSymbolOptimization.txt|Wei|TradingApp	| Run a strategy on multiple symbols with various parameters|
|TS/WSRollingPatternOptimization.txt|Wei|TradingApp	| Run a strategy on multiple time periods with various parameters|
|TS/*				|Wei   |Directory	| converted files for TS|
|MC/*				|Wei   |Directory	| converted files for MC|

Wei Song (weisong44@hotmail.com)
03/24/2017
