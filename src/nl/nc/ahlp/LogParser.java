package nl.nc.ahlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nl.nc.ahlp.impl.LogEntry;

/**
 * Abstract log parser.
 * 
 * @author Nino Camdzic
 */
public abstract class LogParser {
	private static List<LogParser> instances = null;
	private File source = null;
	private long lineCount = -1;
	
	public void setFile(File f){
		this.source = f;
	}
	
	public abstract String getDescription();
	public abstract String[] getFields();
		
	protected BufferedReader getReader() throws FileNotFoundException{
		return new BufferedReader(new FileReader(source));
	}
	
	public long getLineCount(){
		BufferedReader reader = null;
		if(this.lineCount < 0){
			try {
				reader = this.getReader();
				while(reader.readLine() != null){
					lineCount++;
				}
			} catch (IOException e) {
				lineCount = 0;
			} finally{
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this.lineCount;
	}
		
	public abstract LogEntry parseLine(String s);
	
	private static void initParsers() throws ObtainLogParserException {
		if(instances == null || instances.size() == 0) {
			instances = new ArrayList<LogParser>();
			InputStream input = LogParser.class.getClassLoader().getResourceAsStream("META-INF/service/" + LogParser.class.getName());

			if(input != null) {
				BufferedReader reader = null;
				
				try {
					reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
					String className = reader.readLine();
					
					while(className != null) {
						Class<?> clazz = LogParser.class.getClassLoader().loadClass(className);
						LogParser parser = (LogParser) clazz.newInstance();
						
						instances.add(parser);
						
						className = reader.readLine();
					}
				} catch (UnsupportedEncodingException e) {
					throw new ObtainLogParserException(e);
				} catch (IOException e) {
					throw new ObtainLogParserException(e);
				} catch (ClassNotFoundException e) {
					throw new ObtainLogParserException(e);
				} catch (InstantiationException e) {
					throw new ObtainLogParserException(e);
				} catch (IllegalAccessException e) {
					throw new ObtainLogParserException(e);
				} finally {
					if(reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static List<LogParser> getInstances() throws ObtainLogParserException {
		initParsers();
		
		return instances;
	}

	public abstract LogEntry parseLine(int lineNumber);
}