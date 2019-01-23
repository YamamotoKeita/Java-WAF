package jp.co.altonotes.unix;

import java.util.ArrayList;
import java.util.List;

/**
 * Linuxコマンドの出力をパースするためのユーティリティー
 *
 * @author Yamamoto Keita
 *
 */
public class ParseUtils {

	/**
	 * 文字列をスペースで分割する
	 * @param src
	 * @return スペースで分割した文字列の配列
	 */
	public static String[] splitBySpace(String src) {
		List<String> list = new ArrayList<String>();
		int head = 0;
		int tail = 0;
		boolean space = true;

		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) == ' ') {
				if (!space) {
					tail = i;
					list.add(src.substring(head, tail));
					space = true;
				}
			} else if (space){
				head = i;
				space = false;
			}
		}

		if (!space) {
			list.add(src.substring(head, src.length()));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 指定の長さまで右端に特定の文字をつけた文字列を作成する。
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return 指定の長さまで右端に特定の文字をつけた文字列
	 */
	public static String padRight(String source, int size, char padding) {
		int srcLen = source.length();
		if (size < srcLen) {
			source = source.substring(0, size);
		} else if (srcLen < size) {
			StringBuilder sb = new StringBuilder(size);
			sb.append(source);
			for (int j = 0; j < size - srcLen; j++) {
				sb.append(padding);
			}
			source = sb.toString();
		}
		return source;
	}

	/**
	 * 指定の長さまで先頭に特定の文字をつけた文字列を作成する。
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return 指定の長さまで左端に特定の文字をつけた文字列
	 */
	public static String padLeft(String source, int size, char padding) {
		int srcLen = source.length();
		if (size < srcLen) {
			source = source.substring(srcLen - size, srcLen);
		} else if (srcLen < size) {
			StringBuilder sb = new StringBuilder(size);
			for (int j = 0; j < size - srcLen; j++) {
				sb.append(padding);
			}
			sb.append(source);
			source = sb.toString();
		}
		return source;
	}

}
