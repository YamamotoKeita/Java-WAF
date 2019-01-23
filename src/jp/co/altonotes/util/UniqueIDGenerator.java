package jp.co.altonotes.util;


/**
 * ユニークIDを生成するクラス。
 *
 *
 * @author Yamamoto Keita
 *
 */
public class UniqueIDGenerator {

	private static long sTime = 0;
	private static long sCount = 0;
	private static MersenneTwister sRandom = new MersenneTwister();

	/**
	 * 20桁のIDを生成する。
	 *
	 * @return システム時刻を元に生成した20桁のID
	 */
	public static String getID() {
		return getID(20);
	}

	/**
	 * 長さを指定してIDを生成する。
	 *
	 * @param length
	 * @return システム時刻を元に生成したユニークID
	 */
	public static String getID(int length) {
		StringBuffer temp = new StringBuffer(length);

		String timeID = base62encode(getLong());
		temp.append(timeID);

		while (temp.length() < length) {
			temp.append(randomChar());
		}

		return temp.substring(temp.length() - length, temp.length());
	}

	/**
	 * ランダムな16文字を返す。
	 * 使用される文字は0-9, a-b, A-Z
	 * パターン数は約4.7溝(47672401706823533兆)
	 * 一意である保証はないが、同じキーが出る可能性はほぼ無い。
	 *
	 * @return ランダムな16文字
	 */
	public static String getRandomKey() {
		return getRandomKey(16);
	}

	/**
	 * ランダムな文字列を返す。
	 * 使用される文字は0-9, a-b, A-Z
	 *
	 * @return ランダムな文字列
	 */
	public static String getRandomKey(int size) {
		StringBuffer temp = new StringBuffer(size);
		for (int i = 0; i < size; i++) {
			temp.append(randomChar());
		}
		return temp.toString();
	}

	/**
	 * システム時刻を元にlong値のユニークIDを生成する。
	 * 2002年～2286年くらいまで16桁。
	 * 10msに1000回程度取得すると重複するおそれがある。
	 *
	 * @return システム時刻を元に生成したユニークなlong値
	 */
	public synchronized static long getLong() {
		long time = System.currentTimeMillis();
		if (time == sTime) {
			if (sCount >= 1000) {
				throw new IllegalStateException("ID生成分解能の限界を超えました。");
			}
			sCount++;
		} else {
			sTime = time;
			sCount = 0;
		}
		return (time * 1000) + sCount;
	}

	/**
	 * 0-9, a-b, A-Zの中からランダムに一文字取得する。
	 *
	 * @return 0-9, a-b, A-Zの中からランダムに取得した文字
	 */
	public static char randomChar() {
		int i = sRandom.nextInt(0, 61);
		return getNormalChar(i);
	}

	/**
	 * 0-9, a-b, A-Zの文字を0から61の数字に割り当て、
	 * 引数の数値に対応した文字を返す。
	 *
	 * @param i
	 * @return 0から61の数字に対応する0-9, a-b, A-Zの文字
	 */
	public static char getNormalChar(int i) {
		char ch;
		if (i <= 9) {
			ch = (char) (i + 48);
		} else if (10 <= i && i <= 35) {
			ch = (char) (i + 55);
		} else {
			ch = (char) (i + 61);
		}
		return ch;
	}

	/**
	 * long値を0-9, a-b, A-Zの文字を使った62進数の文字列に変換する。
	 *
	 * @param l
	 * @return 62進数の文字列
	 */
	public static String base62encode(long l) {
		StringBuffer temp = new StringBuffer();
		long q;
		while ((q = l / 62) != 0) {
			temp.append(getNormalChar((int)(l % 62)));
			l = q;
		}
		temp.append(getNormalChar((int)(l % 62)));
		temp.reverse();
		return temp.toString();
	}
}
