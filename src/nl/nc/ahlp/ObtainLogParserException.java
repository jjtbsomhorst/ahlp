package nl.nc.ahlp;

public class ObtainLogParserException extends Exception {
	private static final long serialVersionUID = 1L;

	public ObtainLogParserException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ObtainLogParserException(String message) {
		super(message, null);
	}
	
	public ObtainLogParserException(Throwable cause) {
		super(cause);
	}
}