package jp.co.altonotes.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * ������𒷂��Ŕ�r����Comparator�B<br>
 * �����̍~���Ń\�[�g�����B
 *
 * @author Yamamoto Keita
 *
 */
public class StringLengthComparatorDESC implements Comparator<String>, Serializable {

	private static final long serialVersionUID = -4282587144993711180L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String o1, String o2) {
		return o1.length() - o2.length();
	}
}
