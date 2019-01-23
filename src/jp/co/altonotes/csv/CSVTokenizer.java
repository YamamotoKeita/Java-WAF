package jp.co.altonotes.csv;

import java.util.NoSuchElementException;

/**
 * 1行のCSV形式のデータを解析し、それぞれの項目に分解するクラス。
 * CSV形式に対応した java.util.StringTokenizer のようなもの。
 *
 * @author Yamamoto Keita
 *
 */
public class CSVTokenizer {
	private String source;                    // 対象となる文字列
	private int currentPosition;              // 次の読み出し位置
	private int maxPosition;

	/**
	 * コンストラクタ。
	 *
	 * @param line CSV形式の文字列  改行コードを含まない。
	 */
	public CSVTokenizer(String line) {
		source = line;
		maxPosition = line.length();
	}

	/**
	 * 次の項目の文字列を返す。
	 *
	 * @return 次の項目
	 * @exception NoSuchElementException 項目が残っていないとき
	 */
	public String nextToken() {
		if (currentPosition > maxPosition) {
			return null;
		}
		int idx = currentPosition;
		int end = nextComma(currentPosition);

		StringBuffer temp = new StringBuffer();
		while (idx < end) {
			char ch = source.charAt(idx);
			// "が連続で続く場合
			if (ch == '"' && (idx < end - 1) && (source.charAt(idx + 1) == '"')) {
				temp.append(ch);
				idx ++;
			} else {
				temp.append(ch);
			}
			idx++;
		}

		if (temp.length() > 0) {
			if (temp.charAt(0) == '"' && temp.charAt(temp.length() - 1) == '"') {
				temp.deleteCharAt(temp.length() - 1);
				temp.deleteCharAt(0);
			}
		}
		currentPosition = end + 1;
		return temp.toString();
	}

	/**
	 * 次のカンマがある位置を返す。
	 * カンマが残っていない場合は nextComma() == maxPosition となる。
	 * また最後の項目が空の場合も nextComma() == maxPosition となる。
	 *
	 * @param idx 検索を開始する位置
	 * @return 次のカンマがある位置。カンマがない場合は、文字列の
	 * 長さの値となる。
	 */
	private int nextComma(int idx) {
		boolean inQuote = false;
		while (idx < maxPosition) {
			char ch = source.charAt(idx);
			if (!inQuote && ch == ',') {
				break;
			} else if ('"' == ch) {
				inQuote = !inQuote;       // ""の処理もこれでOK
			}
			idx ++;
		}
		return idx;
	}
}
