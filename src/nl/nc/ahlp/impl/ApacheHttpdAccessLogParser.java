package nl.nc.ahlp.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
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
	private static final String KEY_RESPONSE = "Response";
	private static final String KEY_PROTOCOL = "Protocol";
	private static final String KEYREQUEST = "Request";
	private static final String KEYMETHOD = "Method";
	private static final String KEY_DATE = "Date";
	private static final String KEY_HOSTNAME = "Hostname";    
	private static final String REG_EXPR = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*(\\[.*\\])\\s\\\"(\\w+)\\s(.*)\\s(.*)\\\"\\s(\\d{3})";
	private static final Pattern p = Pattern.compile(REG_EXPR);
	private static final String[] fields = new String[]{KEY_HOSTNAME, KEY_DATE, KEYMETHOD, KEYREQUEST, KEY_PROTOCOL, KEY_RESPONSE};
	public String getDescription() {
		return "Apache HTTPD Access Log";
	}
	
	public String[] getFields() {
		return fields;
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
			
			Matcher m = null;
			while(line != null) {
				 m = p.matcher(line.trim());
				 if(m.find()) {
					LogEntry entry = new LogEntry();
					entry.setHostName(m.group(1));
					try {
						entry.setDate(m.group(2));
					} catch (ParseException e) {
						System.out.println("Error parsing date "+m.group(2));
						e.printStackTrace();
					}
					
					entry.setMethod(m.group(3));
					entry.setRequest(m.group(4));
					entry.setProtocol(m.group(5));
					entry.setResponse(m.group(6));
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