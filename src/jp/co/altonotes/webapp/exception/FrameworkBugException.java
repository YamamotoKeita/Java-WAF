package jp.co.altonotes.webapp.exception;

/**
 * �t���[�����[�N�̃o�O�ɂ���Ĕ�������Exception�B<br>
 * ����Exception���������邱�Ƃ́A�{�������Ă͂Ȃ�Ȃ��B
 * 
 * @author Yamamoto Keita
 */
public class FrameworkBugException extends RuntimeException {

	private static final long serialVersionUID = -3702627282515135027L;
	
	/**
	 * �R���X�g���N�^�[
	 * @param string
	 */
	public FrameworkBugException(String string) {
		super(string);
	}
	
	/**
	 * �R���X�g���N�^�[
	 */
	public FrameworkBugException() {}


}
