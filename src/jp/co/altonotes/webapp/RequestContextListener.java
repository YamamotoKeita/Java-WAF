package jp.co.altonotes.webapp;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * リクエストの開始時にフレームワークのコンテキストを作成する。
 * DispatcherServlet で作成すると、マッピング対象外の jsp などにアクセスした際コンテキストが作成できないためリスナーにした。
 * 
 * @author Yamamoto Keita
 *
 */
public class RequestContextListener implements ServletRequestListener {

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
	 */
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest req = (HttpServletRequest) event.getServletRequest();
		// RequestContext を作成する
		RequestContext.createContext(req);
	}

	/*
	 *
	 * (非 Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
	 */
	public void requestDestroyed(ServletRequestEvent event) {
		// ここで RequestContext を破棄する想定だったが、web.xml の error-page がこの処理の後に呼ばれるため破棄はしない。
		// 今のところ RequestContext はどこでも破棄せず、新しいもので上書きする形
	}
}
