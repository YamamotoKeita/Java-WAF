package jp.co.altonotes.http;

import java.io.Serializable;
import java.util.ArrayList;

import jp.co.altonotes.util.TextUtils;

/**
 * Cookieの管理を行うクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class CookieContainer implements Serializable {
	private static final long serialVersionUID = -61518887706011936L;

	private ArrayList<HttpCookie> cookieList = new ArrayList<HttpCookie>();

	/**
	 * Cookieをセットする。
	 *
	 * @param cookies
	 */
	public void setCookies(HttpCookie[] cookies) {
		for (HttpCookie cookie : cookies) {
			cookieList.add(cookie);
		}
	}

	/**
	 * 保持するCookieを配列で全て取得する。
	 *
	 * @return 保持するCookieの配列
	 */
	public HttpCookie[] getCookies() {
		return cookieList.toArray(new HttpCookie[cookieList.size()]);
	}

	/**
	 * Cookieを保存する。
	 *
	 * @param url
	 * @param values
	 */
	public void saveCookieStrings(String url, String[] values) {
		HttpCookie[] cookies = HttpCookie.createCookies(url, values);
		for (HttpCookie cookie : cookies) {
			//同じものがあれば削除
			for (int i = 0; i < cookieList.size(); i++) {
				if (cookieList.get(i).equals(cookie)) {
					cookieList.remove(i);
					break;
				}
			}
			cookieList.add(cookie);
		}
	}

	/**
	 * 指定したURLに対して送信すべきCookieを文字列として取得する。
	 *
	 * @param url
	 * @return 引数のURLに対して送信するべきCookieの文字列
	 */
	public String getCookieString(String url) {
		String host = TextUtils.parseHostName(url, false);
		String path = TextUtils.parseDocumentPath(url);
		StringBuilder result = new StringBuilder(50);
		for (int i = 0; i < cookieList.size(); i++) {
			HttpCookie cookie = cookieList.get(i);
			//期限切れを削除
			if (cookie.hasExpired()) {
				cookieList.remove(i);
				i--;
				continue;
			}

			if (!HttpCookie.domainMatches(cookie.getDomain(), host) || !path.startsWith(cookie.getPath())) {
				continue;
			}

			if (i != 0) {
				result.append("; ");
			}
			result.append(cookie.toString());
		}
		if (result.length() == 0) {
			return null;
		} else {
			return result.toString();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("/******* Instance of CookieContainer contains... ********/\n");
		for (HttpCookie cookie : cookieList) {
			temp.append("domain:" + cookie.getDomain() + ", path:" + cookie.getPath() + ", cookie:" + cookie.toString() + "\n");
		}
		temp.deleteCharAt(temp.length() - 1);
		return temp.toString();
	}
}
