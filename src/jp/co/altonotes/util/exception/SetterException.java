package jp.co.altonotes.util.exception;

/**
 * Setter�Ɋւ���Exception
 *
 * @author Yamamoto Keita
 *
 */
public class SetterException extends RuntimeException {

	private static final long serialVersionUID = 4527271851841506804L;

	/**
	 * �R���X�g���N�^�[
	 * @param message
	 */
	public SetterException(String message) {
		super(message);
	}

}
