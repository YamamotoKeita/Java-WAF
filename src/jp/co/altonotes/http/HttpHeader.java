package jp.co.altonotes.http;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.util.Checker;

/**
 * Httpのヘッダを表すクラス。
 * リクエストヘッダ、レスポンスヘッダ両方の機能を持つ。
 *
 * @author Yamamoto Keita
 *
 */
public class HttpHeader implements Cloneable {
	private Map<String, List<String>> valueMap;
	private int templateType = RequestHeaderFactory.TYPE_UNKNOWN;

	/**
	 * コンストラクタ。
	 */
	public HttpHeader() {
		valueMap = new LinkedHashMap<String, List<String>>();
	}


	/**
	 * コンストラクタ。
	 * サーバーから送られたレスポンスヘッダを元にインスタンスを作成する。
	 *
	 * @param con レスポンスヘッダを取得するHttpURLConnectionインスタンス
	 */
	public HttpHeader(HttpURLConnection con) {
		valueMap = con.getHeaderFields();
	}

	/**
	 * コンストラクタ。
	 * クライアントから送られたリクエストヘッダを元にインスタンスを作成する。
	 *
	 * @param req リクエストヘッダを取得するHttpServletRequestインスタンス
	 */
	@SuppressWarnings("unchecked")
	public HttpHeader(HttpServletRequest req) {
		valueMap = new HashMap<String, List<String>>();
		Enumeration<String> names = req.getHeaderNames();
		String name = null;
		Enumeration<String> values = null;
		ArrayList<String> valList = null;

		while (names.hasMoreElements()) {
			name = names.nextElement();
			values = req.getHeaders(name);

			valList = new ArrayList<String>();
			while (values.hasMoreElements()) {
				valList.add(values.nextElement());
			}
			valueMap.put(name, valList);
		}
	}

	/**
	 * ヘッダにパラメータを追加する。
	 *
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, String value) {
		ArrayList<String> valList = new ArrayList<String>();
		valList.add(value);
		remove(key);
		valueMap.put(key, valList);
	}

	/**
	 * ヘッダにパラメータリストを追加する。
	 *
	 * @param key
	 * @param valList
	 */
	public void setParameterList(String key, ArrayList<String> valList) {
		remove(key);
		valueMap.put(key, valList);
	}

	/**
	 * 指定したキーのパラメータを削除する。
	 *
	 * @param key
	 */
	public void remove(String key) {
		if (key == null) {
			return;
		}
		//キーの大文字、小文字を区別せず削除するため既存キーと引数を比較
		Set<String> mkeys = valueMap.keySet();
		for (String mkey : mkeys) {
			if (mkey != null && mkey.equalsIgnoreCase(key)) {
				valueMap.remove(mkey);
				return;
			}
		}
	}

	/**
	 * refresh属性に指定された更新先のURLを取得する。
	 *
	 * @return refresh属性に指定されたURL
	 */
	public String getRfreshURL() {
		//0; url=http://frog.raindrop.jp
		String value = getParameter(HeaderNames.REFRESH);
		if (value == null) {
			return null;
		}
		String[] params = value.split(";");
		for (String param : params) {
			param = param.trim();
			if (param.toLowerCase().startsWith("url=")) {
				return param.substring(4);
			}
		}
		return null;
	}

	/**
	 * 指定名のパラメータを取得する。
	 * パラメータが配列の場合は、カンマ区切りの文字列として返す。
	 *
	 * @param name
	 * @return 引数に指定したパラメーターの値
	 */
	public String getParameter(String name) {
		List<String> vals = getParameterList(name);
		if (vals == null) {
			return null;
		}

		StringBuilder param = new StringBuilder(50);
		for (int i = 0; i < vals.size(); i++) {
			if (i != 0) {
				param.append(",");
			}
			param.append(vals.get(i));
		}
		return param.toString();
	}

	/**
	 * 指定名のパラメータ一覧を取得する。
	 *
	 * @param name
	 * @return 引数に指定したパラメーターのリスト
	 */
	public List<String> getParameterList(String name) {
		if (name == null) {
			return valueMap.get(null);
		}

		//キーの大文字、小文字を区別せず取得するため既存キーと引数を比較
		Set<String> keys = valueMap.keySet();
		for (String key : keys) {
			if (key != null && key.equalsIgnoreCase(name)) {
				return valueMap.get(key);
			}
		}
		return null;
	}

	/**
	 * 全てのパラメータEntryを取得する。
	 *
	 * @return 全てのパラメータを保持するEntryの配列
	 */
	@SuppressWarnings("unchecked")
	public Entry<String, List<String>>[] getEntries() {
		Set<Entry<String, List<String>>> entrySet = valueMap.entrySet();
		Entry<String, List<String>>[] entries = new Entry[entrySet.size()];
		return entrySet.toArray(entries);
	}

