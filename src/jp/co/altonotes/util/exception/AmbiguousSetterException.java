package jp.co.altonotes.util.exception;

/**
 * �Z�b�^�[����ӂɒ�܂�Ȃ��ꍇ��Exception
 * @author Yamamoto Keita
 *
 */
public class AmbiguousSetterException extends RuntimeException {

	private static final long serialVersionUID = -275482441405247318L;

	/**
	 * �R���X�g���N�^�[
	 * @param message
	 */
	public AmbiguousSetterException(String message) {
		super(message);
	}
}
