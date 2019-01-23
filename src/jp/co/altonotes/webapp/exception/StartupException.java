package jp.co.altonotes.webapp.exception;

/**
 * 起動処理のException
 *
 * @author Yamamoto Keita
 *
 */
public class StartupException extends RuntimeException {
	private static final long serialVersionUID = -6386783271576443286L;

	/**
	 * コンストラクター
	 *
	 * @param message
	 */
	public StartupException(String message) {
		super(message);
	}

}
