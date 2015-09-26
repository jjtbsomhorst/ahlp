package nl.nc.ahlp.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import nl.nc.ahlp.LogParser;
import nl.nc.ahlp.impl.LogEntry;

/**
 * Responsible for loading and filtering.
 * 
 * @author Nino Camdzic
 */
public class LogController {
	private static int INTIAL_CAPACITY = 500;

	private LogParser parser = null;
	private HashMap<File, Long> files = new HashMap<File, Long>();
	private List<LogEntry> entries = null;

	private ILogControllerListener listener = null;

	private long lineCount;

	public LogController(LogParser parser) {
		this.parser = parser;
		this.entries = new Vector<LogEntry>();
	}

	public synchronized LogEntry readLine(int lineNumber) {
		System.out.println("Loading line "+lineNumber);
//		if (lineNumber < entries.size()-1) {
//			
//			// TODO Add support for multpiple files...
			return parser.parseLine(lineNumber);
//		}

//		return entries.get(lineNumber);
	}

	// /**
	// * Load a single access log.
	// *
	// * @param filePath
	// * @return
	// */
	// protected List<LogEntry> loadLog(String filePath) throws IOException {
	// final List<LogEntry> entries = new ArrayList<LogEntry>();
	// FileReader reader = new FileReader(filePath);
	//
	// parser.parse(reader, new LogParserListener() {
	// public void update(LogEntry request) {
	// entries.add(request);
	// }
	// });
	//
	// return entries;
	// }
	//
	// /**
	// * Load the logs.
	// *
	// * @param dirPath
	// * @return
	// */

	public void loadLogs(List<File> logFiles) throws IOException {
		System.out.println("Load log files");
		this.lineCount = 0;

		for (File f : logFiles) {
			parser.setFile(f);
			this.lineCount += parser.getLineCount();
		}
		if (this.listener != null) {
			this.listener.onLogSizeChange(this.lineCount);
		}
	}

	/**
	 * Apply the specified filter.
	 * 
	 * @param field
	 *            The field which will be searched.
	 * @param value
	 *            The value which to search for.
	 * @TODO Fix filter on logentry objects..
	 */
	public void filter(final String field, final String value, boolean regExpr) throws LogControllerException {
		List<LogEntry> filtered = new ArrayList<LogEntry>(10);
		Pattern p = null;

		if (regExpr) {
			try {
				p = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException e) {
				throw new LogControllerException("Failed to parse regular expression.", e);
			}

		}

		// for(LogEntry entry : LogController.this.filtered) {
		// if(p != null) {
		// Matcher m = p.matcher(entry.get(field));
		//
		// if(m.find()) {
		// filtered.add(entry);
		// }
		// } else {
		// if(entry.get(field).equals(value)) {
		// filtered.add(entry);
		// }
		// }
		// }

		// LogController.this.filtered = filtered;
		// UpdateResult result = new UpdateResult(true, filtered,
		// entries.size());
		//
		// LogController.this.setChanged();
		// LogController.this.notifyObservers(new
		// UpdateResult(true,filtered,entries);
	}

	// /**
	// * Clear the current filter.
	// */
	// public void clearFilter() {
	// filtered = entries;
	// UpdateResult result = new UpdateResult(false, filtered, entries.size());
	// }

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
			for (String f : parser.getFields()) {
				out.write(f);
				out.write(";");
			}

			for (LogEntry e : this.entries) {
				for (String key : e.keySet()) {
					out.write(e.get(key).toString());
					out.write(";");
				}
			}

			result = true;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}

		return result;
	}

	public void setListener(ILogControllerListener l) {
		this.listener = l;
	}

	public long getItemCount() {
		return this.lineCount;
	}
}
