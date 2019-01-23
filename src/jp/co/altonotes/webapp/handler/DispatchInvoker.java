package jp.co.altonotes.webapp.handler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * RequestDispatcher�����s����B
 *
 * @author Yamamoto Keita
 *
 */
public class DispatchInvoker implements IRequestHandler {

	/** �f�B�X�p�b�`��̃p�X	*/
	private String path;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param path
	 */
	public DispatchInvoker(String path) {
		this.path = path;
	}

	/*
	 * (�� Javadoc)
	 * @see jp.co.altonotes.webapp.handler.RequestHandler#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String process(HttpServletRequest req, HttpServletResponse resp) {

		try {
			RequestDispatcher dispathcer = req.getRequestDispatcher(path);
			dispathcer.forward(req, resp);
		} catch (Exception ignored) {} // forward��ŃG���[�������s���邽�߉������Ȃ�

		return null;
	}

}
