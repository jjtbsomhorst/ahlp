package nl.nc.ahlp.controller;

import java.util.List;
import java.util.Map;

public class UpdateResult {
	private boolean filtered = false;
	private List<Map<String, String>> entries = null;
	private int total = 0;
	
	public UpdateResult(boolean filtered, List<Map<String, String>> entries, int total) {
		this.filtered = filtered;
		this.entries = entries;
		this.total = total;
	}
	
	public boolean isFiltered() {
		return filtered;
	}
	
	public List<Map<String, String>> getEntries() {
		return entries;
	}
	
	public int getTotalEntries() {
		return total;
	}
}