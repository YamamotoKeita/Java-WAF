package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.PropertyAccessor.AccessResult;
import jp.co.altonotes.webapp.util.MessageList;

/**
 * HTTPリクエストに紐づく情報を保持する。<br>
 * HttpServletRequest, HttpServletResponseなどの機能を代替する。
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestInfo {

	/** メッセージのアトリビュート名 */
	public static final String ATTRIBUTE_MESSAGES = "messages";

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String originalPath;
	private String path;
	private String htmlType;
	private boolean isAvailableURLSession;
	private MobileInfo mobileHeader;
	private MessageList messageList = new MessageList();;
	private Throwable systemError;
	private String systemErrorMessage;
	private Map<String, String> variableMap;
	private ServletContext servletContext;

	/**
	 * 
	 * @param request
	 * @return リクエストのパス
	 */
	protected static String getRequestPath(HttpServletRequest request) {
		String path = request.getPathInfo();
		if (path == null) {
			path = request.getServletPath();
		}
		return path;
	}
	
	/**
	 * コンストラクター
	 *
	 * @param req
	 */
	protected RequestInfo(HttpServletRequest req) {
		originalPath = getRequestPath(req);
		this.request = req;
	}

	/**
	 * リクエストとレスポンスの情報をセットする
	 * @param req
	 */
	protected void setSource(HttpServletRequest req, HttpServletResponse resp) {
		this.request = req;
		this.response = resp;
		path = getRequestPath(req);
	}

	/**
	 * パス変数をセットする
	 *
	 * @param variableMap
	 */
	protected void setPathVariable(Map<String, String> variableMap) {
		if (this.variableMap == null) {
			this.variableMap = variableMap;
		} else {
			Set<Entry<String, String>> entrySet = variableMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				variableMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * リクエストされたパスを取得する。
	 * Contextパスの後ろから、クエリ文字列の前までの文字列。
	 * 最初にリクエストされたパスであり、別のパスにリクエストを foward しても変更されない。
	 *
	 * @return リクエストパス。Contextパスおよびクエリは含まない。
	 */
	protected String getOriginalPath() {
		return originalPath;
	}

	/**
	 * HTMLのタイプを設定する
	 *
	 * @param type
	 */
	protected void setHTMLType(String type) {
		this.htmlType = type;
	}

	/**
	 * このリクエストに対して表示するページがXHTMLか判定する。
	 *
	 * @return 表示するページがXHTMLの場合<code>true</code>
	 */
	public boolean isXHTMLPage() {
		if (htmlType != null && htmlType.equalsIgnoreCase("xhtml")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * URLに付加されたセッションIDによるセッション管理を行うか設定する。<br>
	 * <code>true</code>を設定した場合、JSPタグでURLを書き出す際にセッションIDが付加される。<br>
	 * ただし、Cookieが有効な場合はCookieによるセッション管理が行われれ、URLにセッションIDは付加されない。
	 *
	 * @param flag
	 */
	protected void enableURLSession(boolean flag) {
		this.isAvailableURLSession = flag;
	}

	/**
	 * URLのセッションIDによるセッション管理が有効か判定する。<br>
	 * このメソッドが<code>true</code>を返す場合、JSPタグでURLを書き出す際にセッションIDが付加される。<br>
	 * ただし、Cookieが有効な場合はCookieによるセッション管理が行われれ、URLにセッションIDは付加されない。<br>
	 *
	 * @return URLに付加されたセッションIDによるセッション管理を行う場合<code>true</code>
	 */
	public boolean isAvailableURLSession() {
		return isAvailableURLSession;
	}

	/**
	 * パス変数を取得する。
	 *
	 * @param name
	 * @return
	 */
	protected String getPathVariable(String name) {
		if (variableMap == null) {
			return null;
		}
		return variableMap.get(name);
	}

	/**
	 * パス変数マップを取得する。
	 *
	 * @return
	 */
	protected Map<String, String> getPathVariableMap() {
		return variableMap;
	}

	/**
	 * 携帯端末の情報を取得する。
	 *
	 * @return
	 */
	protected MobileInfo getMobileInfo() {
		if (mobileHeader == null) {
			mobileHeader = MobileInfo.createFromRequest(this.request);
		}
		return mobileHeader;
	}

	/**
	 * HttpServletRequestを取得する。
	 *
	 * @return このリクエストに紐づくHttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * HttpServletResponseを取得する。
	 *
	 * @return このリクエストに紐づくHttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * HttpSessionを取得する。存在しない場合nullを返す。
	 *
	 * @return
	 */
	protected HttpSession getSession() {
		return request.getSession(false);
	}

	/**
	 * エラーメッセージを追加する。
	 *
	 * @param message
	 */
	protected void addMessage(String message) {
		messageList.add(message);
		request.setAttribute(ATTRIBUTE_MESSAGES, messageList);
	}

	/**
	 * エラーメッセージを取得する。
	 *
	 * @return
	 */
	protected MessageList getMessages() {
		return messageList;
	}

	/**
	 * エラーメッセージがあるか判定する。
	 *
	 * @return
	 */
	protected boolean hasMessage() {
		return messageList != null && messageList.hasMessage();
	}

	/**
	 * システムエラーを設定する。
	 *
	 * @param t
	 * @param message
	 */
	protected void setError(Throwable t, String message) {
		this.systemError = t;
		this.systemErrorMessage = message;
	}

	/**
	 * システムエラーがあるか判定する。
	 *
	 * @return
	 */
	protected boolean hasThrowable() {
		return systemError != null;
	}

	/**
	 * システムエラーメッセージを取得する。
	 *
	 * @return
	 */
	protected String getSystemErrorMessage() {
		return systemErrorMessage;
	}

	/**
	 * システムエラーを取得する。
	 *
	 * @return
	 */
	protected Throwable getError() {
		return systemError;
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、リクエストにセットする。
	 *
	 * @param <T>
	 * @param name
	 * @param object
	 * @return
	 */
	protected <T> T bind(String name, T object) {

		inject(name, object);

		request.setAttribute(name, object);

		return object;
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、リクエストにセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param initial
	 * @return
	 */
	protected <T> T bind(T initial) {
		return bind(toBeanName(initial), initial);
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、セッションにセットする。
	 *
	 * @param <T>
	 * @param name
	 * @param initial
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T bindInSession(String name, T initial) {
		T target = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			target = (T) session.getAttribute(name);
		} else {
			session = request.getSession(true);
		}

		if (target == null) {
			target = initial;
			session.setAttribute(name, target);
		}

		inject(name, target);

		return target;
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、セッションにセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param key
	 * @param initial
	 * @return
	 */
	protected <T> T bindInSession(T initial) {
		return bindInSession(toBeanName(initial), initial);
	}

	/**
	 * リクエストパラメータにより、パラメータ名に対応する対象オブジェクトの値を更新する。
	 *
	 * @param attributeName
	 * @param target
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object inject(String attributeName, Object target) {
		String attrKey = attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR;
		int idx = attrKey.length();

		Enumeration<String> nameList = request.getParameterNames();

		// パラメーター名だけ処理する
		while (nameList.hasMoreElements()) {
			String paramName = nameList.nextElement();

			if (!paramName.startsWith(attrKey)) {
				continue;
			}

			String propertyName = paramName.substring(idx);
			if (propertyName.length() == 0) {
				System.out.println("[警告]プロパティ名のフォーマットが不正です：" + paramName);
				continue;
			}

			String[] paramValues = request.getParameterValues(paramName);

			AccessResult result = null;

			if (paramValues.length == 1) {
				// String 値をセットする
				result = PropertyAccessor.setNestedProperty(attributeName, target, propertyName, paramValues[0]);
			} else {
				// String[] 値をセットする
				result = PropertyAccessor.setNestedProperty(attributeName, target, propertyName, paramValues);
			}

			if (!result.isSuccess()) {
				String message = "[警告] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " に値をセットできませんでした";
				if (result.hasMessage()) {
					System.out.println(message + "：" + result.message);
				} else {
					System.out.println(message);
				}
			}
		}
		return target;
	}

	/**
	 * オブジェクトをセットする。
	 *
	 * @param name
	 * @param obj
	 */
	protected void set(String name, Object obj) {
		request.setAttribute(name, obj);
	}

	/**
	 * オブジェクトをセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param obj
	 */
	protected void set(Object obj) {
		set(toBeanName(obj), obj);
	}

	/**
	 * オブジェクトを取得する。
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T get(String name, Class<T> klass) {
		Object obj = request.getAttribute(name);
		if (obj == null) {
			return null;
		}

		return (T) obj;
	}

	/**
	 * オブジェクトを取得する。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param klass
	 * @return
	 */
	protected <T> T get(Class<T> klass) {
		return get(toBeanName(klass), klass);
	}

	/**
	 * オブジェクトをセッションにセットする。
	 *
	 * @param name
	 * @param obj
	 */
	protected void setInSession(String name, Object obj) {
		request.getSession().setAttribute(name, obj);
	}

	/**
	 * オブジェクトをセッションにセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param obj
	 */
	protected void setInSession(Object obj) {
		setInSession(toBeanName(obj), obj);
	}

	/**
	 * オブジェクトをセッションから取得する。
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getFromSession(String name, Class<T> klass) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		Object obj = session.getAttribute(name);
		if (obj == null) {
			return null;
		}

		return (T) obj;
	}

	/**
	 * オブジェクトをセッションから取得する。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param klass
	 * @return
	 */
	protected <T> T getFromSession(Class<T> klass) {
		return getFromSession(toBeanName(klass), klass);
	}

	/**
	 * オブジェクトのクラス名をBean名に変換する。
	 *
	 * @param obj
	 * @return
	 */
	private static String toBeanName(Object obj) {
		return toBeanName(obj.getClass());
	}

	/**
	 * クラス名をBean名に変換する。
	 *
	 * @param obj
	 * @return
	 */
	private static String toBeanName(Class<?> klass) {
		String beanName = klass.getSimpleName();
		beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1, beanName.length());
		return beanName;
	}

	/**
	 * レスポンスにクッキーを追加する。
	 *
	 * @param cookie
	 */
	protected void addResponseCookie(Cookie cookie) {
		response.addCookie(cookie);
	}

	/**
	 * クッキーを取得する。
	 *
	 * @return
	 */
	protected Cookie[] getCookies() {
		return request.getCookies();
	}

	/**
	 * セッション情報を削除する。
	 *
	 */
	protected void deleteSession() {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * IPアドレスを取得する。
	 *
	 * @return
	 */
	protected String getIPAddress() {
		return request.getRemoteAddr();
	}

	/**
	 * InputStreamを取得する。
	 *
	 * @return
	 * @throws IOException
	 */
	protected InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	/**
	 * リダイレクト要求を返却する。
	 *
	 * @param location
	 * @throws IOException
	 */
	protected void sendRedirect(String location) {
		try {
			response.sendRedirect(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * フォワードする。
	 *
	 * @param path
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void forward(String path) {
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 引数に指定した名前のリクエストパラメーターを取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のリクエストパラメーター
	 */
	protected String getParameter(String name) {
		return request.getParameter(name);
	}

	/**
	 * JSESSIONIDを取得する
	 *
	 * @return JSESSIONID
	 */
	protected String getJSessionID() {
		Cookie[] cookies = getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				return cookie.getValue();
			}
		}

		return null;
	}

	/**
	 * 引数に指定した名前のCookieの値を取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のCookieの値
	 */
	protected String getCookieValue(String name) {
		Cookie cookie = getCookie(name);

		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	/**
	 * 引数に指定した名前のCookieを取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のCookie
	 */
	protected Cookie getCookie(String name) {
		Cookie[] cookies = getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}

		return null;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	protected void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	protected ServletContext getServletContext() {
		return servletContext;
	}

//	public void setParameter(String name, String value) {
//	}
//
//	public void setParameterMap(Map<String, String> map) {
//	}
//
//	public void setParametersMap(Map<String, String[]> map) {
//	}
//
//	public void setParameterBean(Object obj) {
//	}
//
//	public void setPath(String path) {
//
//	}
//
//	public void setInputStream(InputStream in) {
//
//	}
//
//	public void setCookie(Cookie cookie) {
//
//	}
//
//	public void setMobileInfo(MobileInfo mobileInfo) {
//	}

}
