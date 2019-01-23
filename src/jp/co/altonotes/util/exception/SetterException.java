package jp.co.altonotes.util.exception;

/**
 * Setterに関するException
 *
 * @author Yamamoto Keita
 *
 */
public class SetterException extends RuntimeException {

	private static final long serialVersionUID = 4527271851841506804L;

	/**
	 * コンストラクター
	 * @param message
	 */
	public SetterException(String message) {
		super(message);
	}

}
