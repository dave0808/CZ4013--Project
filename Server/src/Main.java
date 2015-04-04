
public class Main {

	public static void main(String[] args) {
		
		/*
		 * Variable storing the type of invocation semantics
		 * False for at least once
		 * True for at most once
		 */
		final boolean invocSemantic = true;
		
		/* Probability that server will not receive a packet
		 * This should be a number between 0 (no failure) and 1 (Always fails)
		 * Including 0 and 1.
		 */
		final float failProb = 0;
		
		Server serve = new Server(invocSemantic, failProb);

	}

}
