package nl.nc.ahlp.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.nc.ahlp.LogParser;
import nl.nc.ahlp.LogParserListener;

/**
 * Apache HTTPD accesslog parser.
 * 
 * @author Nino Camdzic
 */
public class ApacheHttpdAccessLogParser extends LogParser {
	// Non Java version regular expr: (\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}).*(\[.*\])\s\"(\w+)\s(.*)\"\s(\d{3})      
	private final String REG_EXPR = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*(\\[.*\\])\\s\\\"(\\w+)\\s(.*)\\s(.*)\\\"\\s(\\d{3})";
	private Pattern p = Pattern.compile(REG_EXPR);
	public String getDescription() {
		return "Apache HTTPD Access Log";
	}
	
	public String[] getFields() {
		return new String[]{"Hostname", "Date", "Method", "Request", "Protocol", "Response"};
	}
	
	public void parse(Reader reader, LogParserListener listener) {
		BufferedReader in = null;
		
		if(reader instanceof BufferedReader) {
			in = (BufferedReader) reader;
		} else {
			in = new BufferedReader(reader);
		}
		
		try {
			String line = in.readLine();
			
			
			while(line != null) {
				Matcher m = p.matcher(line.trim());
				if(m.find()) {
					Map<String, String> entry = new HashMap<String, String>();
					entry.put("Hostname", m.group(1));
					String dateString = m.group(2);
					dateString = dateString.substring(1, dateString.length() - 1);
					entry.put("Date", dateString);
					entry.put("Method", m.group(3));
					entry.put("Request", m.group(4));
					entry.put("Protocol", m.group(5));
					entry.put("Response", m.group(6));
					
					listener.update(entry);
				}
			
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
