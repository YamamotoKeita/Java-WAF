package jp.co.altonotes.util;

import java.util.List;

/**
 * ���l�v�Z�Ɋւ��郆�[�e�B���e�B�[�B
 *
 * @author Yamamoto Keita
 *
 */
public class MathUtils {
	
	/**
	 * �l�̕��ς����߂�B
	 *
	 * @param values
	 * @return�@���ϒl
	 */
	public static float average(float[] values) {
		double sum = 0;
		for (float f : values) {
			sum += f;
		}
		return (float) (sum / values.length);
	}

	/**
	 * �l�̕��ς����߂�B
	 *
	 * @param values
	 * @return ���ϒl
	 */
	public static float average(List<Float> values) {
		double sum = 0;
		for (float f : values) {
			sum += f;
		}
		return (float) (sum / values.size());
	}

	/**
	 * �O��܂ł̕��ρA�ǉ�����l�A�f�[�^�̑��������ɁA�V�������ς����߂�B
	 *
	 * @param oldAverage
	 * @param additional
	 * @param count
	 * @param exponent
	 * @return ���ϒl
	 */
	public static float average(float oldAverage, float additional, int count) {
		if (count == 1) {
			return additional;
		} else {
			return oldAverage + ((additional-oldAverage) / count);
		}
	}

	/**
	 * �����̒l�̒��̍ő�l�����߂�B
	 *
	 * @param values
	 * @return �ő�l
	 */
	public static float max(float[] values) {
		if (values.length < 1) {
			throw new IllegalArgumentException("�f�[�^����0�ł��B");
		}
		float max = values[0];
		for (int i = 1; i < values.length; i++) {
			max = Math.max(max, values[i]);
		}
		return max;
	}

	/**
	 * �����̒l�̒��̍ŏ��l�����߂�B
	 *
	 * @param values
	 * @return �ŏ��l
	 */
	public static float min(float[] values) {
		if (values.length < 1) {
			throw new IllegalArgumentException("�f�[�^����0�ł��B");
		}
		float min = values[0];
		for (int i = 1; i < values.length; i++) {
			min = Math.min(min, values[i]);
		}
		return min;
	}

	public static float variance(float[] values) {
		if (values.length < 2) {
			throw new IllegalArgumentException("�f�[�^���P�ȉ��̕��U�͋��߂��܂���B");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += (avg - f) * (avg - f);
		}
		return difSum / values.length;
	}

	/**
	 * �l�̕s�Ε��U�����߂�B
	 *
	 * @param values
	 * @return ���Օ��U
	 */
	public static float unbiasedVariance(float[] values) {
		if (values.length < 2) {
			throw new IllegalArgumentException("�f�[�^���P�ȉ��̕��U�͋��߂��܂���B");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += (avg - f) * (avg - f);
		}
		return difSum / (values.length - 1);
	}

	/**
	 * �l�̕��U�����߂�
	 * @param values
	 * @return
	 */
	public static float variance(List<Float> values) {
		if (values.size() < 2) {
			throw new IllegalArgumentException("�f�[�^���P�ȉ��̕��U�͋��߂��܂���B");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += square(avg - f);
		}
		return difSum / values.size();
	}

	/**
	 * �l�̕s�Ε��U�����߂�B
	 *
	 * @param values
	 * @return ���Օ��U
	 */
	public static float unbiasedVariance(List<Float> values) {
		if (values.size() < 2) {
			throw new IllegalArgumentException("�f�[�^���P�ȉ��̕��U�͋��߂��܂���B");
		}

		float avg = average(values);
		float difSum = 0;

		for (float f : values) {
			difSum += square(avg - f);
		}
		return difSum / (values.size() - 1);
	}

	/**
	 * �O��܂ł̕s�Ε��U�A�O��܂ł̕��ρA�V�������ρA�ǉ�����l�A�l�̌������ɕs�Ε��U���v�Z����B
	 *
	 * @param oldVariance
	 * @param oldAvg
	 * @param newAvg
	 * @param additional
	 * @param n
	 * @return ���Օ��U
	 */
	public static float unbiasedVariance(float oldVariance, float oldAvg, float newAvg, float additional, float n) {
		float v1 = oldVariance * (n-2) / (n-1);
		float v2 = oldAvg * oldAvg;
		float v3 = ((additional * additional) - (n * newAvg * newAvg)) / (n - 1);
		return v1 + v2 + v3;
	}

	/**
	 * �l���悷��B
	 *
	 * @param f
	 * @return ��悵���l
	 */
	public static float square(float f) {
		return (float) Math.pow(f, 2);
	}

	/**
	 * �l�̐��̕��������擾����B
	 *
	 * @param f
	 * @return ���̕�����
	 */
	public static float root(float f) {
		return (float) Math.sqrt(f);
	}

	/**
	 * �I���W�i���l�Ɏw�肵�������ŕʂ̒l��������B
	 *
	 * @param original
	 * @param additional
	 * @param rate
	 * @return �u�����h�����l
	 */
	public static float mix(float original, float additional, float rate) {
		float v1 = original * (1-rate);
		float v2 = additional * rate;
		return v1 + v2;
	}

}
