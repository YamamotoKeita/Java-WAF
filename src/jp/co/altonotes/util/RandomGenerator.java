package jp.co.altonotes.util;

import java.util.ArrayList;
import java.util.Random;

/**
 * �����𐶐�����N���X�B
 *
 * @author Yamamoto Keita
 *
 */
public class RandomGenerator {

	private static Random random = new Random(System.currentTimeMillis());
	private ArrayList<Integer> list = new ArrayList<Integer>();
	private int minimum = 0;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param min
	 * @param max
	 */
	private RandomGenerator(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("�ŏ��l�ɍő�l���傫���l���ݒ肳��Ă��܂��B");
		}
		minimum = min;
		int count = max - min + 1;
		for (int i = 0; i < count; i++) {
			list.add(i);
		}
	}

	/**
	 * �w�肵���ŏ��l�ƍő�l�͈͓̔��ŁA��Ɉ�ӂȃ����_�������𐶐�����C���X�^���X���擾����B
	 *
	 * @param min
	 * @param max
	 * @return�@�����_���Ȑ���
	 */
	public static RandomGenerator getUniqueNumberGenerator(int min, int max) {
		return new RandomGenerator(min, max);
	}

	/**
	 * ���̗��������C���X�^���X���A���̒l�������Ă��邩�`�F�b�N����B
	 *
	 * @return ���̒l�������Ă���ꍇ true
	 */
	public boolean hasMore() {
		return list.size() != 0;
	}

	/**
	 * ���̗����𐶐�����B
	 *
	 * @return ���̗���
	 */
	public int nextInt() {
		int index = getNaturalInt(list.size() - 1);
		int plus = list.get(index);
		list.remove(index);
		return minimum + plus;
	}

	/**
	 * �����_���Ȑ����𐶐�����B
	 *
	 * @return �����_���Ȑ���
	 */
	public static int getInt() {
		return random.nextInt();
	}

	/**
	 * �����_���Ȑ����𐶐�����B
	 *
	 * @return �����_���Ȑ���
	 */
	public static long getLong() {
		return random.nextLong();
	}

	/**
	 * �����_���ȃo�C�g�z��𐶐�����
	 * @param bytes
	 */
	public static void getByte(byte[] bytes) {
		random.nextBytes(bytes);
	}

	/**
	 * �ő�l���w�肵�āA�����_����0�ȏ�̐����𐶐�����B
	 *
	 * @param min
	 * @param max
	 * @return �����_���Ȏ��R��
	 */
	public static int getNaturalInt(int max) {
		int base = random.nextInt();
		if (base < 0) {
			base = -base;
		}
		return base % (max+1);
	}

	/**
	 * �ő�l���w�肵�āA�����_����0�ȏ�̐����𐶐�����B
	 *
	 * @param min
	 * @param max
	 * @return �����_���Ȏ��R��
	 */
	public static long getNaturalLong(long max) {
		long base = random.nextLong();
		if (base < 0) {
			base = -base;
		}
		return base % (max+1);
	}

	/**
	 * �ŏ��l�ƍő�l���w�肵�āA�����_���Ȑ����𐶐�����B
	 *
	 * @param min
	 * @param max
	 * @return �����_���Ȑ���
	 */
	public static int getInt(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("�ŏ��l�ɍő�l���傫���l���ݒ肳��Ă��܂��B");
		}
		long dif = max - min;
		long result = min + getNaturalLong(dif);
		return (int) result;
	}

}
