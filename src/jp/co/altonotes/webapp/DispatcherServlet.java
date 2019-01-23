package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.http.HeaderNames;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.handler.IRequestHandler;

/**
 * Web Application のコントロールクラス。
 * 全てのHTTPリクエストを受け、前処理を行った後適切なモジュールやJSPにディスパッチする。
 *
 * @author Yamamoto Keita
 *
 */
public final class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -1777739678425294015L;

	/** JSPのディスパッチャー	*/
	private transient RequestDispatcher jspDispathcer;

	/** HTMLなど静的リソースのディスパッチャー	*/
	private transient RequestDispatcher defaultDispathcer;

	/** フィルター処理のマップ	*/
	private transient RequestMap<IRequestHandler> filterMap;

	/** GateWayのマップ	*/
	private transient RequestMap<IRequestHandler> gatewayMap;

	/** エラー処理のマップ	*/
	private transient RequestMap<IRequestHandler> errorHandlerMap;

	/** Forward, Rdirect処理のマップ	*/
	private transient RequestMap<IRequestHandler> viewMap;

	/** Viewのパスリゾルバー	*/
	private transient ViewResolver viewRsolver;

	/**
	 * 初期化
	 */
	@Override
	public void init() {

		try {
			ServletContext servletContext  = getServletContext();

			// configファイルの読み込み
			ApplicationConfig config = new ApplicationConfig(getServletContext());

			errorHandlerMap = config.getErrorHandlerMap();
			viewRsolver = new ViewResolver(servletContext);
			gatewayMap = config.loadGatewayMap();
			filterMap = config.getFilterMap();
			viewMap = config.getViewMap();
			jspDispathcer = servletContext.getNamedDispatcher("jsp");
			String defaultServletName = config.getDefaultServletName();
			defaultDispathcer =  servletContext.getNamedDispatcher(defaultServletName);

		} catch (RuntimeException e) {
			destroy();
			// throw すれば APServer が Exception ログを出してくれるはず
			throw e;
		}
	}

	/**
	 * 終了処理を実行する。
	 */
	@Override
	public void destroy() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	/**
	 * 全てのリクエストを処理する。
	 *
	 * @param req
	 * @param resp
	 * @param requestMethod
	 */
	private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
		
		RequestInfo context = RequestContext.getCurrentContext();
		context.setServletContext(getServletContext());
		try {
			doFilter(req, resp, context);
			
			if (doForward(req, resp, context)) {
				return;
			} else if (doJSP(req, resp, context)) {
				return;
			} else if (doGatewayMethod(req, resp, context)) {
				return;
			} else {
				doDefault(req, resp, context);
				return;
			}
		} catch (Throwable e) {
			doError(req, resp, "ディスパッチエラー", e, context);
		} finally {
			RequestContext.destroy();
		}
	}

	/**
	 * Gatewayメソッドを実行する。
	 *
	 * @param requestDelegate
	 * @return
	 */
	private boolean doGatewayMethod(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {
		IRequestHandler gateway = gatewayMap.get(delegate, req);

		if (gateway != null) {
			try {
				String viewPath = gateway.process(req, resp);
				if (viewPath != null) {
					viewRsolver.showView(viewPath, req, resp, delegate.isAvailableURLSession());
				}
			} catch (Throwable th) {
				doError(req, resp, "ゲートウェイエラー", th, delegate);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * フォワード処理を実行する
	 *
	 * @param context
	 * @return
	 */
	private boolean doForward(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		IRequestHandler view = viewMap.get(req);

		if (view != null) {
			try {
				view.process(req, resp);
			} catch (Throwable t) {
				doError(req, resp, "ビューエラー", t, delegate);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * JSPのリクエストを処理する。
	 *
	 * @param req
	 * @param resp
	 */
	private boolean doJSP(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		String extention = TextUtils.getExtention(RequestInfo.getRequestPath(req));

		if ("jsp".equals(extention) || "jspx".equals(extention)) {
			try {
				jspDispathcer.forward(req, resp);
			} catch (Exception e) {
				doError(req, resp, "ページエラー", e, delegate);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * サーブレット、JSP以外のリクエストを処理する。
	 *
	 * @param req
	 * @param resp
	 */
	private void doDefault(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		try {
			defaultDispathcer.forward(req, resp);
		} catch (Exception e) {
			doError(req, resp, "ファイルエラー", e, delegate);
		}
	}

	/**
	 * Filter処理を実行する
	 *
	 * @param context
	 */
	private void doFilter(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		IRequestHandler filter = filterMap.get(req);
		if (filter != null) {
			try {
				filter.process(req, resp);
			} catch (Throwable t) {
				doError(req, resp, "フィルターエラー", t, delegate);
			}
		}
	}

	/**
	 * エラー処理を行う。
	 *
	 * @param req
	 * @param resp
	 * @param message
	 * @param e
	 */
	private void doError(HttpServletRequest req, HttpServletResponse resp, String message, Throwable t, RequestInfo delegate) {

		// 以下のエラー処理でforwardしてさらにそこでエラーになると、またこのメソッドが呼ばれ無限ループするので、
		// 一度ここを通ったリクエストに対しては何もしない。
		if (RequestContext.hasError()) {
			doDefaultError(req, resp, "エラー処理中のエラー",t);
			return;
		} else {
			RequestContext.setError(t, message);
		}

		IRequestHandler exceptionHandler = errorHandlerMap.get(req);
		if (exceptionHandler != null) {
			try {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				String page = exceptionHandler.process(req, resp);
				viewRsolver.showView(page, req, resp, delegate.isAvailableURLSession());
				return;
			} catch (Throwable e) {
				doDefaultError(req, resp, "エラー処理中のエラー", e.initCause(t));
			}
		} else {
			doDefaultError(req, resp, message, t);
		}
	}

	/**
	 * デフォルトのエラー処理を行う。<br>
	 * エラー処理が無限ループするのでこのメソッドは絶対に何もThrowしてはいけない！
	 *
	 * @param req
	 * @param resp
	 * @param title
	 * @param th
	 */
	private void doDefaultError(HttpServletRequest req, HttpServletResponse resp, String title, Throwable th) {
		OutputStream out = null;
		th.printStackTrace();
		try {
			if (RequestContext.hasError()) {
				System.err.println("エラー処理中のエラー:" + th);
			}

			try {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				e.printStackTrace();
			}

			StringBuilder sb = new StringBuilder();
			putErrorHead(sb);

			//title
			sb.append("<div class=\"title\">");
			sb.append(title);
			sb.append("</div>\r\n");

			//exception
			sb.append("<div class=\"exception\">");
			sb.append(th.getClass().getName());
			sb.append("</div>\r\n");

			//message
			sb.append("<div class=\"message\">");

			String message = th.getMessage();
			if (message == null) {
				message = "";
			}
			message = TextUtils.htmlEscape(message);
			message = message.replaceAll("\\r\\n", "<br>");
			message = message.replaceAll("\\n", "<br>");

			sb.append(message);
			sb.append("</div>\r\n");

			//hr
			sb.append("<hr>\r\n");

			//stacktrace
			sb.append("<div class=\"stacktrace\">StackTrace:</div>\r\n");

			//stacks
			sb.append("<div class=\"stacks\">\r\n");
			StackTraceElement[] stacks = th.getStackTrace();
			for (StackTraceElement stack : stacks) {
				sb.append(stack.toString());
				sb.append("<br>\r\n");
			}

			Throwable cause = th.getCause();
			if (cause != null) {
				sb.append("<br>\r\ncause :<br>\r\n");
				stacks = cause.getStackTrace();
				for (StackTraceElement stack : stacks) {
					sb.append(stack.toString());
					sb.append("<br>\r\n");
				}
			}

			sb.append("</div>\r\n");

			//end
			sb.append("</body>\r\n</html>\r\n");

			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.setHeader(HeaderNames.CONTENT_TYPE, "text/html;charset=Shift_JIS");

			out = resp.getOutputStream();
			out.write(sb.toString().getBytes("Shift_JIS"));
		} catch (Throwable e) {
			// エラー処理が無限ループするのでこのエリアでは絶対に何もThrowしてはならない！
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void putErrorHead(StringBuilder sb) {
		sb.append("\r\n");
		sb.append("<style type=\"text/css\">\r\n");
		sb.append(".title {\r\n");
		sb.append("	font-size: large;\r\n");
		sb.append("	background-color: #C0E0F0;\r\n");
		sb.append("	margin-top: 0px;\r\n");
		sb.append("	padding: 3px 5px;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("}\r\n");
		sb.append(".exception {\r\n");
		sb.append("	font-size: medium;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("	color: red;\r\n");
		sb.append("	margin-top: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".message {\r\n");
		sb.append("	font-size: medium;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 1em;\r\n");
		sb.append("	margin-top: 0.5em;\r\n");
		sb.append("	margin-bottom: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".stacktrace {\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".stacks {\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 2em;\r\n");
		sb.append("}\r\n");
		sb.append("</style>\r\n");
		sb.append("\r\n");
		sb.append("</head>\r\n");
		sb.append("\r\n");
		sb.append("<body>");
	}
}
