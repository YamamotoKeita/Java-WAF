package jp.co.altonotes.webapp;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 指定された文字列より、適切な方法で適切なビューを表示する。
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
	 * コンストラクター
	 *
	 * @param servletContext
	 */
	public ViewResolver(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Viewを表示する
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
		// コンテキスト内リダイレクト
		if (path.startsWith(ViewType.REDIRECT)) {
			path = path.substring(REDIRECT_PREFIX_LENGTH);
			path = req.getContextPath() + path;
			redirect(path, resp, useURLSession);
		}
		// URLリダイレクト
		else if (path.startsWith(ViewType.URL_REDIRECT)) {
			path = path.substring(ABSOLUTE_REDIRECT_PREFIX_LENGTH);
			redirect(path, resp, useURLSession);
		}
		// 別コンテキストにリダイレクト
		else if (path.startsWith(ViewType.FORWARD_TO_OTHER_CONTEXT)) {
			path = path.substring(CROSS_APP_FORWARD_PREFIX_LENGTH);
			int idx = path.indexOf("://");
			if (idx == -1) {
				throw new IllegalArgumentException("コンテキストパスの記述がありません：" + path);
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
			throw new IllegalStateException(contextPath + " のコンテキストを取得できません");
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
	 * クライアントにリダイレクト要求を送信する。
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
	 * リクエストを転送する。
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
			} catch (ServletException e) {//forward先でエラー処理が行われるので何もしなくていいと思う
				e.printStackTrace();
			}
		}
	}

}
