package nl.nc.ahlp.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import nl.nc.ahlp.LogParser;
import nl.nc.ahlp.LogParserListener;

/**
 * Responsible for loading and filtering.
 * 
 * @author Nino Camdzic
 */
public class LogController extends Observable {
	private static int INTIAL_CAPACITY = 50000;
	
	private LogParser parser = null;
	private List<Map<String, String>> entries = null;
	private List<Map<String, String>> filtered = null;
	
	public LogController(LogParser parser) {
		this.parser = parser;
		entries = new ArrayList<Map<String, String>>(INTIAL_CAPACITY);
	}
	
	/**
	 * Load a single access log.
	 * 
	 * @param filePath
	 * @return
	 */
	protected List<Map<String, String>> loadLog(String filePath) throws IOException {
		final List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		FileReader reader = new FileReader(filePath);
		
		parser.parse(reader, new LogParserListener() {
			public void update(Map<String, String> request) {
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
		entries = filtered = null;
		entries = new ArrayList<Map<String, String>>();
		
		for(File f : logFiles) {
			if(f.isFile()) {
				entries.addAll(LogController.this.loadLog(f.getAbsolutePath()));
			}
		}

		Collections.sort(entries, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> obj1, Map<String, String> obj2) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
				Date date1 = null;
				Date date2 = null;
				
				try {
					date1 = format.parse(obj1.get("Date"));
					date2 = format.parse(obj2.get("Date"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// Newest to oldest.
				return date2.compareTo(date1);
			}
			
		});
		
		filtered = entries;
		UpdateResult result = new UpdateResult(false, filtered, entries.size());
		
		LogController.this.setChanged();
		LogController.this.notifyObservers(result);
	}
	
	/**
	 * Apply the specified filter.
	 * 
	 * @param field The field which will be searched.
	 * @param value The value which to search for.
	 */
	public void filter(final String field, final String value, boolean regExpr) throws LogControllerException {
			List<Map<String, String>> filtered = new ArrayList<Map<String, String>>();
			Pattern p = null;
			
			if(regExpr) {
				try {
					p = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
				} catch(PatternSyntaxException e) {
					throw new LogControllerException("Failed to parse regular expression.", e);
				}
				
			}
			
			for(Map<String, String> entry : LogController.this.filtered) {
				if(p != null) {
					Matcher m = p.matcher(entry.get(field));
					
					if(m.find()) {
						filtered.add(entry);
					}
				} else {
					if(entry.get(field).equals(value)) {
						filtered.add(entry);
					}
				}
			}
			
			LogController.this.filtered = filtered;
			UpdateResult result = new UpdateResult(true, filtered, entries.size());
			
			LogController.this.setChanged();
			LogController.this.notifyObservers(result);
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
			for(Map<String, String> map : filtered) {
				for(int i = 0; i < fields.length; i++) {
					out.write(map.get(fields[i]));
					
					if(fields.length - i > 1) {
						out.print(";");
					} else {
						out.println();
					}
				}
			}
			
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
