package jp.co.altonotes.webapp.exception;

/**
 * Component�쐬����Exception
 *
 * @author Yamamoto Keita
 *
 */
public class ComponentException extends RuntimeException {

	private static final long serialVersionUID = -4617549641549021234L;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param msg
	 */
	public ComponentException(String msg) {
		super(msg);
	}
}
