package jp.co.altonotes.http;

/**
 * HttpHeaderのパラメータ名一覧を保持するクラス。<br>
 * enum にしてもいいかもしれない。
 *
 * @author Yamamoto Keita
 *
 */
public class HeaderNames {

	//汎用
	/** Content-Type	*/
	public static final String CONTENT_TYPE = "Content-Type";
	/** Content-Encoding	*/
	public static final String CONTENT_ENCODING = "Content-Encoding";
	/** Accept-Encoding	*/
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	/** Accept-Language	*/
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	/** User-Agent	*/
	public static final String USER_AGENT = "User-Agent";
	/** Accept	*/
	public static final String ACCEPT = "Accept";
	/** Accept-Charset	*/
	public static final String ACCEPT_CHARSET = "Accept-Charset";
	/** Location	*/
	public static final String LOCATION = "Location";
	/** Referer	*/
	public static final String REFERER = "Referer";

	//IE7
	/** ua-cpu	*/
	public static final String UA_CPU = "ua-cpu";

	//SoftBank
	/** x-jphone-msname	*/
	public static final String X_JPHONE_MSNAME = "x-jphone-msname";
	/** x-jphone-smaf	*/
	public static final String X_JPHONE_SMAF = "x-jphone-smaf";
	/** x-jphone-display	*/
	public static final String X_JPHONE_DISPLAY = "x-jphone-display";
	/** x-jphone-region	*/
	public static final String X_JPHONE_REGION = "x-jphone-region";
	/** x-jphone-uid	*/
	public static final String X_JPHONE_UID = "x-jphone-uid";
	/** x-jphone-color	*/
	public static final String X_JPHONE_COLOR = "x-jphone-color";
	/** x-s-display-info	*/
	public static final String X_S_DISPLAY_INFO = "x-s-display-info";
	/** Max-Forwards	*/
	public static final String MAX_FORWARDS = "Max-Forwards";

	//変数
	/** Cache-Control	*/
	public static final String CACHE_CONTROL = "Cache-Control";
	/** Pragma	*/
	public static final String PRAGMA = "Pragma";
	/** Cookie	*/
	public static final String COOKIE = "Cookie";
	/** Set-Cookie	*/
	public static final String SET_COOKIE = "Set-Cookie";
	/** Set-Cookie2	*/
	public static final String SET_COOKIE2 = "Set-Cookie2";
	/** Keep-Alive	*/
	public static final String KEEP_ALIVE = "Keep-Alive";

	//特殊
	/** Refresh	*/
	public static final String REFRESH = "Refresh";

}
