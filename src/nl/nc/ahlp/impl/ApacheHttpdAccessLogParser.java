package nl.nc.ahlp.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
	private static final String DESCRIPTION_APACHE_HTTPD_ACCESS_LOG = "Apache HTTPD Access Log";
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
		return DESCRIPTION_APACHE_HTTPD_ACCESS_LOG;
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
	
	@Override
	public LogEntry parseLine(String line) {
		Matcher m = p.matcher(line.trim());
		LogEntry entry = null;
		if(!m.find()){
			System.out.println("Could not match data with string");
			return null;
		}
		
			entry = new LogEntry();
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
		return entry;
	}

	@Override
	public LogEntry parseLine(int lineNumber) {
		BufferedReader reader = null;
		LogEntry line = null;
		try{
			reader = this.getReader();
			for(int i = 0;i<lineNumber;i++){
				reader.readLine();
			}
			
			line =  this.parseLine(reader.readLine());
		}catch(IOException e){
			
		}finally{
			try{
				reader.close();
			}catch(IOException ioe){
				System.out.println("Could not clean up reader");
			}
		}
		return line;
		
	}
}