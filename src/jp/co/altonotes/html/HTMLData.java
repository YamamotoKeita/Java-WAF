package jp.co.altonotes.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.http.HttpHeader;

/**
 * HTMLデータを表すクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class HTMLData {

	/** デフォルトで使用する文字コード */
	public static final String DEFAULT_CHARSET = "windows-31J";

	private static final Pattern PATTERN_META = Pattern.compile("<meta +[^>]*>");
	private static final Pattern PATTERN_HTTP_EQUIV = Pattern.compile("http-equiv *= *\"[^\"]*\"");
	private static final Pattern PATTERN_CONTENT = Pattern.compile("content *= *\"[^\"]*\"");

	private String stringByHeaderCharset;
	private String stringByMetaCharset;
	private String headerCharset;
	private String metaCharset;
	private HttpHeader metaHeader = new HttpHeader();

	/**
	 * コンストラクタ
	 * @param data 
	 * @param headerCharset 
	 */
	public HTMLData(byte[] data, String headerCharset) {
		this.headerCharset = headerCharset;
		setStringByHeaderCharset(data);
		parseMetaTags();
		setStringByMetaCharset(data);
	}

	/**
	 * ヘッダのcharsetを元にした文字列をセットする。
	 *
	 * @param data
	 */
	private void setStringByHeaderCharset(byte[] data) {
		if (headerCharset != null) {
			try {
				stringByHeaderCharset = new String(data, headerCharset);
			} catch (UnsupportedEncodingException ignored) {}//非対応文字コードの場合デフォルト文字コードを使用。
		}
		if (stringByHeaderCharset == null) {
			try {
				stringByHeaderCharset = new String(data, DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException ignored) {}//デフォルトはどこでも対応している文字コードにし、エラーを想定しない。
		}
	}

	/**
	 * METAタグを解析する。
	 */
	private void parseMetaTags() {
		String htmlWithoutComment = deleteComment(stringByHeaderCharset.toLowerCase());
		Matcher mtr = PATTERN_META.matcher(htmlWithoutComment);
		ArrayList<String> metas = new ArrayList<String>();

		while (mtr.find()) {
			metas.add(mtr.group());
		}

		for (String str : metas) {
			String key = null;
			String value = null;
			mtr = PATTERN_HTTP_EQUIV.matcher(str);
			if (mtr.find()) {
				key = mtr.group();
				key = key.substring(key.indexOf('"') + 1, key.lastIndexOf('"'));
			}
			mtr = PATTERN_CONTENT.matcher(str);
			if (mtr.find()) {
				value = mtr.group();
				value = value.substring(value.indexOf('"') + 1, value.lastIndexOf('"'));
			}

			if (key != null && value != null) {
				metaHeader.setParameter(key, value);
			}
		}
		metaCharset = metaHeader.getCharset();
	}

	/**
	 * METAタグのcharsetを元にした文字列をセットする。
	 */
	private void setStringByMetaCharset(byte[] data) {
		//METAのcharsetがヘッダcharsetと異なる場合
		if (metaCharset != null && !metaCharset.equalsIgnoreCase(headerCharset)) {
			try {
				stringByMetaCharset = new String(data, metaCharset);
			} catch (UnsupportedEncodingException ignored) {}
		}
	}

	/**
	 * HTML文字列内の<!-- -->に囲まれたコメントを削除する。
	 *
	 * @param html
	 * @return
	 */
	private static String deleteComment(String html) {
		int head = 0;
		int tail = 0;
		StringBuffer buf = new StringBuffer(html.length());

		while ((tail = html.indexOf("<!--", head)) >= 0) {
			buf.append(html.substring(head, tail));
			head = tail;
			tail = html.indexOf("-->", head + 4);
			if (tail == -1) {
				break;
			} else {
				head = tail + 3;
			}
		}
		buf.append(html.substring(head, html.length()));
		return buf.toString();
	}

	/**
	 * HTML内のMETAタグによるHTTPヘッダを返す。
	 *
	 * @return HTML内のMETAタグに指定されたHTTPヘッダ
	 */
	public HttpHeader getMetaHeader() {
		return metaHeader;
	}

	/**
	 * METAタグのcharsetを取得する。
	 *
	 * @return HTML内のMETAタグに指定された文字コード
	 */
	public String getMetaCharset() {
		return metaCharset;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (stringByMetaCharset != null) {
			return stringByMetaCharset;
		} else {
			return stringByHeaderCharset;
		}
	}
}
