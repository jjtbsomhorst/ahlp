package nl.nc.ahlp.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LogEntry extends HashMap<String, Object> implements Comparable<LogEntry>{

	private static final long serialVersionUID = 1L;
	private static final String KEY_RESPONSE = "Response";
	private static final String KEY_PROTOCOL = "Protocol";
	private static final String KEYREQUEST = "Request";
	private static final String KEYMETHOD = "Method";
	private static final String KEY_DATE = "Date";
	private static final String KEY_HOSTNAME = "Hostname";    
	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
	
	public void setResponse(String value){
		this.put(KEY_RESPONSE,value);
	}
	public void setProtocol(String value){
		this.put(KEY_PROTOCOL,value);
	}
	public void setRequest(String value){
		this.put(KEYREQUEST,value);
	}
	public void setMethod(String value){
		this.put(KEYMETHOD,value);
	}
	public void setDate(String value) throws ParseException{		
		this.put(KEY_DATE,format.parse(value.substring(1,value.length()-1)));
	}
	
	public String getDateAsString(){
		return format.format((Date)  this.get(KEY_DATE));
	}
	
	public void setHostName(String value){
		this.put(KEY_HOSTNAME,value);
	}
	
	public String getReponse(){
		return (String) super.get(KEY_RESPONSE);
	}
	public String getProtocol(){
		return (String) super.get(KEY_PROTOCOL);
	}
	public String getRequest(){
		return (String) super.get(KEYREQUEST);
	}
	public String getMethod(){
		return (String) super.get(KEYMETHOD);
	}
	public Date getDate(){
		return (Date) super.get(KEY_DATE);
	}
	public String getHostName(){
		return (String) super.get(KEY_HOSTNAME);
	}
	
	@Override
	public int compareTo(LogEntry o) {
		return o.getDate().compareTo(this.getDate());
	}
}
