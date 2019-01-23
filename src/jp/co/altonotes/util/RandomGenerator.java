package jp.co.altonotes.util;

import java.util.ArrayList;
import java.util.Random;

/**
 * 乱数を生成するクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class RandomGenerator {

	private static Random random = new Random(System.currentTimeMillis());
	private ArrayList<Integer> list = new ArrayList<Integer>();
	private int minimum = 0;

	/**
	 * コンストラクタ。
	 *
	 * @param min
	 * @param max
	 */
	private RandomGenerator(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("最小値に最大値より大きい値が設定されています。");
		}
		minimum = min;
		int count = max - min + 1;
		for (int i = 0; i < count; i++) {
			list.add(i);
		}
	}

	/**
	 * 指定した最小値と最大値の範囲内で、常に一意なランダム整数を生成するインスタンスを取得する。
	 *
	 * @param min
	 * @param max
	 * @return　ランダムな整数
	 */
	public static RandomGenerator getUniqueNumberGenerator(int min, int max) {
		return new RandomGenerator(min, max);
	}

	/**
	 * この乱数生成インスタンスが、次の値を持っているかチェックする。
	 *
	 * @return 次の値を持っている場合 true
	 */
	public boolean hasMore() {
		return list.size() != 0;
	}

	/**
	 * 次の乱数を生成する。
	 *
	 * @return 次の乱数
	 */
	public int nextInt() {
		int index = getNaturalInt(list.size() - 1);
		int plus = list.get(index);
		list.remove(index);
		return minimum + plus;
	}

	/**
	 * ランダムな整数を生成する。
	 *
	 * @return ランダムな整数
	 */
	public static int getInt() {
		return random.nextInt();
	}

	/**
	 * ランダムな整数を生成する。
	 *
	 * @return ランダムな整数
	 */
	public static long getLong() {
		return random.nextLong();
	}

	/**
	 * ランダムなバイト配列を生成する
	 * @param bytes
	 */
	public static void getByte(byte[] bytes) {
		random.nextBytes(bytes);
	}

	/**
	 * 最大値を指定して、ランダムな0以上の整数を生成する。
	 *
	 * @param min
	 * @param max
	 * @return ランダムな自然数
	 */
	public static int getNaturalInt(int max) {
		int base = random.nextInt();
		if (base < 0) {
			base = -base;
		}
		return base % (max+1);
	}

	/**
	 * 最大値を指定して、ランダムな0以上の整数を生成する。
	 *
	 * @param min
	 * @param max
	 * @return ランダムな自然数
	 */
	public static long getNaturalLong(long max) {
		long base = random.nextLong();
		if (base < 0) {
			base = -base;
		}
		return base % (max+1);
	}

	/**
	 * 最小値と最大値を指定して、ランダムな整数を生成する。
	 *
	 * @param min
	 * @param max
	 * @return ランダムな整数
	 */
	public static int getInt(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("最小値に最大値より大きい値が設定されています。");
		}
		long dif = max - min;
		long result = min + getNaturalLong(dif);
		return (int) result;
	}

}
