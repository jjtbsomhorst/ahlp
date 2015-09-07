package nl.nc.ahlp.controller;

public class LogControllerException extends Exception {
	private static final long serialVersionUID = 1L;

	public LogControllerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LogControllerException(Throwable cause) {
		super(cause);
	}
}
