package jp.co.altonotes.webapp.exception;

/**
 * �d������Condition��o�^�����ꍇ�ɔ�������Exception�B
 *
 * @author Yamamoto Keita
 *
 */
public class DuplicateConditionException extends Exception {

	private static final long serialVersionUID = -3080706001808695828L;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param message
	 */
	public DuplicateConditionException(String message) {
		super(message);
	}
}
