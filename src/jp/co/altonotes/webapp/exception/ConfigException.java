package jp.co.altonotes.webapp.exception;

/**
 * �A�v���P�[�V�����N�����̃R���t�B�O�ɂ��ݒ�G���[
 *
 * @author Yamamoto Keita
 *
 */
public class ConfigException extends RuntimeException{
	private static final long serialVersionUID = 7875714318101991278L;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
	}
}
