package jp.co.altonotes.util.exception;

/**
 * セッターが一意に定まらない場合のException
 * @author Yamamoto Keita
 *
 */
public class AmbiguousSetterException extends RuntimeException {

	private static final long serialVersionUID = -275482441405247318L;

	/**
	 * コンストラクター
	 * @param message
	 */
	public AmbiguousSetterException(String message) {
		super(message);
	}
}
