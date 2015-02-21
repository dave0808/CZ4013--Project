package data;

public class InvalidMessageException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param s the exception to be thrown.
	 */
	public InvalidMessageException( String s ) {
		
		super(s);
		
	}//end of default Constructor.
}
