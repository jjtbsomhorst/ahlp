package nl.nc.ahlp.controller;

import java.util.List;

import nl.nc.ahlp.impl.LogEntry;

public class UpdateResult {
	private boolean filtered = false;
	private List<LogEntry> entries = null;
	private int total = 0;
	
	public UpdateResult(boolean filtered, List<LogEntry> entries, int total) {
		this.filtered = filtered;
		this.entries = entries;
		this.total = total;
	}
	
	public boolean isEmpty(){
		return this.entries.isEmpty();
	}
	public int size(){
		return this.entries.size();
	}
	
	public boolean isFiltered() {
		return filtered;
	}
	
	public List<LogEntry> getEntries() {
		return this.entries;
	}
	
	public int getTotalEntries() {
		return total;
	}
}