package com.weisong.trading.portfolio.strategy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultSystemChooser implements SystemChooser {
	
	final static public String KEY_SYSTEM_CHOSEN = "system.chosen";
	
	protected Set<String> chosenSystemsIds = new HashSet<>();
	
	public DefaultSystemChooser(Map<String, String> config) {
		
		String chosen = config.get(KEY_SYSTEM_CHOSEN);
		if(chosen != null) {
			for(String name : chosen.split(",")) {
				chosenSystemsIds.add(name.trim().toUpperCase());
			}
		}
	}
	
	@Override
	public boolean chooseFileName(String fname) {
		String systemId = fname
			.substring(fname.lastIndexOf(File.separator) + 1, fname.length() - 1)
			.substring(0, 4)
			.toUpperCase();
		return chooseSystemId(systemId);
	}
	
	@Override
	public boolean chooseSystemId(String systemId) {
		if(systemId == null) {
			return false;
		}
		if(chosenSystemsIds.isEmpty()) {
			return true;
		}
		return chosenSystemsIds.contains(systemId.toUpperCase());
	}

	static public class Builder {
		public DefaultSystemChooser create(Map<String, String> props) {
			return new DefaultSystemChooser(props);
		}

		public DefaultSystemChooser createDefault() {
			Map<String, String> config = new HashMap<>();
			config.put(KEY_SYSTEM_CHOSEN, "S991,S992");
			return new DefaultSystemChooser(config);
		}
	}
}
