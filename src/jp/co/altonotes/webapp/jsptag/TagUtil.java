package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.PageContext;

import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.RequestInfo;

/**
 * カスタムタグクラスで使用するユーティリティー
 * @author Yamamoto Keita
 *
 */
public class TagUtil {

	/**
	 * config.xml で指定した HTMLタイプが XHTML か判定する
	 * @param pageContext
	 * @return ページが XHTML の場合 true
	 */
	public static boolean isXHTML(PageContext pageContext) {
		RequestInfo context = RequestContext.getCurrentContext(pageContext.getRequest());
		return context.isXHTMLPage();
	}

	/**
	 * config.xml でURLセッションが有効に設定されているか判定する
	 * @param pageContext
	 * @return URLセッションが有効にな場合 true
	 */
	public static boolean isAvailableURLSession(PageContext pageContext) {
		RequestInfo context = RequestContext.getCurrentContext(pageContext.getRequest());
		return context.isAvailableURLSession();
	}
}
