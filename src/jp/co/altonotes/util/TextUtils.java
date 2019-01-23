package jp.co.altonotes.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 値から特定フォーマットのStringへの変換および、
 * 特定フォーマットのStringから値への取得を行う。
 *
 * @author Yamamoto Keita
 *
 */
public class TextUtils {

	/**
	 * 文字列中の「&lt;」「&gt;」「 &amp;」「&nbsp;」「&quot;」をHTML特殊文字に変換する。
	 *
	 * @param str
	 * @return HTMLエスケープされた文字列
	 */
	public static String htmlEscape(String str) {
		String res = str;

		// & はエスケープシーケンスとして使用するので一番先に変換する必要がある
		res = res.replace("&", "&amp;");

		res = res.replace("<", "&lt;");
		res = res.replace(">", "&gt;");
		res = res.replace(" ", "&nbsp;");
		res = res.replace("\"", "&quot;");

		return res;
	}

	/**
	 * 文字列の配列を連結して一つの文字列にする。
	 *
	 * @param strs
	 * @param separater
	 * @return 連結された文字列
	 */
	public static String combine(String[] strs, String separater) {
		if (strs == null || strs.length == 0) {
			return null;
		}else if (strs.length == 1) {
			return strs[0];
		}

		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str + separater);
		}
		return sb.substring(0, sb.length() - separater.length());
	}

	/**
	 * 文字列のListを連結して一つの文字列にする。
	 *
	 * @param strs
	 * @param separater
	 * @return リストを連結した文字列
	 */
	public static String combine(List<String> strs, String separater) {
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str + separater);
		}
		return sb.substring(0, sb.length() - separater.length());
	}

	/**
	 * 指定した長さの連続した半角スペースを返す。
	 *
	 * @param length
	 * @return 指定した長さの連続した半角スペース
	 */
	public static String blank(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * 指定の長さまで左端に特定の文字をつけた文字列を作成する。
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return 左端を指定の文字で埋めた文字列
	 */
	public static String padLeft(long source, int size, char padding) {
		String str = String.valueOf(source);
		return padLeft(str, size, padding);
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
	 * 文字列末尾にある、[]に囲まれた数値をintとして取得する。<br>
	 * 例）array[1]の場合、1が返る。<br>
	 * <br>
	 * 以下の場合は-1を返す。
	 * <ul>
	 * <li>末尾に[]が存在しない</li>
	 * <li>[]内を数値として認識できない</li>
	 * <li>[]内が負の数</li>
	 * </ul>
	 *
	 * @param str
	 * @return 文字列末尾の[]に囲まれた数値
	 */
	public static int parseArrayIndex(String str) {
		int aryIdx = -1;
		String idxStr = null;
		int head = 0;
		int tail = 0;

		head = str.indexOf("[");
		if (head == -1) {
			return -1;
		}

		tail = str.indexOf("]", head + 2);
		if (tail != str.length() - 1) {
			return -1;
		}

		idxStr = str.substring(head + 1, tail);
		if (Checker.isNumber(idxStr)) {
			aryIdx = Integer.parseInt(idxStr);
		} else {
			return -1;
		}

		if (0 <= aryIdx) {
			return aryIdx;
		} else {
			return -1;
		}
	}

	/**
	 * URLからプロトコルを取得する。
	 *
	 * @param url
	 * @return プロトコル
	 */
	public static String parseProtocol(String url) {
		int idx = url.indexOf("://");
		if (idx == -1) {
			return null;
		}
		return url.substring(0, idx);
	}

	/**
	 * URLからホスト名を取得する。
	 *
	 * @param url
	 * @param getPort 
	 * @return ホスト名
	 */
	public static String parseHostName(String url, boolean getPort) {
		String name = null;
		if (url.startsWith("http://")) {
			name = url.substring(7);
		} else if (url.startsWith("https://")) {
			name = url.substring(8);
		} else {
			return null;
		}

		int idx = name.indexOf("/");
		if (idx != -1) {
			name = name.substring(0, idx);
		}
		if (!getPort) {
			idx = name.indexOf(":");
			if (idx != -1) {
				name = name.substring(0, idx);
			}
		}
		return name;
	}

	/**
	 * URLからホスト名、クエリを取り除いたドキュメントパスを取り出す。
	 *
	 *
	 * @param url
	 * @return ドキュメントパス
	 */
	public static String parseDocumentPath(String url) {
		String path = null;
		if (url.startsWith("http://")) {
			path = url.substring(7);
		} else if (url.startsWith("https://")) {
			path = url.substring(8);
		} else {
			return null;
		}

		int idx = path.indexOf("/");
		if (idx == -1) {
			return "";
		}
		path = path.substring(idx);
		idx = path.indexOf("?", path.lastIndexOf("/"));
		if (idx != -1) {
			path = path.substring(0, idx);
		}
		return path;
	}

	/**
	 * 最後のスラッシュ以降の文字列を取得する。
	 * スラッシュが含まれない場合、引数をそのまま返す。
	 *
	 * @param path
	 * @return パスの最後のスラッシュ以降の文字列
	 */
	public static String getLastPath(String path) {
		int idx = path.lastIndexOf("/");
		if (idx == -1) {
			return path;
		}
		path = path.substring(idx + 1);
		idx = path.indexOf("?", idx);
		if (idx != -1) {
			path = path.substring(0, idx);
		}
		return path;
	}

	/**
	 * ファイル名やパスから拡張子（末尾のドット以降の文字列）を取得する。
	 *
	 * @param name
	 * @return 拡張子
	 */
	public static String getExtention(String name) {
		int idx = name.lastIndexOf('.');
		if (idx == -1) {
			return null;
		} else {
			return name.substring(idx + 1, name.length());
		}
	}

	/**
	 * 文字列の右端から特定の文字を取りのぞく。
	 *
	 * @param str
	 * @param c
	 * @return 右端の特定の文字をのぞいた文字列
	 */
	public static String rightTrim(String str, char c) {
		char[] chars = str.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			if (chars[i] != c) {
				return str.substring(0, i+1);
			}
		}
		return "";
	}

	/**
	 * 文字列の左端から特定の文字を取りのぞく。
	 *
	 * @param str
	 * @param c
	 * @return 左端の特定の文字をのぞいた文字列
	 */
	public static String leftTrim(String str, char c) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != c) {
				return str.substring(0, chars.length - i);
			}
		}
		return "";
	}

	/**
	 * 文字列の両端から複数種類の文字列を取りのぞく。
	 *
	 * @param str
	 * @param excludes
	 * @return トリムした文字列
	 */
	//TODO System.arraycopy を使って実装する
