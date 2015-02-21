package data;

public class InvalidIDException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param s the exception to be thrown.
	 */
	public InvalidIDException( String s ) {
		
		super(s);
		
	}//end of default Constructor.
}
