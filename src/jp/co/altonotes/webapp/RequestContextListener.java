package jp.co.altonotes.webapp;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * ���N�G�X�g�̊J�n���Ƀt���[�����[�N�̃R���e�L�X�g���쐬����B
 * DispatcherServlet �ō쐬����ƁA�}�b�s���O�ΏۊO�� jsp �ȂǂɃA�N�Z�X�����ۃR���e�L�X�g���쐬�ł��Ȃ����߃��X�i�[�ɂ����B
 * 
 * @author Yamamoto Keita
 *
 */
public class RequestContextListener implements ServletRequestListener {

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
	 */
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest req = (HttpServletRequest) event.getServletRequest();
		// RequestContext ���쐬����
		RequestContext.createContext(req);
	}

	/*
	 *
	 * (�� Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
	 */
	public void requestDestroyed(ServletRequestEvent event) {
		// ������ RequestContext ��j������z�肾�������Aweb.xml �� error-page �����̏����̌�ɌĂ΂�邽�ߔj���͂��Ȃ��B
		// ���̂Ƃ��� RequestContext �͂ǂ��ł��j�������A�V�������̂ŏ㏑������`
	}
}
