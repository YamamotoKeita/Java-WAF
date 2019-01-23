package jp.co.altonotes.webapp;

/**
 * View�̕\�����@�B<br>
 *
 * <pre>
 * Navigator.REDIRECT + "/path"
 * Navigator.CROSS_APP_FORWARD + "context://path/cvc"
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public final class ViewType {

	/** �R���e�L�X�g�p�X����̑��΃p�X�ɂ�郊�_�C���N�g */
	public static final String REDIRECT = "redirect > ";

	/** ��΃p�X�ɂ�郊�_�C���N�g */
	public static final String URL_REDIRECT = "absolute-redirect > ";

	/** �ʃR���e�L�X�g�p�X�ւ̃t�H���[�h */
	public static final String FORWARD_TO_OTHER_CONTEXT = "other-context > ";

	/**
	 * �ʃR���e�L�X�g�p�X�ւ̃t�H���[�h���w������View�̃L�[�B
	 * 
	 * @param contextPath
	 * @param path
	 * @return �ʃR���e�L�X�g�p�X�ւ̃t�H���[�h���w�����镶����
	 */
	public static String forwardToOtherContext(String contextPath, String path) {
		return FORWARD_TO_OTHER_CONTEXT + contextPath + " > " + path;
	}
}
