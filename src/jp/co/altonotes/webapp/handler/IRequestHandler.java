package jp.co.altonotes.webapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ���N�G�X�g���������郂�W���[��
 *
 * @author Yamamoto Keita
 *
 */
public interface IRequestHandler {

	/**
	 * HTTP���N�G�X�g�ɑ΂��鏈�����s���B
	 *
	 * @param req
	 * @param resp
	 * @return ���\�b�h���s��̑J�ڐ�̃p�X
	 * @throws Throwable
	 */
	public String process(HttpServletRequest req, HttpServletResponse resp) throws Throwable;
}
