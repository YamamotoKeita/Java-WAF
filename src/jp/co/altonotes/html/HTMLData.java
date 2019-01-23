package jp.co.altonotes.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.http.HttpHeader;

/**
 * HTML�f�[�^��\���N���X�B
 *
 * @author Yamamoto Keita
 *
 */
public class HTMLData {

	/** �f�t�H���g�Ŏg�p���镶���R�[�h */
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
	 * �R���X�g���N�^
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
	 * �w�b�_��charset�����ɂ�����������Z�b�g����B
	 *
	 * @param data
	 */
	private void setStringByHeaderCharset(byte[] data) {
		if (headerCharset != null) {
			try {
				stringByHeaderCharset = new String(data, headerCharset);
			} catch (UnsupportedEncodingException ignored) {}//��Ή������R�[�h�̏ꍇ�f�t�H���g�����R�[�h���g�p�B
		}
		if (stringByHeaderCharset == null) {
			try {
				stringByHeaderCharset = new String(data, DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException ignored) {}//�f�t�H���g�͂ǂ��ł��Ή����Ă��镶���R�[�h�ɂ��A�G���[��z�肵�Ȃ��B
		}
	}

	/**
	 * META�^�O����͂���B
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
	 * META�^�O��charset�����ɂ�����������Z�b�g����B
	 */
	private void setStringByMetaCharset(byte[] data) {
		//META��charset���w�b�_charset�ƈقȂ�ꍇ
		if (metaCharset != null && !metaCharset.equalsIgnoreCase(headerCharset)) {
			try {
				stringByMetaCharset = new String(data, metaCharset);
			} catch (UnsupportedEncodingException ignored) {}
		}
	}

	/**
	 * HTML���������<!-- -->�Ɉ͂܂ꂽ�R�����g���폜����B
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
	 * HTML����META�^�O�ɂ��HTTP�w�b�_��Ԃ��B
	 *
	 * @return HTML����META�^�O�Ɏw�肳�ꂽHTTP�w�b�_
	 */
	public HttpHeader getMetaHeader() {
		return metaHeader;
	}

	/**
	 * META�^�O��charset���擾����B
	 *
	 * @return HTML����META�^�O�Ɏw�肳�ꂽ�����R�[�h
	 */
	public String getMetaCharset() {
		return metaCharset;
	}

	/*
	 * (�� Javadoc)
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
