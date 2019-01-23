package jp.co.altonotes.csv;

import java.util.ArrayList;

/**
 * CSVデータの１行を表すクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class CSVLine {
	private ArrayList<CSVElement> items = new ArrayList<CSVElement>();

	/**
	 * コンストラクタ
	 */
	public CSVLine() {}

	/**
	 * コンストラクタ
	 *
	 * @param line
	 */
	public CSVLine(String line) {
		CSVTokenizer ct = new CSVTokenizer(line);
		String str = null;
		while ((str = ct.nextToken()) != null) {
			items.add(new CSVElement(str));
		}
	}

	/**
	 * 文字列配列に変換する。
	 *
	 * @return CSVの一行をカンマ区切りで分割した文字列の配列
	 */
	public String[] toStringArray() {
		ArrayList<String> list = new ArrayList<String>(items.size());
		for (CSVElement element : items) {
			list.add(element.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 文字列を追加する。
	 *
	 * @param str 追加する文字列
	 */
	public void addItem(String str) {
		items.add(new CSVElement(str));
	}

	/**
	 * 1行の項目数を返す。
	 *
	 * @return CSVLineに含んでいる項目の数
	 */
	public int size() {
		return items.size();
	}

	/**
	 * n番目の項目を String で返す。
	 *
	 * @param idx 項目の番号 [0 ～ size()-1]
	 * @return n番目の文字列。エンクォートはしない。
	 */
	public String getItem(int idx) {
		CSVElement element = items.get(idx);
		return element.toString();
    }

	/**
	 * 項目を削除する。
	 *
	 * @param n 項目の番号 [0 ～ size()-1]
	 */
	public void removeItem(int n) {
		items.remove(n);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer list = new StringBuffer();
		for (int i = 0; i < items.size(); i ++) {
			CSVElement element = items.get(i);
			String item = element.toString();
			list.append(item);
			if (items.size() - 1 != i) {
				list.append(',');
			}
		}
		return list.toString();
	}

}
