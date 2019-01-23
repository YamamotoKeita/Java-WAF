package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.webapp.exception.StartupException;

/**
 * リクエストコンテキストを作成するフィルター
 * @author Yamamoto Keita
 *
 */
public class RequestContextFilter implements Filter {
	
	/** 起動時実行モジュール処理エラーメッセージ	*/
	private static final String ERROR_MESSAGE_STARTUP = "<initialize>プロセスの実行中にエラーが発生しました：";

	/** 起動時実行モジュール一覧	*/
	private List<StartupProcess> startUps;

	/** 文字コードのマップ	*/
	private transient RequestMap<String> charsetMap;

	/** HTML TYPEのマップ	*/
	private transient RequestMap<String> htmlTypeMap;

	/** URL Sessionのマップ	*/
	private transient RequestMap<Boolean> urlSessionMap;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			ServletContext servletContext  = filterConfig.getServletContext();
			System.out.println("\r\n[" +servletContext .getServletContextName() + "] を初期化します");

			// configファイルの読み込み
			ApplicationConfig config = new ApplicationConfig(servletContext);

			charsetMap = config.getCharsetMap();
			htmlTypeMap = config.getHTMLTypeMap();
			urlSessionMap = config.getURLSessionMap();
			startUps = config.getStartups();

			for (StartupProcess process : startUps) {
				try {
					process.run();
				} catch (Throwable e) {
					throw (StartupException) new StartupException(ERROR_MESSAGE_STARTUP + process).initCause(e);
				}
			}

		} catch (RuntimeException e) {
			destroy();
			// throw すれば APServer が Exception ログを出してくれるはず
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		if (startUps != null) {
			for (StartupProcess startup : startUps) {
				startup.destroy();
			}
		}
	}
	
	/*
	 * Filter処理を行う
	 *
	 * (非 Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srcReq, ServletResponse srcResp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) srcReq;
		HttpServletResponse resp = (HttpServletResponse) srcResp;

		RequestInfo context = RequestContext.getCurrentContext();
		context.setSource(req, resp);

		setCharacterEncoding(context);
		setHTMLType(context);
		setURLSessionFlag(context);
		
		chain.doFilter(req, resp);
	}

	/**
	 * リクエストパラメーターのエンコーディングを設定する。
	 *
	 * 注意：GETパラメーターの文字コードはページエンコーディングに関わらず、UTF-8にするのがルールだが、
	 * GETパラメーターもページエンコーディングと同じエンコーディングで送信するブラウザがの方が多いのでGETリクエストにもページエンコーディングを設定する。
	 * GETクエリに対するsetCharacterEncodingを有効にするためには、Tomcat5.X以降ではserver.xmlのconnectorの属性にuseBodyEncodingForURI="true"を設定する必要がある。
	 *
	 * @param context
	 */
	private void setCharacterEncoding(RequestInfo context) {

		String charset = charsetMap.get(context.getRequest());

		if (charset != null) {
			try {
				context.getRequest().setCharacterEncoding(charset);
			} catch (UnsupportedEncodingException ignored) {} //起動時にチェックするので無視
		}
	}

	/**
	 * HTML TYPEを設定する。
	 *
	 * @param req
	 * @param path
	 */
	private void setHTMLType(RequestInfo context) {
		String type = htmlTypeMap.get(context.getRequest());
		if (type != null) {
			context.setHTMLType(type);
		}
	}

	/**
	 * URLSessionのフラグをセットする
	 *
	 * @param context
	 */
	private void setURLSessionFlag(RequestInfo context) {
		Boolean flag = urlSessionMap.get(context.getRequest());
		if (flag != null && flag) {
			context.enableURLSession(true);
		}
	}
}
