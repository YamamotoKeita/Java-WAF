package jp.co.altonotes.util.exception;

/**
 * Getter�Ɋւ���Exception
 *
 * @author Yamamoto Keita
 *
 */
public class GetterException extends RuntimeException {

	private static final long serialVersionUID = 1684999549176051785L;

	/**
	 * �R���X�g���N�^�[
	 * @param message
	 */
	public GetterException(String message) {
		super(message);
	}

}
