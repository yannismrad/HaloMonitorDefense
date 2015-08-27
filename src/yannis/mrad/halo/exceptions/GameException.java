package yannis.mrad.halo.exceptions;

/**
 * Class GameException
 * @author Yannis
 * 
 * Exception GameException
 *
 */
public class GameException extends Exception{
	private String message;
	

	/**
	 * Constructeur de GameException
	 * @param message
	 */
	public GameException(String message) {
		super(message);
	}
	

}
