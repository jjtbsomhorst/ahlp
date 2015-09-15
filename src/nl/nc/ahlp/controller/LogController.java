package nl.nc.ahlp.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import nl.nc.ahlp.LogParser;
import nl.nc.ahlp.LogParserListener;
import nl.nc.ahlp.impl.LogEntry;

/**
 * Responsible for loading and filtering.
 * 
 * @author Nino Camdzic
 */
public class LogController extends Observable {
	private static int INTIAL_CAPACITY = 500;
	
	private LogParser parser = null;
	private List<LogEntry> entries = null;
	private List<LogEntry> filtered = null;
	
	public LogController(LogParser parser) {
		this.parser = parser;
		this.entries = new ArrayList<LogEntry>(INTIAL_CAPACITY);
	}
	
	/**
	 * Load a single access log.
	 * 
	 * @param filePath
	 * @return
	 */
	protected List<LogEntry> loadLog(String filePath) throws IOException {
		final List<LogEntry> entries = new ArrayList<LogEntry>();
		FileReader reader = new FileReader(filePath);
		
		parser.parse(reader, new LogParserListener() {
			public void update(LogEntry request) {
				entries.add(request);
			}
		});
		
		return entries;
	}
	
	/**
	 * Load the logs.
	 * 
	 * @param dirPath
	 * @return
	 */
	public void loadLogs(final File[] logFiles) throws IOException {
		filtered= null;
		this.entries.clear();
		
		for(File f : logFiles) {
			if(f.isFile()) {
				entries.addAll(LogController.this.loadLog(f.getAbsolutePath()));
			}
		}
		Collections.sort(entries);
		
		filtered = entries;
		
		LogController.this.setChanged();
		LogController.this.notifyObservers(new UpdateResult(false, filtered, entries.size()));
	}
	
	/**
	 * Apply the specified filter.
	 * 
	 * @param field The field which will be searched.
	 * @param value The value which to search for.
	 * @TODO Fix filter on logentry objects..
	 */
	public void filter(final String field, final String value, boolean regExpr) throws LogControllerException {
			List<LogEntry> filtered = new ArrayList<LogEntry>(10);
			Pattern p = null;
			
			if(regExpr) {
				try {
					p = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
				} catch(PatternSyntaxException e) {
					throw new LogControllerException("Failed to parse regular expression.", e);
				}
				
			}
			
//			for(LogEntry entry : LogController.this.filtered) {
//				if(p != null) {
//					Matcher m = p.matcher(entry.get(field));
//					
//					if(m.find()) {
//						filtered.add(entry);
//					}
//				} else {
//					if(entry.get(field).equals(value)) {
//						filtered.add(entry);
//					}
//				}
//			}
			
//			LogController.this.filtered = filtered;
//			UpdateResult result = new UpdateResult(true, filtered, entries.size());
//			
//			LogController.this.setChanged();
//			LogController.this.notifyObservers(new UpdateResult(true,filtered,entries);
	}
	
	/**
	 * Clear the current filter.
	 */
	public void clearFilter() {
		filtered = entries;
		UpdateResult result = new UpdateResult(false, filtered, entries.size());
		
		this.setChanged();
		this.notifyObservers(result);
	}
	
	/**
	 * Export the filtered entries to CSV.
	 * 
	 * @param file
	 * @return
	 */
	public boolean exportCsv(File file) throws IOException {
		boolean result = false;
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(file);
			String[] fields = parser.getFields();
			
			// First line are columns.
			for(int i = 0; i < fields.length; i++) {
				out.write(fields[i]);
				
				if(fields.length - i > 1) {
					out.print(";");
				} else {
					out.println();
				}
			}
			
			// The rest are the filtered entries.
//			for(Map<LogEntry> map : filtered) {
//				for(int i = 0; i < fields.length; i++) {
//					out.write(map.get(fields[i]));
//					
//					if(fields.length - i > 1) {
//						out.print(";");
//					} else {
//						out.println();
//					}
//				}
//			}
//			
			result = true;
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
		
		return result;
	}
}
