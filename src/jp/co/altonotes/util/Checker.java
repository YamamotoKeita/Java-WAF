package jp.co.altonotes.util;

import java.util.Collection;
import java.util.Map;

/**
 * 文字列などの検証を行う。
 *
 * @author Yamamoto Keita
 *
 */
public class Checker {

	/**
	 * 引数に指定した文字列がnullまたは空文字かどうか判定する。
	 *
	 * @param str
	 * @return 引数の文字列がnullまたは空文字の場合<code>true</code>
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数に指定した文字列がnullまたは空文字でないかどうか判定する。
	 *
	 * @param str
	 * @return 引数に指定した文字列がnullまたは空文字でない場合<code>true</code>
	 */
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	/**
	 * 引数に指定した文字列がnull、空文字または空白か判定する。
	 *
	 * @param str
	 * @return 引数に指定した文字列がnull、空文字または空白の場合<code>true</code>
	 */
	public static boolean isBlank(CharSequence str) {
		if (isEmpty(str)) {
			return true;
		}

		int strLen = str.length();

		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 引数の文字列が空白以外の文字を含むか判定する。
	 *
	 * @param str
	 * @return 引数の文字列が空白以外の文字を含む場合<code>true</code>
	 */
	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	/**
	 * 文字列が全て数字かどうか判定する
	 *
	 * @param str
	 * @return 文字列が全て数字の場合<code>true</code>
	 */
	public static boolean isNumber(String str) {
		if (isNotEmpty(str)) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!Character.isDigit(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が数字かどうか判定する
	 *
	 * @param c
	 * @return 文字が数字の場合<code>true</code>
	 */
	public static boolean isNumber(char c) {
		return Character.isDigit(c);
	}

	/**
	 * 文字列が数値を表すかどうか判定する。
	 * +, -, 小数点を含めてもよい。
	 *
	 * @param str
	 * @return 文字列が数値表す場合<code>true</code>
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 文字列が"http://"または"https://"で始まるか判定する。
	 *
	 * @param str
	 * @return 文字列が"http://"または"https://"で始まる場合<code>true</code>
	 */
	public static boolean isHttpURL(String str) {
		if (str != null) {
			return str.startsWith("http://") || str.startsWith("https://");
		} else {
			return false;
		}
	}

	/**
	 * 文字列が小文字or大文字アルファベットか判定する。
	 *
	 * @param str
	 * @return 文字列が小文字or大文字アルファベットの場合<code>true</code>
	 */
	public static boolean isAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が小文字or大文字アルファベットか判定する。
	 * @param c
	 * @return 文字が小文字or大文字アルファベットの場合<code>true</code>
	 */
	public static boolean isAlpha(char c) {
		return isLowerAlpha(c) || isUpperAlpha(c);
	}

	/**
	 * 文字列が全て小文字アルファベットか判定する。
	 *
	 * @param str
	 * @return 文字列が全て小文字アルファベットの場合<code>true</code>
	 */
	public static boolean isLowerAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isLowerAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が小文字アルファベットか判定する。
	 *
	 * @param c
	 * @return 文字が小文字アルファベットの場合<code>true</code>
	 */
	public static boolean isLowerAlpha(char c) {
		if (97 <= c && c <= 122) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文字列が全て大文字アルファベットか判定する。
	 *
	 * @param str
	 * @return 文字列が全て大文字アルファベットの場合<code>true</code>
	 */
	public static boolean isUpperAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isUpperAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が大文字アルファベットか判定する。
	 *
	 * @param c
	 * @return 文字が大文字アルファベットの場合<code>true</code>
	 */
	public static boolean isUpperAlpha(char c) {
		if (65 <= c && c <= 90) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文字列が全て半角英数字および、キーボードで入力できる記号か判定する。
	 *
	 * @param str
	 * @return 文字列が全て半角英数字および、キーボードで入力できる記号の場合<code>true</code>
	 */
	public static boolean isKeybordCharacter(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isKeybordCharacter(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が半角英数字および、キーボードで入力できる記号か判定する。
	 *
	 * @param c
	 * @return 文字が半角英数字および、キーボードで入力できる記号の場合<code>true</code>
	 */
	public static boolean isKeybordCharacter(char c) {
		if (33 <= c && c <= 126) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文字列が半角カナ（半角濁点、半角半濁点、半角長音含む）か判定する。
	 *
	 * @param str
	 * @return 文字列が半角カナの場合<code>true</code>
	 */
	public static boolean isHankakuKana(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isHankakuKana(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 文字が半角カナ（半角濁点、半角半濁点、半角長音含む）か判定する。
	 *
	 * @param c
	 * @return 文字が半角カナの場合<code>true</code>
	 */
	public static boolean isHankakuKana(char c) {
		if (65383 <= c && c <= 65439) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文字列リストに指定した文字列が含まれるか判定する。
	 *
	 * @param list
	 * @param checked
	 * @return 文字列リストに指定した文字列が含まれる場合<code>true</code>
	 */
	public static boolean contains(Collection<String> list, String checked) {
		return contains(list.toArray(new String[list.size()]), checked, true);
	}

	/**
	 * 文字列配列に指定した文字列が含まれるか判定する。
	 *
	 * @param array
	 * @param checked
	 * @return 文字列配列に指定した文字列が含まれる場合<code>true</code>
	 */
	public static boolean contains(String[] array, String checked) {
		return contains(array, checked, true);
	}

	/**
	 * 文字列リストに指定した文字列が含まれるか判定する。
	 * 大文字小文字を区別しない。
	 *
	 * @param list
	 * @param checked
	 * @return 文字列リストに指定した文字列が含まれる場合<code>true</code>
	 */
	public static boolean containsIgnoreCase(Collection<String> list, String checked) {
		return contains(list.toArray(new String[list.size()]), checked, false);
	}

	/**
	 * 文字列配列に指定した文字列が含まれるか判定する。
	 * 大文字小文字を区別しない。
	 *
	 * @param array
	 * @param checked
	 * @return 文字列配列に指定した文字列が含まれる場合<code>true</code>
	 */
	public static boolean containsIgnoreCase(String[] array, String checked) {
		return contains(array, checked, false);
	}

	/**
	 * 文字列配列に指定した文字列が含まれるか判定する。
	 *
	 * @param array
	 * @param checked
	 * @param caseSensitive
	 * @return
	 */
	private static boolean contains(String[] array, String checked, boolean caseSensitive) {
		boolean hit = false;
		for (String string : array) {
			if (caseSensitive) {
				if (string.equals(checked)) {
					hit = true;
					break;
				}
			} else {
				if (string.equalsIgnoreCase(checked)) {
					hit = true;
					break;
				}
			}
		}
		return hit;
	}

	/**
	 * 配列が空か判定する。
	 *
	 * @param array
	 * @return 配列が空の場合<code>true</code>
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * コレクションが空か判定する。
	 *
	 * @param collection
	 * @return コレクションが空の場合<code>true</code>
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Mapが空か判定する。
	 *
	 * @param map
	 * @return Mapが空の場合<code>true</code>
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * 半角/全角、ひらがな/カタカナを区別せず、２つの文字列の日本語の読み方が同じならtrueを返す。
	 *
	 * @param str1
	 * @param str2
	 * @return 半角/全角、ひらがな/カタカナを区別せず、２つの文字列の日本語の読み方が同じなら<code>true</code>
	 */
	public static boolean soundsSameInJapanese(String str1, String str2) {
		//スペースを抜く
		str1 = str1.replaceAll(" ", "");
		str1 = str1.replaceAll("　", "");
		str2 = str2.replaceAll(" ", "");
		str2 = str2.replaceAll("　", "");

		//TODO 実装する。

		return str1.equals(str2);
	}

}
