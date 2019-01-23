package jp.co.altonotes.util.exception;

/**
 * Getterに関するException
 *
 * @author Yamamoto Keita
 *
 */
public class GetterException extends RuntimeException {

	private static final long serialVersionUID = 1684999549176051785L;

	/**
	 * コンストラクター
	 * @param message
	 */
	public GetterException(String message) {
		super(message);
	}

}
