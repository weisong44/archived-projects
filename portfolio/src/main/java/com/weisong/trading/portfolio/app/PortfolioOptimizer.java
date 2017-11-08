package com.weisong.trading.portfolio.app;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.chart.ChartWindow;
import com.weisong.trading.portfolio.generator.Generator;
import com.weisong.trading.portfolio.model.Portfolio;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationProfile;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;
import com.weisong.trading.portfolio.model.PortfolioEquity;
import com.weisong.trading.portfolio.strategy.AllPassSystemFilter;
import com.weisong.trading.portfolio.strategy.DefaultSystemChooser;
import com.weisong.trading.portfolio.strategy.NetProfitSystemFilter;
import com.weisong.trading.portfolio.strategy.NetProfitSystemFilter1;
import com.weisong.trading.portfolio.strategy.NetProfitSystemFilter2;
import com.weisong.trading.portfolio.strategy.NetProfitToMaxDdSystemFilter;
import com.weisong.trading.portfolio.strategy.Processor;
import com.weisong.trading.portfolio.strategy.RebalanceDates;
import com.weisong.trading.portfolio.strategy.ReverseSelectionSystemFilter;
import com.weisong.trading.portfolio.strategy.SFIncubationSystemFilter;
import com.weisong.trading.portfolio.strategy.SystemFilter;
import com.weisong.trading.portfolio.strategy.SystemFilterChain;
import com.weisong.trading.portfolio.util.LogLevel;

public class PortfolioOptimizer {

	static String inputFileOrDir;
	static String configFile;
	static Date begDate;
	static Date endDate;
	static boolean outputEquity;
	static boolean outputSystemSelection;
	static boolean outputSystemDates;
	static boolean disableFiltering;
	static boolean disableSystemIdMatching;
	static boolean displayGui;
	static boolean outputInCsv;
	static boolean printHelpAndExit;
	
    static protected void printErrorAndExit(Exception ex) {
        ex.printStackTrace();
        printErrorAndExit(ex.getMessage());
    }
    
    static protected void printErrorAndExit(String errMsg) {
        System.err.println("\nERROR: " + errMsg + "\n");
        printUsageAndExit();
    }
    
    static protected void printUsageAndExit() {
        String msg = ""
            + "Description:\n"
            + "    Optimize a portfolio of strategies\n"
            + "Usage:\n"
            + "    java PortfolioOptimizer <dir/file> [-beg <date>] [-end <date>] [-c <config-file>] [-oe] [-os] [-csv] [-v] [-vv]\n"
            + "Parameters:\n"
            + "    dir/file  Directory or file that contains daily system statistics\n"
            + "    -beg      Beginning date (MM/DD/YYYY)\n"
            + "    -end      End date (MM/DD/YYYY)\n"
            + "    -c        Config file name\n"
            + "    -oe       Print protfolio equity curve\n"
            + "    -os       Print system selection\n"
            + "    -od       Print system date ranges\n"
            + "    -nf       Disable equity curve filtering\n"
            + "    -no       Disable optimization all together\n"
            + "    -np       Do not use system Id pattern S[0-9]{3}-(TS|MC).csv,\n"
            + "              instead use *.csv found in the directory\n"
            + "    -g        Display in GUI\n"
            + "    -csv      Output in Csv format\n"
            + "    -v        Verbose\n"
            + "    -vv       More verbose\n"
            + "    -h        Print usage information\n";
        System.out.println(msg);
        System.exit(0);
    }


    static protected void processArgs(String[] args) throws Exception {
        for(int i = 0; i < args.length; i++) {
            String a = args[i];
            if("-oe".equalsIgnoreCase(a)) {
                outputEquity = true;
            } else if("-c".equalsIgnoreCase(a)) {
                configFile = args[++i];
            } else if("-beg".equalsIgnoreCase(a)) {
                begDate = Constants.df1.parse(args[++i]);
            } else if("-end".equalsIgnoreCase(a)) {
                endDate = Constants.df1.parse(args[++i]);
            } else if("-os".equalsIgnoreCase(a)) {
                outputSystemSelection = true;
            } else if("-oe".equalsIgnoreCase(a)) {
                outputSystemSelection = true;
            } else if("-od".equalsIgnoreCase(a)) {
                outputSystemDates = true;
            } else if("-nf".equalsIgnoreCase(a)) {
                disableFiltering = true;
            } else if("-no".equalsIgnoreCase(a)) {
                disableFiltering = true;
            } else if("-np".equalsIgnoreCase(a)) {
                disableSystemIdMatching = true;
            } else if("-g".equalsIgnoreCase(a)) {
                displayGui = true;
            } else if("-csv".equalsIgnoreCase(a)) {
                outputInCsv = true;
            } else if("-v".equalsIgnoreCase(a)) {
                Constants.logLevel = LogLevel.INFO;
            } else if("-vv".equalsIgnoreCase(a)) {
                Constants.logLevel = LogLevel.DEBUG;
            } else if("-h".equalsIgnoreCase(a)) {
                printHelpAndExit = true;
            } else {
            	inputFileOrDir = a;
            	File fileOrDir = new File(a);
            	if(fileOrDir.exists() == false) {
            		printErrorAndExit(a + " not found");
            	}
            }
        }
    }
    
    public static int getNumberOfOptions(boolean ... values) {
    	int n = 0;
    	for(boolean b : values) {
    		if(b) ++n;
    	}
    	return n;
    }

