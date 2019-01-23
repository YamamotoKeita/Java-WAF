package jp.co.altonotes.webapp.exception;

/**
 * Component作成時のException
 *
 * @author Yamamoto Keita
 *
 */
public class ComponentException extends RuntimeException {

	private static final long serialVersionUID = -4617549641549021234L;

	/**
	 * コンストラクター
	 *
	 * @param msg
	 */
	public ComponentException(String msg) {
		super(msg);
	}
}