//	public static String trim(String str, String[] excludes) {
//		// 引数のデータをぶっ壊さないようコピる
//		String[] copy = Arrays.copyOf(excludes, excludes.length);
//
//		// 例えば "a"と"ab"を取り除く場合、"a"を先に取り除くと"b"だけが残り"ab"を取り除けない。
//		// そのため文字長が長い順にマッチするよう並べ替える
//		Arrays.sort(copy, new StringLengthComparatorASC());
//		excludes = copy;
//
//		int start = 0;
//		boolean goNext = true;
//		while (goNext) {
//			goNext = false;
//			for (int i = 0; i < excludes.length; i++) {
//				if (str.startsWith(excludes[i], start)) {
//					start += excludes[i].length();
//					goNext = true;
//					break;
//				}
//			}
//		}
//
//		int end = str.length();
//		goNext = true;
//		while (goNext) {
//			goNext = false;
//			for (int i = 0; i < excludes.length; i++) {
//				int len = excludes[i].length();
//				if (str.indexOf(excludes[i], end - len) == end - len) {
//					end -= len;
//					goNext = true;
//					break;
//				}
//			}
//		}
//
//		return str.substring(start, end);
//	}

	/**
	 * 文字列を指定した文字コードでバイト配列にする。
	 * UnsupportedEncodingExceptionをthrowせず、変換に失敗した場合nullを返す。
	 *
	 * @param str
	 * @param charset
	 * @return 文字列を表すバイト配列
	 */
	public static byte[] getBytes(String str, String charset) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes(charset);
		} catch (UnsupportedEncodingException ignoted) {}
		return bytes;
	}

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
	 * Throwableからスタックトレースを文字列として取り出す。
	 *
	 * @param ex
	 * @return スタックトレースの文字列
	 */
	public static String stackTraceToString(Throwable ex) {
		StringWriter sw = null;
		PrintWriter  pw = null;

		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			return sw.toString();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 半角カタカナ、全角カタカナを全角ひらがなに変換する
	 *
	 * @param str
	 * @return 全角ひらがなに変換した文字列
	 */
	public static String toZenkakuHiragana(String str) {
		//TODO 実装する
		return null;
	}

	/**
	 * 半角カタカナ、全角カタカナを全角ひらがなに変換する
	 * @param c
	 * @return 全角ひらがなに変換した文字
	 */
	public static char toZenkakuHiragana(char c) {
		if (12354 <= c && c <= 12435) {
			return (char)(c + 96);
		} else {
			return c;
		}
	}

	/**
	 * 半角カタカナ、全角ひらがなを全角カタカナに変換する
	 *
	 * @param str
	 * @return 全角カタカナに変換した文字列
	 */
	public static String toZenkakuKatakana(String str) {
		//TODO 実装する
		return null;
	}

	/**
	 * 半角かなに変換する
	 *
	 * @param str
	 * @return 半角かなに変換した文字列
	 */
	public static String toHankakuKana(String str) {
		//TODO 実装する
		return null;
	}

	/**
	 * X / _ / X「 ゆのがシステムにメッセージを出力するよ 」
	 *
	 * @param obj
	 */
	public static void yunoOut(Object obj) {
		System.out.print("X / _ / X「 ");
		System.out.print(obj);
		System.out.println(" 」");
	}

}
