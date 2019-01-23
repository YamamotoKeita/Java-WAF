package jp.co.altonotes.webapp.handler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * RequestDispatcherを実行する。
 *
 * @author Yamamoto Keita
 *
 */
public class DispatchInvoker implements IRequestHandler {

	/** ディスパッチ先のパス	*/
	private String path;

	/**
	 * コンストラクター
	 *
	 * @param path
	 */
	public DispatchInvoker(String path) {
		this.path = path;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.handler.RequestHandler#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String process(HttpServletRequest req, HttpServletResponse resp) {

		try {
			RequestDispatcher dispathcer = req.getRequestDispatcher(path);
			dispathcer.forward(req, resp);
		} catch (Exception ignored) {} // forward先でエラー処理が行われるため何もしない

		return null;
	}

}
