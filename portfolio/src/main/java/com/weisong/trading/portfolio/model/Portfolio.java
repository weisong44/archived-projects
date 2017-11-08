package com.weisong.trading.portfolio.model;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.strategy.RebalanceDates;
import com.weisong.trading.portfolio.strategy.SystemChooser;
import com.weisong.trading.portfolio.strategy.SystemFilterChain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Portfolio {

	@Getter 
	@AllArgsConstructor
	static public class OptimizationProfile {
		private RebalanceDates rebalanceDates;
		private SystemFilterChain filterChain;
	}

	@Getter 
	@AllArgsConstructor
	static public class OptimizationResult {

		private Portfolio portfolio;
		private List<SystemSelection> selections;
		
		public String toCsv() {
			return toString().replaceAll("\t", ",");
		}
		
		@Override
		public String toString() {
			Set<String> allMonthsSet = new HashSet<>();
			selections.forEach(ss -> allMonthsSet.addAll(ss.getAlltMonthlySelection().keySet()));
			List<String> allMonthList = new LinkedList<>(allMonthsSet);
			Collections.sort(allMonthList);
			
			Map<String, SystemSelection> sMap = new HashMap<>();
			selections.forEach(s -> {
				String id = s.getSystem().getSystemId();
				sMap.put(id, s);
			});

			List<String> sIds = new LinkedList<>(sMap.keySet());
			Collections.sort(sIds);
			
			StringBuilder sb = new StringBuilder();
			
			// Header
			sb.append("Month\tDate");
			sIds.forEach(id -> sb.append("\t").append(id));
			sb.append("\t").append("Total");
			sb.append("\n");
			
			// Monthly selections
			allMonthList.forEach(yearAndMonth -> {
				sb.append(yearAndMonth);
				for(String id : sIds) {
					int year = Integer.valueOf(yearAndMonth.split("-")[0]);
					int month = Integer.valueOf(yearAndMonth.split("-")[1]);
					YearAndMonth ym = new YearAndMonth(year, month);
					MonthlySelection sel = sMap.get(id).getMonthlySelection(ym);
					if(sel != null) {
						sb.append("\t").append(Constants.df1.format(sel.getSelectionDate()));
						break;
					}
				}
				int total = 0;
				for(String id : sIds) {
					int year = Integer.valueOf(yearAndMonth.split("-")[0]);
					int month = Integer.valueOf(yearAndMonth.split("-")[1]);
					YearAndMonth ym = new YearAndMonth(year, month);
					MonthlySelection sel = sMap.get(id).getMonthlySelection(ym);
					if(sel != null) {
						total += sel.getContracts();
						sb.append("\t").append(sel.getContracts());
					} else {
						sb.append("\t").append(0);
					}
				}
				sb.append("\t").append(total);
				sb.append("\n");
			});
			
			return sb.toString();
		}
	}

	@Getter private String name;
	@Getter private List<SystemStats> systems;
	
	public OptimizationResult optimize(OptimizationProfile profile) throws Exception {
		List<SystemSelection> results = new LinkedList<>();
		for(SystemStats system : systems) {
			SystemSelection selection = new SystemSelection(system); 
			profile.getFilterChain().filter(selection);
			results.add(selection);
		}
		return new OptimizationResult(this, results);
	}
	
	public Map<String, Date> getLastDates() {
		Map<String, Date> map = new HashMap<>();
		systems.forEach(s -> map.put(s.getSystemId(), s.getDailyStats().get(0).getDate()));
		return map;
	}
	
	static public Portfolio fromDirOrFile(String name, String dirOrFile, 
			Date begDate, Date endDate, boolean disableSystemIdMatching,
			SystemChooser chooser) throws Exception {
		String pattern = disableSystemIdMatching ?
				"**/*.csv"
			  :	"**/S[0-9][0-9][0-9]*-{TS,MC}.csv";
		List<String> fnames = new LinkedList<>();
		Path path = Paths.get(dirOrFile);
		PathMatcher matcher = FileSystems.getDefault()
		        .getPathMatcher("glob:" + pattern);
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		    @Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			        throws IOException {
		    	if(matcher.matches(file)) {
			    	fnames.add(file.toString());
		    	}
		        return FileVisitResult.CONTINUE;
		    }
		});

		List<SystemStats> systems = new LinkedList<>();
		for(String f : fnames) {
			if(disableSystemIdMatching || chooser.chooseFileName(f)) {
				systems.add(SystemStats.fromFile(f, begDate, endDate));
			}
		}
		
		return new Portfolio(name, systems);
	}
}
