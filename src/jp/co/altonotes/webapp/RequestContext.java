package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.util.MessageList;

/**
 * HTTPリクエストのコンテキストへのアクセスを提供する。<br>
 * ThreadLocal にコンテキストの情報を保存することにより、<br>
 * このクラスの static メソッドでコンテキストの情報にアクセスできる。<br>
 *
 * HttpServletRequest, HttpServletResponseの持つ機能に加えて、
 * リクエストパラメーターをオブジェクトへの埋め込み機能や、
 * メッセージの管理などの機能を持つ。
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestContext {

	/** コンテキストオブジェクトを保存するServletRequestの属性名 */
	public static final String FRAMEWORK_CONTEXT = "FRAMEWORK_CONTEXT";

	private static ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<RequestInfo>();

	/**
	 * ThreadLocal にリクエストのコンテキストを作成する。
	 * 作成したコンテキストの情報はこのクラスの静的メソッドで同一スレッドから取得できるようになる。
	 *
	 * @param req
	 * @return コンテキストの情報を保持する RequestDelegate インスタンス
	 */
	protected static RequestInfo createContext(HttpServletRequest req) {
		RequestInfo requestInfo = new RequestInfo(req);
		threadLocal.set(requestInfo);
		req.setAttribute(FRAMEWORK_CONTEXT, requestInfo);
		return requestInfo;
	}

	/**
	 * カレントスレッドのContextを破棄する
	 */
	public static void destroy() {
		threadLocal.remove();
	}

	/**
	 * Contextが既に存在するか判定する。
	 *
	 * @return
	 */
	public static boolean hasContext() {
		return threadLocal.get() != null;
	}

	/**
	 * 既に存在するRequestDelegateを取得する
	 *
	 * @return
	 */
	protected static RequestInfo getCurrentContext() {
		return threadLocal.get();
	}

	/**
	 * ServletRequestに紐づくRequest情報を取得する
	 *
	 * @param request
	 * @return ServletRequestに紐づくRequest情報
	 */
	public static RequestInfo getCurrentContext(ServletRequest request) {
		return (RequestInfo) request.getAttribute(FRAMEWORK_CONTEXT);
	}

	/**
	 * 指定した名前のパス変数を取得する。
	 *
	 * @param name
	 * @return 引数に指定した名前に対応するパス変数
	 */
	public static String getPathVariable(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getPathVariable(name);
	}

	/**
	 * システムエラーをセットする。
	 *
	 * @param t
	 * @param message
	 */
	protected static void setError(Throwable t, String message) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setError(t, message);
	}

	/**
	 * CookieからJSESSIONIDの値を取得する
	 *
	 * @return リクエストのCookieにセットされたJSESSIONIDの値
	 */
	public static String getJSessionID() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getJSessionID();
	}

	/**
	 * システムエラーを取得する。
	 *
	 * @return 発生したシステムエラー
	 */
	public static Throwable getError() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getError();
	}

	/**
	 * 発生したエラーのタイトルを取得する。
	 *
	 * @return 発生したシステムエラーのタイトル
	 */
	public static String getErrorMessage() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getSystemErrorMessage();
	}

	/**
	 * システムエラーが発生しているか判定する。
	 *
	 * @return システムエラーが発生している場合<code>true</code>
	 */
	public static boolean hasError() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.hasThrowable();
	}

	/**
	 * 引数に指定した名前のリクエストパラメーターを取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のリクエストパラメーター
	 */
	public static String getParameter(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getParameter(name);
	}

	/**
	 * リクエストパラメータを引数のインスタンスの対応するプロパティにセットし、Contextにセットする。
	 *
	 * @param initial
	 * @param key
	 * @return 適切な値をセットした、引数で渡されたオブジェクト
	 */
	public static <T> T bind(String key, T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bind(key, initial);
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、Contextにセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param initial
	 * @return 適切な値をセットした、引数で渡されたオブジェクト
	 */
	public static <T> T bind(T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bind(initial);
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、ContextのSessionにセットする。
	 *
	 * @param initial
	 * @param key
	 * @return 適切な値をセットした、引数で渡されたオブジェクト
	 */
	public static <T> T bindInSession(String key, T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bindInSession(key, initial);
	}

	/**
	 * リクエストパラメータを引数のインスタンスに埋め込み、ContextのSessionにセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param initial
	 * @return 適切な値をセットした、引数で渡されたオブジェクト
	 */
	public static <T> T bindInSession(T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bindInSession(initial);
	}

	/**
	 * オブジェクトをセットする。
	 *
	 * @param name
	 * @param obj
	 */
	public static void set(String name, Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.set(name, obj);
	}

	/**
	 * オブジェクトをセットする。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param obj
	 */
	public static void set(Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.set(obj);
	}

	/**
	 * オブジェクトを取得する。
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return 引数の名前でattributeにセットされたオブジェクト
	 */
	public static <T> T get(String name, Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.get(name, klass);
	}

	/**
	 * オブジェクトを取得する。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param klass
	 * @return クラス名の先頭を小文字にした名前でattributeにセットされたオブジェクト
	 */
	public static <T> T get(Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.get(klass);
	}

	/**
	 * オブジェクトをセッションにセットする。
	 *
	 * @param name
	 * @param obj
	 */
	public static void setInSession(String name, Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setInSession(name, obj);
	}

	/**
	 * オブジェクトをセッションアトリビュートにセットする。
	 * クラス名の先頭を小文字にした文字列がアトリビュート名として使用される。
	 *
	 * @param obj
	 */
	public static void setInSession(Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setInSession(obj);
	}

	/**
	 * 引数の名前のアトリビュートをセッションから取得する。
	 * アトリビュートは引数に指定したクラスにキャストされる。
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return 引数の名前でセッションAttributeから取得したオブジェクト
	 */
	public static <T> T getFromSession(String name, Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getFromSession(name, klass);
	}

	/**
	 * オブジェクトをセッションから取得する。
	 * キーはクラス名の先頭を小文字にしたものを使用する。
	 *
	 * @param <T>
	 * @param klass
	 * @return セッションアAttributeから取得したオブジェクト
	 */
	public static <T> T getFromSession(Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getFromSession(klass);
	}

	/**
	 * コンテキストにメッセージを追加する。
	 *
	 * @param message
	 */
	public static void addMessage(String message) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.addMessage(message);
	}

	/**
	 * コンテキストにメッセージがあるか判定する。
	 *
	 * @return コンテキストにメッセージがある場合<code>true</code>
	 */
	public static boolean hasMessage() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.hasMessage();
	}

	/**
	 * コンテキストのメッセージを取得する。
	 *
	 * @return コンテキストにセットされたメッセージを
	 */
	public static MessageList getMessages() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getMessages();
	}

	/**
	 * コンテキストのHttpServletRequestを取得する。
	 *
	 * @return コンテキストのHttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest();
	}

	/**
	 * コンテキストのHttpSessionを取得する。
	 * 存在しない場合nullを返す。
	 *
	 * @return コンテキストのHttpSession
	 */
	public static HttpSession getSession() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getSession();
	}

	/**
	 * コンテキストのHttpServletResponseを取得する。
	 *
	 * @return コンテキストのHttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getResponse();
	}

	/**
	 * 携帯端末情報を取得する。
	 *
	 * @return 携帯端末情報
	 */
	public static MobileInfo getMobileInfo() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getMobileInfo();
	}

	/**
	 * コンテキストのレスポンスにcookieを追加する。
	 *
	 * @param cookie
	 */
	public static void addResponseCookie(Cookie cookie) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.addResponseCookie(cookie);
	}

	/**
	 * リクエストに含まれるクッキー情報を取得する。
	 *
	 * @return リクエストに含まれるクッキー情報
	 */
	public static Cookie[] getCookies() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookies();
	}

	/**
	 * コンテキストのセッション情報を削除する。
	 *
	 */
	public static void deleteSession() {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.deleteSession();
	}

	/**
	 * リクエストのIPアドレスを取得する。
	 *
	 * @return リクエストのIPアドレス
	 */
	public static String getIPAddress() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getIPAddress();
	}

	/**
	 * 表示するページがXHTMLか判定する。
	 *
	 * @return 表示するページがXHTMLの場合<code>true</code>
	 */
	public static boolean isXHTML() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.isXHTMLPage();
	}

	/**
	 * リクエストのInputStreamを取得する
	 *
	 * @return リクエストのInputStream
	 * @throws IOException
	 */
	public static InputStream getInputStream() throws IOException {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getInputStream();
	}

	/**
	 * リダイレクト要求を送信する
	 *
	 * @param location
	 */
	public static void sendRedirect(String location) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.sendRedirect(location);
	}

	/**
	 * フォワードする
	 *
	 * @param path
	 */
	public static void forward(String path) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.forward(path);
	}

	/**
	 * 引数に指定した名前のCookieの値を取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のCookieの値
	 */
	public static String getCookieValue(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookieValue(name);
	}

	/**
	 * 引数に指定した名前のCookieを取得する
	 *
	 * @param name
	 * @return 引数に指定した名前のCookie
	 */
	public static Cookie getCookie(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookie(name);
	}

	/**
	 * コンテキストパスを取得する
	 * @return コンテキストパス
	 */
	public static String getContextPath() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest().getContextPath();
	}
	
	/**
	 * ServletContextを取得する
	 * @return ServletContext
	 */
	public static ServletContext getServletContext() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getServletContext();
	}
	
	/**
	 * User-Agentを取得する
	 * @return User-Agent
	 */
	public static String getUserAgent() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest().getHeader("User-Agent");
	}
}
