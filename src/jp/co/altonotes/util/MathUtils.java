package jp.co.altonotes.util;

import java.util.List;

/**
 * 数値計算に関するユーティリティー。
 *
 * @author Yamamoto Keita
 *
 */
public class MathUtils {
	
	/**
	 * 値の平均を求める。
	 *
	 * @param values
	 * @return　平均値
	 */
	public static float average(float[] values) {
		double sum = 0;
		for (float f : values) {
			sum += f;
		}
		return (float) (sum / values.length);
	}

	/**
	 * 値の平均を求める。
	 *
	 * @param values
	 * @return 平均値
	 */
	public static float average(List<Float> values) {
		double sum = 0;
		for (float f : values) {
			sum += f;
		}
		return (float) (sum / values.size());
	}

	/**
	 * 前回までの平均、追加する値、データの総数を元に、新しい平均を求める。
	 *
	 * @param oldAverage
	 * @param additional
	 * @param count
	 * @param exponent
	 * @return 平均値
	 */
	public static float average(float oldAverage, float additional, int count) {
		if (count == 1) {
			return additional;
		} else {
			return oldAverage + ((additional-oldAverage) / count);
		}
	}

	/**
	 * 複数の値の中の最大値を求める。
	 *
	 * @param values
	 * @return 最大値
	 */
	public static float max(float[] values) {
		if (values.length < 1) {
			throw new IllegalArgumentException("データ数が0個です。");
		}
		float max = values[0];
		for (int i = 1; i < values.length; i++) {
			max = Math.max(max, values[i]);
		}
		return max;
	}

	/**
	 * 複数の値の中の最小値を求める。
	 *
	 * @param values
	 * @return 最小値
	 */
	public static float min(float[] values) {
		if (values.length < 1) {
			throw new IllegalArgumentException("データ数が0個です。");
		}
		float min = values[0];
		for (int i = 1; i < values.length; i++) {
			min = Math.min(min, values[i]);
		}
		return min;
	}

	public static float variance(float[] values) {
		if (values.length < 2) {
			throw new IllegalArgumentException("データ数１以下の分散は求められません。");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += (avg - f) * (avg - f);
		}
		return difSum / values.length;
	}

	/**
	 * 値の不偏分散を求める。
	 *
	 * @param values
	 * @return 普遍分散
	 */
	public static float unbiasedVariance(float[] values) {
		if (values.length < 2) {
			throw new IllegalArgumentException("データ数１以下の分散は求められません。");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += (avg - f) * (avg - f);
		}
		return difSum / (values.length - 1);
	}

	/**
	 * 値の分散を求める
	 * @param values
	 * @return
	 */
	public static float variance(List<Float> values) {
		if (values.size() < 2) {
			throw new IllegalArgumentException("データ数１以下の分散は求められません。");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += square(avg - f);
		}
		return difSum / values.size();
	}

	/**
	 * 値の不偏分散を求める。
	 *
	 * @param values
	 * @return 普遍分散
	 */
	public static float unbiasedVariance(List<Float> values) {
		if (values.size() < 2) {
			throw new IllegalArgumentException("データ数１以下の分散は求められません。");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += square(avg - f);
		}
		return difSum / (values.size() - 1);
	}

	/**
	 * 前回までの不偏分散、前回までの平均、新しい平均、追加する値、値の個数を元に不偏分散を計算する。
	 *
	 * @param oldVariance
	 * @param oldAvg
	 * @param newAvg
	 * @param additional
	 * @param n
	 * @return 普遍分散
	 */
	public static float unbiasedVariance(float oldVariance, float oldAvg, float newAvg, float additional, float n) {
		float v1 = oldVariance * (n-2) / (n-1);
		float v2 = oldAvg * oldAvg;
		float v3 = ((additional * additional) - (n * newAvg * newAvg)) / (n - 1);
		return v1 + v2 + v3;
	}

	/**
	 * 値を二乗する。
	 *
	 * @param f
	 * @return 二乗した値
	 */
	public static float square(float f) {
		return (float) Math.pow(f, 2);
	}

	/**
	 * 値の正の平方根を取得する。
	 *
	 * @param f
	 * @return 正の平方根
	 */
	public static float root(float f) {
		return (float) Math.sqrt(f);
	}

	/**
	 * オリジナル値に指定した割合で別の値を混ぜる。
	 *
	 * @param original
	 * @param additional
	 * @param rate
	 * @return ブレンドした値
	 */
	public static float mix(float original, float additional, float rate) {
		float v1 = original * (1-rate);
		float v2 = additional * rate;
		return v1 + v2;
	}

}
