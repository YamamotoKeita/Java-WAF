package jp.co.altonotes.webapp.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;

/**
 * コンテンツを提供するサーブレット。
 * HTTPサーバーの役目をする。
 *
 * APサーバーのコンテンツ出力サーブレットを使用すると、
 * フレームワークで404が感知できないため
 *
 * @author Yamamoto Keita
 *
 */
public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -3425858095631492382L;

	private transient MimeMapps mimeMaps;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serveResource(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serveResource(req, resp);
	}

	/**
	 * 対応するリソースをクライアントに出力する。
	 *
	 * @param req
	 * @param resp
	 */
	protected void serveResource(HttpServletRequest req, HttpServletResponse resp) {
		setResponseHeader(req, resp);
		outputResource(req, resp);
	}

	private void setResponseHeader(HttpServletRequest req, HttpServletResponse resp) {
		//Content-Length
		// Content-Type
		//Cach-Control
		//Pragma
		//Expires
		//Content-Encoding
		//Etag

		//テキストのレスポンスは圧縮する価値があります。
		//イメージとPDFファイルは圧縮してはいけません。CPUを浪費するだけではなく、ファイルサイズがかえって大きくなってしまいます。

	}

	private void outputResource(HttpServletRequest req, HttpServletResponse resp) {

	}

	/**
	 * Request先の相対パスを取得する。
	 *
	 * @param req
	 * @return
	 */
	protected String getRelativePath(HttpServletRequest req) {

		// RequestDispatcher.include() で指定された path をチェックする。
		if (req.getAttribute(Globals.INCLUDE_REQUEST_URI_ATTR) != null) {
			String result = (String) req.getAttribute(Globals.INCLUDE_PATH_INFO_ATTR);
			if (result == null) {
				result = (String) req.getAttribute(Globals.INCLUDE_SERVLET_PATH_ATTR);
			}
			if ((result == null) || (result.equals(""))) {
				result = "/";
			}
			return (result);
		}

		//なければパス取得
		String result = req.getPathInfo();
		if (result == null) {
			result = req.getServletPath();
		}
		if ((result == null) || (result.equals(""))) {
			result = "/";
		}
		return (result);

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO 自動生成されたメソッド・スタブ
		super.doDelete(req, resp);
	}


	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO 自動生成されたメソッド・スタブ
		super.doHead(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO 自動生成されたメソッド・スタブ
		super.doOptions(arg0, arg1);
	}


	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO 自動生成されたメソッド・スタブ
		super.doPut(req, resp);
	}

}
