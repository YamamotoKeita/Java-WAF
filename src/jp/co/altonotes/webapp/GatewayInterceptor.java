package jp.co.altonotes.webapp;

import jp.co.altonotes.webapp.handler.GatewayInvoker.GatewayInvocation;


/**
 * Gateway �̏������C���^�[�Z�v�g����ꍇ�ɁA
 * ���̃N���X���p�����āA�e���\�b�h���I�[�o�[���C�h����B
 * 
 * @author Yamamoto Keita
 *
 */
public interface GatewayInterceptor {

	/**
	 * Gateway�����̊J�n�O�Ɏ��s���鏈��
	 */
	public void before();
	
	/**
	 * Gateway�����̊�����Ɏ��s���鏈��
	 */
	public void after();
	
	/**
	 * Gateway���������ݍ��ތ`�ő}�����鏈��
	 * 
	 * @param invocation
	 * @return �J�ڐ�̃p�X
	 * @throws Throwable
	 */
	public String around(GatewayInvocation invocation) throws Throwable;
}