    public static void main(String[] args) throws Exception {
    	
        if(args.length < 1) {
            printUsageAndExit();
        }

        processArgs(args);
        
        if(printHelpAndExit) {
        	printUsageAndExit();
        }
        
        if(getNumberOfOptions(outputEquity, outputSystemSelection, outputSystemDates) > 1) {
        	printErrorAndExit("too many output options!");
        }

        if(Constants.logLevel >= LogLevel.INFO) {
			System.out.println("==========================================");
			System.out.println("begDate = " + (begDate != null ? Constants.df1.format(begDate) : "N/A"));
			System.out.println("endDate = " + (endDate != null ? Constants.df1.format(endDate) : "N/A"));
        	System.out.println("inputDirectory = " + inputFileOrDir);
			System.out.println("outputEquity = " + outputEquity);
			System.out.println("outputSystemSelection = " + outputSystemSelection);
			System.out.println("disableFiltering = " + disableFiltering);
			System.out.println("disableSystemIdMatching = " + disableSystemIdMatching);
			System.out.println("displayGui = " + displayGui);
			System.out.println("outputInCsv = " + outputInCsv);
			System.out.println("log level = " + LogLevel.LOG_LEVEL_STR[Constants.logLevel]);
			System.out.println("==========================================");
        }
        
        if(inputFileOrDir == null) {
        	printErrorAndExit("No input directory provided");
        }
        
        Map<String, String> props = null;
        if(configFile != null) {
        	props = Processor.loadConfig(configFile);
        }
        
        DefaultSystemChooser systemChooser = new DefaultSystemChooser(props);
        
		Portfolio portfolio = Portfolio.fromDirOrFile("portfolio", inputFileOrDir, begDate, endDate, 
				disableSystemIdMatching, systemChooser);
        if(getNumberOfOptions(outputEquity, outputSystemSelection, outputSystemDates) <= 0) {
        	return;
        }
        
        RebalanceDates.Type reType = RebalanceDates.Type.valueOf(props.get("rebalance.date.type"));
        RebalanceDates rebalanceDates = new RebalanceDates(reType);
        
        Processor.Builder<?>[] builders = new Processor.Builder<?>[] {
        	new AllPassSystemFilter.Builder()
          ,	new NetProfitSystemFilter1.Builder()
          ,	new NetProfitSystemFilter2.Builder()
          , new ReverseSelectionSystemFilter.Builder()
          , new NetProfitToMaxDdSystemFilter.Builder()
          , new SFIncubationSystemFilter.Builder()
        };
        
        List<SystemFilter> filters = new LinkedList<>();
		if(disableFiltering) {
			filters.add(new AllPassSystemFilter.Builder().createDefault());
		} else {
	        for(Processor.Builder<?> b : builders) {
	        	if(b instanceof SystemFilter.Builder == false) {
	        		continue;
	        	}
        		if(props != null) {
        			SystemFilter filter = (SystemFilter) b.create(props);
        			if(filter != null) {
        				filters.add(filter);
        			}
        		} else {
        			if(b instanceof NetProfitSystemFilter.Builder
    				|| b instanceof ReverseSelectionSystemFilter.Builder
    				|| b instanceof NetProfitToMaxDdSystemFilter.Builder)
        			filters.add((SystemFilter) b.createDefault());
        		}
	        }

			List<String> filterOrder = null;
			if(props != null) {
				SystemFilter.Config c = new SystemFilter.Config(props) {
					@Override public String getName() {
						return null;
					}
				};
				filterOrder = c.getOrder();
			}

			if(filterOrder == null) {
				filterOrder = Arrays.asList(
					"net-profit-1"
				  ,	"net-profit-2"
				  , "profit-to-maxdd"
				  ,	"reverse-selection"
				);
			}
			
			Map<String, SystemFilter> map = filters.stream().collect(Collectors.toMap(
				f -> ((SystemFilter.Config) f.getConfig()).getName(), 
				f -> f));
			filters.clear();
			for(String name : filterOrder) {
				SystemFilter f = map.get(name);
				if(f == null) {
					throw new RuntimeException("No instance for for filter found " + name);
				}
				filters.add(f);
			}
		}
		
		SystemFilterChain filterChain = new SystemFilterChain(
				filters.toArray(new SystemFilter[filters.size()]));

		OptimizationProfile profile = new OptimizationProfile(rebalanceDates, filterChain);
		OptimizationResult result = portfolio.optimize(profile);
		
		Generator generator = new Generator();

		if(outputEquity) {
			PortfolioEquity equity = generator.generatePortfolioEquityCurve(result);
			if(displayGui) {
				new ChartWindow(portfolio.getName(), equity);
			} else {
				String equityStr = outputInCsv ?
					equity.toCsv()
				  : equity.toString();	
				System.out.println(equityStr);
			}
		} else if(outputSystemSelection) {
			if(displayGui) {
				new ChartWindow(portfolio.getName(), result);
			} else {
				String selectionStr = outputInCsv ?
					result.toCsv()
				  : result.toString();	
				System.out.println(selectionStr);
			}
		} else if(outputSystemDates) {
			StringBuffer sb = new StringBuffer();
			sb.append("System\tFrom\tTo\tCount").append("\n");
			portfolio.getSystems().stream()
//				.sorted((o1, o2) -> o1.getSystemId().compareTo(o2.getSystemId()))
				.sorted((o1, o2) -> o1.getFirstDate().compareTo(o2.getFirstDate()))
				.forEach(s -> { sb
					.append(s.getSystemId()).append("\t")
					.append(Constants.df1.format(s.getFirstDate())).append("\t")
					.append(Constants.df1.format(s.getLastDate())).append("\t")
					.append(s.getCount()).append("\n");
				});
			String dateStr = outputInCsv ?
				sb.toString().replace("\t", ",")
			  : sb.toString();	
			System.out.println(dateStr);
		}
    }

}
