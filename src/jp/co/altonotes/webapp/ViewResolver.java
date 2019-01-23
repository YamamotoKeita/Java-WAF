package jp.co.altonotes.webapp;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �w�肳�ꂽ��������A�K�؂ȕ��@�œK�؂ȃr���[��\������B
 *
 * @author Yamamoto Keita
 *
 */
public final class ViewResolver {

	private static final int REDIRECT_PREFIX_LENGTH = ViewType.REDIRECT.length();
	private static final int ABSOLUTE_REDIRECT_PREFIX_LENGTH = ViewType.URL_REDIRECT.length();
	private static final int CROSS_APP_FORWARD_PREFIX_LENGTH = ViewType.FORWARD_TO_OTHER_CONTEXT.length();

	private ServletContext servletContext;

	/**
	 *
	 * �R���X�g���N�^�[
	 *
	 * @param servletContext
	 */
	public ViewResolver(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * View��\������
	 *
	 * @param path
	 * @param req
	 * @param resp 
	 * @param useURLSession 
	 * @throws IOException
	 */
	public void showView(String path, HttpServletRequest req, HttpServletResponse resp, boolean useURLSession) throws IOException {

		if (path == null || path.length() == 0) {
			return;
		}
		// �R���e�L�X�g�����_�C���N�g
		if (path.startsWith(ViewType.REDIRECT)) {
			path = path.substring(REDIRECT_PREFIX_LENGTH);
			path = req.getContextPath() + path;
			redirect(path, resp, useURLSession);
		}
		// URL���_�C���N�g
		else if (path.startsWith(ViewType.URL_REDIRECT)) {
			path = path.substring(ABSOLUTE_REDIRECT_PREFIX_LENGTH);
			redirect(path, resp, useURLSession);
		}
		// �ʃR���e�L�X�g�Ƀ��_�C���N�g
		else if (path.startsWith(ViewType.FORWARD_TO_OTHER_CONTEXT)) {
			path = path.substring(CROSS_APP_FORWARD_PREFIX_LENGTH);
			int idx = path.indexOf("://");
			if (idx == -1) {
				throw new IllegalArgumentException("�R���e�L�X�g�p�X�̋L�q������܂���F" + path);
			}
			String contextPath = path.substring(0, idx);
			path = path.substring(idx + 3);
			forward(contextPath, path, req, resp);
		} else {
			forward(path, req, resp);
		}
	}

	private void forward(String contextPath, String path, HttpServletRequest req, HttpServletResponse resp) {
		ServletContext otherContext = servletContext.getContext(contextPath);
		if (otherContext == null) {
			throw new IllegalStateException(contextPath + " �̃R���e�L�X�g���擾�ł��܂���");
		}

		RequestDispatcher dispatcher = otherContext.getRequestDispatcher(path);
		try {
			dispatcher.forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �N���C�A���g�Ƀ��_�C���N�g�v���𑗐M����B
	 *
	 * @param path
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void redirect(String path, HttpServletResponse resp, boolean useURLSession) throws IOException {
		if (useURLSession) {
			path = resp.encodeRedirectURL(path);
		}
		resp.sendRedirect(path);
	}

	/**
	 * ���N�G�X�g��]������B
	 *
	 * @param path
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	public static void forward(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);

		if (dispatcher != null) {
			try {
				dispatcher.forward(req, resp);
			} catch (ServletException e) {//forward��ŃG���[�������s����̂ŉ������Ȃ��Ă����Ǝv��
				e.printStackTrace();
			}
		}
	}

}
