package jp.co.altonotes.webapp.property;

/**
 * �v���p�e�B�A�N�Z�X�̌��ʂ�ێ�����B
 * �p�t�H�[�}���X�̂��߁A���\�b�h���Ő�������̂ł͂Ȃ��A�����ɓn���B
 * 
 * @author Yamamoto Keita
 *
 */
final class Result {

	/** ���b�Z�[�W */
	protected String message;
	
	/** �G���[�t���O */
	protected boolean isFailed;

	/**
	 * �R���X�g���N�^�[
	 */
	protected Result() {}

	/**
	 * �A�N�Z�X�������s�̍ۂɌĂяo���B
	 * @param message
	 */
	protected void fail(String message) {
		this.message = message;
		isFailed = true;
	}
	
	/**
	 * @return ���b�Z�[�W������ꍇ<code>true</code>
	 */
	protected boolean hasMessage() {
		return message != null;
	}


	@Override
	public String toString() {
		return isFailed ? "FAILED" : "SUCCESS" + " : " + message;
	}

	/**
	 * �ێ����錋�ʂ��N���A����
	 */
	public void clear() {
		message = null;
		isFailed = false;
	}

}
