package jp.co.altonotes.webapp.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * メッセージのリストを保持するクラス。
 *
 * @author Yamamoto Keita
 *
 */
public final class MessageList implements Iterable<String> {

	private ArrayList<String> list = new ArrayList<String>();
	private String prefix = "<li>";
	private String suffix = "</li>";
	private String header;
	private String footer;

	/**
	 * メッセージを追加する。
	 *
	 * @param message
	 */
	public void add(String message) {
		list.add(message);
	}

	/**
	 * メッセージを保持しているか判定する。
	 *
	 * @return メッセージを保持している場合<code>true</code>
	 */
	public boolean hasMessage() {
		if (0 < list.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return メッセージの数
	 */
	public int size() {
		return list.size();
	}

	/**
	 * メッセージを全て削除する
	 */
	public void clear() {
		list.clear();
	}
	
	/**
	 * @param msg
	 * @return 引数のメッセージがセットされていれば <code>true</code>
	 */
	public boolean contains(String msg) {
		return list.contains(msg);
	}
	
	/**
	 * @return 個別のメッセージの先頭に付与する文字列
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix 個別のメッセージの先頭に付与する文字列
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return 個別のメッセージの末尾に付与する文字列
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix 個別のメッセージの末尾に付与する文字列
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return メッセージリスト全体の先頭に付与する文字列
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header メッセージリスト全体の先頭に付与する文字列
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return メッセージリスト全体の末尾に付与する文字列
	 */
	public String getFooter() {
		return footer;
	}

	/**
	 * @param footer メッセージリスト全体の末尾に付与する文字列
	 */
	public void setFooter(String footer) {
		this.footer = footer;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder(100);

		if (header != null) {
			temp.append(header + "\r\n");
		}

		for (String message : list) {
			if (prefix != null) {
				temp.append(prefix);
			}

			temp.append(message);

			if (suffix != null) {
				temp.append(suffix);
			}
			temp.append("\r\n");
		}

		if (footer != null) {
			temp.append(footer);
		}

		return temp.toString();
	}

	/**
	 * @return メッセージの iterator
	 */
	public Iterator<String> iterator() {
		return list.iterator();
	}

}
