package jp.co.altonotes.webapp.exception;

/**
 * �N��������Exception
 *
 * @author Yamamoto Keita
 *
 */
public class StartupException extends RuntimeException {
	private static final long serialVersionUID = -6386783271576443286L;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param message
	 */
	public StartupException(String message) {
		super(message);
	}

}
