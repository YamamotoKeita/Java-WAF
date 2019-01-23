package jp.co.altonotes.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 文字列を長さで比較するComparator。<br>
 * 長さの昇順でソートされる。
 *
 * @author Yamamoto Keita
 *
 */
public class StringLengthComparatorASC implements Comparator<String>, Serializable {

	private static final long serialVersionUID = -5638491135451846931L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String o1, String o2) {
		return o2.length() - o1.length();
	}
}
