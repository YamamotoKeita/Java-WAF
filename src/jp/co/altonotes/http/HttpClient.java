package jp.co.altonotes.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.altonotes.html.HTMLData;
import jp.co.altonotes.util.TextUtils;


/**
 * HTTP通信を行うクライアント。
 *
 * @author Yamamoto Keita
 *
 */
public class HttpClient {
	private CookieContainer cookieContainer = new CookieContainer();
	private String currentURL;
	private HttpResponse response;
	private String responseCharset;
	private String requestCharset;
	private byte[] byteData;
	private HTMLData htmlData;
	private int mHeaderType = RequestHeaderFactory.TYPE_UNKNOWN;
	private HashMap<String, ArrayList<String>> mParameters = new HashMap<String, ArrayList<String>>();
	private boolean mDisableSSLVerify = true;

	/**
	 * コンストラクタ
	 */
	public HttpClient() {
		mHeaderType = RequestHeaderFactory.TYPE_PC_IE7;
	}

	/**
	 * コンストラクタ
	 *
	 * @param headerType
	 */
	public HttpClient(int headerType) {
		mHeaderType = headerType;
	}

	/**
	 * ロケーションを直接指定してURLにアクセスする。
	 *
	 * @param url
	 * @throws IOException
	 */
	public void accessByLocation(String url) throws IOException {
		access(url, false, null);
	}

	/**
	 * リンクからURLにアクセスする。
	 * ヘッダに現在ページがRefererとしてセットされる。
	 *
	 * @param url
	 * @throws IOException
	 */
	public void accessByLink(String url) throws IOException {
		access(url, true, null);
	}

	/**
	 * フォームを送信する。
	 *
	 * @param url
	 * @throws IOException
	 */
	public void submitForm(String url) throws IOException {
		access(url, true, getPostParameter());
	}

	/**
	 * フォームを入力する。
	 *
	 * @param name
	 * @param value
	 */
	public synchronized void fillForm(String name, String value) {
		ArrayList<String> values = mParameters.get(name);
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
		mParameters.put(name, values);
	}

	/**
	 * URLにアクセスする。
	 *
	 * @param url
	 * @throws IOException
	 */
	private void access(String url, boolean setReferer, byte[] postData) throws IOException {
		HttpConnector con = null;
		HttpHeader header = null;
		this.currentURL = url;

		if (url == null || url.length() == 0) {
			throw new IllegalArgumentException("url に null または空文字が指定されました");
		}

		try {
			con = new HttpConnector(url);
			if (mDisableSSLVerify) {
				con.enableSSLVerify(false);
			}
			header = RequestHeaderFactory.createTemplate(mHeaderType);
			if (setReferer && url != null) {
				header.setParameter(HeaderNames.REFERER, url);
			}

			String cookieValue = null;
			if (url != null) {
				cookieValue = cookieContainer.getCookieString(url);
			}
			if (cookieValue != null) {
				header.setParameter(HeaderNames.COOKIE, cookieValue);
			}
			con.setHeader(header);
			if (postData != null) {
				this.response = con.post(postData);
			} else {
				this.response = con.get();
			}
			cookieContainer.saveCookieStrings(url, response.getCookieValues());
		} catch (MalformedURLException e) {
			throw new IOException(url + "がURLとして認識できない。");
		}
		afterProcess(postData);
	}

	/**
	 * レスポンス取得後の処理を行う。
	 * @throws IOException
	 */
	private void afterProcess(byte[] postData) throws IOException {
		HttpHeader responseHeader = response.getHeader();

		//リダイレクト
		String code = responseHeader.getStatusCode();
		// 300 Multiple Choices
		// 301 Moved Permanently
		// 302 Found (Method変更無し)
		// 303 See Other (GET)
		// 304 Not Modified
		// 305 Use Proxy (Method変更無し)
		if (code != null && code.startsWith("3")) {
			String location = responseHeader.getParameter(HeaderNames.LOCATION);
			if (location != null) {
				if (code.equals("303") || postData == null) {
					access(location, false, null);
				} else {
					access(location, false, postData);
				}
				return;
			}
		}

		//更新
		String refresh = responseHeader.getRfreshURL();
		if (refresh != null) {
			String protocol = TextUtils.parseProtocol(currentURL);
			String host = TextUtils.parseHostName(currentURL, true);
			accessByLocation(protocol + "://" + host + refresh);
			return;
		}

		//HTML取得
		byteData = response.getDecodedBody();//Content-Encodingの処理
		responseCharset = response.getCharset();
		htmlData = new HTMLData(byteData, responseCharset);

		//更新
		refresh = htmlData.getMetaHeader().getRfreshURL();
		if (refresh != null) {
			String protocol = TextUtils.parseProtocol(currentURL);
			String host = TextUtils.parseHostName(currentURL, true);
			accessByLocation(protocol + "://" + host + refresh);
			return;
		}

		HttpHeader metaHeader = htmlData.getMetaHeader();

		if (metaHeader.getCharset() != null) {
			responseCharset = metaHeader.getCharset();
		}

		// HTMLのMETAタグからSet-Cookie、Set-Cookie2を取得
		cookieContainer.saveCookieStrings(currentURL, metaHeader.getResponseCookies());

		//フォームクリア
		mParameters = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * SSL証明書の正当性検証の有効/無効を切り替える。
	 * デフォルトでは検証が無効な状態になっている。
	 *
	 * @param flag
	 */
	public void enableSSLVerify(boolean flag) {
		mDisableSSLVerify = !flag;
	}


	/**
	 * レスポンスを文字列として取得する。
	 *
	 * @return HTTPレスポンスの文字列
	 */
	public String getResponseString() {
		return htmlData.toString();
	}

	/**
	 * レスポンスをバイナリデータとして取得する。
	 *
	 * @return HTTPレスポンスのバイナリデータ
	 */
	public byte[] getBinaryData() {
		return response.getDecodedBody();
	}

	/**
	 * HttpResponseを取得する。
	 *
	 * @return HTTPレスポンス
	 */
	public HttpResponse getResponse() {
		return response;
	}

	/**
	 * POST送信するデータを取得する。
	 *
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getPostParameter() throws UnsupportedEncodingException {
		String charset = getRequestCharset();
		if (charset == null) {
			charset = responseCharset;
			if (charset == null) {
				charset = HTMLData.DEFAULT_CHARSET;
			}
		}

		StringBuffer params = new StringBuffer();

		Set<Entry<String, ArrayList<String>>> set = mParameters.entrySet();
		for (Entry<String, ArrayList<String>> entry : set) {
			String key = URLEncoder.encode(entry.getKey(), charset);
			ArrayList<String> values = entry.getValue();
			for (String value : values) {
				params.append(key + "=" + URLEncoder.encode(value, charset) + "&");
			}
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);//最後の&を消す
		}
		return params.toString().getBytes(charset);
	}

	/**
	 * 保持するCookieを配列で全て取得する。
	 *
	 * @return 保持するCookieの配列
	 */
	public HttpCookie[] getCookies() {
		return cookieContainer.getCookies();
	}

	/**
	 * @return the requestCharset
	 */
	public String getRequestCharset() {
		return requestCharset;
	}

	/**
	 * @param requestCharset the requestCharset to set
	 */
	public void setRequestCharset(String requestCharset) {
		this.requestCharset = requestCharset;
	}
}
