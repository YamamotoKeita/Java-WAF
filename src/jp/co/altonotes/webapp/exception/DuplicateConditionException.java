package jp.co.altonotes.webapp.exception;

/**
 * 重複したConditionを登録した場合に発生するException。
 *
 * @author Yamamoto Keita
 *
 */
public class DuplicateConditionException extends Exception {

	private static final long serialVersionUID = -3080706001808695828L;

	/**
	 * コンストラクター
	 *
	 * @param message
	 */
	public DuplicateConditionException(String message) {
		super(message);
	}
}
