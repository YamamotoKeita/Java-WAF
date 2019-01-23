package jp.co.altonotes.http;

/**
 * 特定のブラウザのリクエストを真似たリクエストヘッダを生成するクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class RequestHeaderFactory {

	//TODO enumにしたい
	/** 未定義	*/
	public static final int TYPE_UNKNOWN = -1;
	/** InternetExplorer 7	*/
	public static final int TYPE_PC_IE7 = 0;
	/** FireFox 3.0	*/
	public static final int TYPE_PC_FIREFOX3 = 1;
	/** Softbank 812SH	*/
	public static final int TYPE_SOFTBANK_812SH = 2;
	/** DOCOMO SO706i	*/
	public static final int TYPE_DOCOMO_SO706i = 3;

	private static final HttpHeader UNKNOWN;
	private static final HttpHeader PC_IE7;
	private static final HttpHeader PC_FIREFOX3;
	private static final HttpHeader SOFTBANK_812SH;
	private static final HttpHeader DOCOMO_SO706i;

	static {
		UNKNOWN = new HttpHeader();

		PC_IE7 = new HttpHeader();
		PC_IE7.setTemplateType(TYPE_PC_IE7);
		PC_IE7.setParameter(HeaderNames.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; InfoPath.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022)");
		PC_IE7.setParameter(HeaderNames.ACCEPT_ENCODING, "gzip, deflate");
		PC_IE7.setParameter(HeaderNames.ACCEPT_LANGUAGE, "ja");
		PC_IE7.setParameter(HeaderNames.ACCEPT, "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, ");
		PC_IE7.setParameter(HeaderNames.UA_CPU, "x86");

		PC_FIREFOX3 = new HttpHeader();
		PC_FIREFOX3.setTemplateType(TYPE_PC_FIREFOX3);
		PC_FIREFOX3.setParameter(HeaderNames.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; ja; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8");
		PC_FIREFOX3.setParameter(HeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		PC_FIREFOX3.setParameter(HeaderNames.ACCEPT_LANGUAGE, "ja,en-us;q=0.7,en;q=0.3");
		PC_FIREFOX3.setParameter(HeaderNames.ACCEPT_ENCODING, "gzip,deflate");
		PC_FIREFOX3.setParameter(HeaderNames.ACCEPT_CHARSET, "Shift_JIS,utf-8;q=0.7,*;q=0.7");
		PC_FIREFOX3.setParameter(HeaderNames.KEEP_ALIVE, "300");

		SOFTBANK_812SH = new HttpHeader();
		SOFTBANK_812SH.setTemplateType(TYPE_SOFTBANK_812SH);
		SOFTBANK_812SH.setParameter(HeaderNames.USER_AGENT, "SoftBank/1.0/821SH/SHJ001 Browser/NetFront/3.4 Profile/MIDP-2.0 Configuration/CLDC-1.1");
		SOFTBANK_812SH.setParameter(HeaderNames.ACCEPT_ENCODING, "identity");
		SOFTBANK_812SH.setParameter(HeaderNames.ACCEPT_LANGUAGE, "ja-JP;q=1.0");
		SOFTBANK_812SH.setParameter(HeaderNames.ACCEPT_CHARSET, "utf-8,euc-jp,iso-10646-ucs-2,iso-2022-jp,iso-8859-1,shift_jis,us-ascii,utf-16");
		SOFTBANK_812SH.setParameter(HeaderNames.ACCEPT, "*/*");
		SOFTBANK_812SH.setParameter(HeaderNames.MAX_FORWARDS, "10");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_MSNAME, "821SH");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_UID, "a2tXLKCcOqajbkFL");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_DISPLAY, "240*400");
		SOFTBANK_812SH.setParameter(HeaderNames.X_S_DISPLAY_INFO, "234*350/23*17/TB");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_REGION, "44020");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_COLOR, "C262144");
		SOFTBANK_812SH.setParameter(HeaderNames.X_JPHONE_SMAF, "40/pcm/grf/rs");

		DOCOMO_SO706i = new HttpHeader();
		DOCOMO_SO706i.setTemplateType(TYPE_DOCOMO_SO706i);
		DOCOMO_SO706i.setParameter(HeaderNames.USER_AGENT, "DoCoMo/2.0 SO706i(c100;TB;W24H16)");
		DOCOMO_SO706i.setParameter(HeaderNames.MAX_FORWARDS, "10");
	}

	/**
	 * 特定のブラウザを偽装したリクエストヘッダを作成する。
	 *
	 * @param type
	 * @return 引数の定数に対応するHttpHeaderのテンプレート
	 */
	public static HttpHeader createTemplate(int type) {
		switch (type) {
		case TYPE_UNKNOWN:
			return UNKNOWN.clone();
		case TYPE_PC_IE7:
			return PC_IE7.clone();
		case TYPE_PC_FIREFOX3:
			return PC_FIREFOX3.clone();
		case TYPE_SOFTBANK_812SH:
			return SOFTBANK_812SH.clone();
		case TYPE_DOCOMO_SO706i:
			return DOCOMO_SO706i.clone();
		}
		return null;
	}

}
