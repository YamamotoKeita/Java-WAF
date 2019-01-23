package jp.co.altonotes.webapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * リクエストを処理するモジュール
 *
 * @author Yamamoto Keita
 *
 */
public interface IRequestHandler {

	/**
	 * HTTPリクエストに対する処理を行う。
	 *
	 * @param req
	 * @param resp
	 * @return メソッド実行後の遷移先のパス
	 * @throws Throwable
	 */
	public String process(HttpServletRequest req, HttpServletResponse resp) throws Throwable;
}