	/**
	 * Content-Typeがhtmlまたはxhtmlか判定する。
	 *
	 * @return Content-Typeがhtmlまたはxhtmlの場合<code>true</code>
	 */
	public boolean isHtml() {
		String type = getParameter(HeaderNames.CONTENT_TYPE);
		if (type != null) {
			String[] params = type.split(";");
			for (String param : params) {
				if (param.trim().equalsIgnoreCase("text/html") || param.trim().equalsIgnoreCase("application/xhtml+xml")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Content-Typeの文字コードを取得する。
	 *
	 * @return Content-Typeの文字コード
	 */
	public String getCharset() {
		String type = getParameter(HeaderNames.CONTENT_TYPE);
		if (type != null) {
			String[] params = type.split(";");
			for (String param : params) {
				param = param.trim();
				//大文字、小文字を区別せず判定する
				if (param.toLowerCase().startsWith("charset=")) {
					return param.substring(8, param.length());
				}
			}
		}
		return null;
	}

	/**
	 * HTTPのステータスコードを取得する。
	 * "HTTP/1.x 200 OK"などをパースしてコード部分を取り出す。
	 *
	 * @return HTTPのステータスコード
	 */
	public String getStatusCode() {
		String allStatus = getParameter(null);
		if (allStatus == null) {
			return null;
		}

		String[] statusArray = allStatus.split(" ");
		for (String status : statusArray) {
			if (status.length() == 3 && Checker.isNumber(status)) {
				return status;
			}
		}
		return null;
	}

	/**
	 * システムに文字列として出力する。
	 *
	 */
	public void print() {
		System.out.print(toString());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String key = null;
		List<String> values = null;
		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			key = entry.getKey();
			values = entry.getValue();
			for (String val : values) {
				if (key != null) {
					buf.append(key+": ");
				}
				buf.append(val);
				buf.append("\n");
			}
		}

		return buf.toString();
	}

	/**
	 * クライアントへのレスポンスにこのヘッダを適用する。
	 *
	 * @param resp
	 */
	public void write(HttpServletResponse resp) {
		String code = getStatusCode();
		//ステータスコード200(正常終了)以外の場合
		if (code != null && !"200".equals(code)) {
			resp.setStatus(Integer.parseInt(code));
		}

		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			if (entry.getKey() == null) {
				continue;
			}

			List<String> valList = entry.getValue();
			for (int i = 0; i < valList.size(); i++) {
				if (i == 0) {
					resp.setHeader(entry.getKey(), valList.get(i));
				} else {
					resp.addHeader(entry.getKey(), valList.get(i));
				}
			}
		}
	}

	/**
	 * Set-Cookie, Set-Cookie2の値を全て取り出す。
	 *
	 * @return Set-Cookie, Set-Cookie2の値
	 */
	public String[] getResponseCookies() {
		List<String> all = new ArrayList<String>();
		List<String> cookie1 = getParameterList(HeaderNames.SET_COOKIE);
		List<String> cookie2 = getParameterList(HeaderNames.SET_COOKIE2);

		if (cookie1 != null) {
			for (String value : cookie1) {
				all.add(value);
			}
		}
		if (cookie2 != null) {
			for (String value : cookie2) {
				all.add(value);
			}
		}

		return all.toArray(new String[all.size()]);
	}

	/**
	 * Content-Encodingに指定されたレスポンスデータの圧縮方式を取得する。
	 *
	 * @return Content-Encodingに指定されたレスポンスデータの圧縮方式
	 */
	public String getContentEncoding() {
		String encoding = getParameter(HeaderNames.CONTENT_ENCODING);
		if (encoding != null) {
			return encoding.trim();
		} else {
			return null;
		}
	}

	/**
	 * ヘッダの文字数を取得する。
	 *
	 * @return ヘッダの文字数
	 */
	public int size() {
		return toString().length();
	}

	/**
	 * リクエストヘッダーのテンプレートタイプを取得する。
	 *
	 * @return リクエストヘッダーのテンプレートタイプ
	 */
	public int getTemplateType() {
		return templateType;
	}

	/**
	 * リクエストヘッダーのテンプレートタイプを設定する。<br>
	 * テンプレートを設定することで、IEやFireFoxや携帯端末などからのリクエストに見せかけることができる。<br>
	 * 引数に指定する値は<code>RequestHeaderFactory</code>クラスを参照。
	 *
	 */
	protected void setTemplateType(int templateType) {
		this.templateType = templateType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public HttpHeader clone() {
		HttpHeader clone = null;

		clone = new HttpHeader();
		clone.templateType = templateType;
		
		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			//シャローコピーなのであしからず
			ArrayList<String> valueList = new ArrayList<String>(entry.getValue());
			clone.setParameterList(entry.getKey(), valueList);
		}

		return clone;
	}

}
