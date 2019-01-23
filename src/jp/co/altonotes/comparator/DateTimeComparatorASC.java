package jp.co.altonotes.comparator;

import java.io.Serializable;
import java.util.Comparator;

import jp.co.altonotes.model.DateTime;

/**
 * 時刻を比較するComparator。<br>
 * 時刻の昇順でソートされる。
 *
 * @author Yamamoto Keita
 *
 */
public class DateTimeComparatorASC implements Comparator<DateTime>, Serializable {

	private static final long serialVersionUID = 438264758777667946L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(DateTime o1, DateTime o2) {
		long l = o2.getTimeInMillis() - o1.getTimeInMillis();
		long maxInt = Integer.MAX_VALUE;
		if (maxInt < l) {
			l = maxInt;
		}
		return (int) l;
	}
}
