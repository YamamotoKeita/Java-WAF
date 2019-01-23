package jp.co.altonotes.util;


/**
 * ���j�[�NID�𐶐�����N���X�B
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
	 * 20����ID�𐶐�����B
	 *
	 * @return �V�X�e�����������ɐ�������20����ID
	 */
	public static String getID() {
		return getID(20);
	}

	/**
	 * �������w�肵��ID�𐶐�����B
	 *
	 * @param length
	 * @return �V�X�e�����������ɐ����������j�[�NID
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
	 * �����_����16������Ԃ��B
	 * �g�p����镶����0-9, a-b, A-Z
	 * �p�^�[�����͖�4.7�a(47672401706823533��)
	 * ��ӂł���ۏ؂͂Ȃ����A�����L�[���o��\���͂قږ����B
	 *
	 * @return �����_����16����
	 */
	public static String getRandomKey() {
		return getRandomKey(16);
	}

	/**
	 * �����_���ȕ������Ԃ��B
	 * �g�p����镶����0-9, a-b, A-Z
	 *
	 * @return �����_���ȕ�����
	 */
	public static String getRandomKey(int size) {
		StringBuffer temp = new StringBuffer(size);
		for (int i = 0; i < size; i++) {
			temp.append(randomChar());
		}
		return temp.toString();
	}

	/**
	 * �V�X�e������������long�l�̃��j�[�NID�𐶐�����B
	 * 2002�N�`2286�N���炢�܂�16���B
	 * 10ms��1000����x�擾����Əd�����邨���ꂪ����B
	 *
	 * @return �V�X�e�����������ɐ����������j�[�N��long�l
	 */
	public synchronized static long getLong() {
		long time = System.currentTimeMillis();
		if (time == sTime) {
			if (sCount >= 1000) {
				throw new IllegalStateException("ID��������\�̌��E�𒴂��܂����B");
			}
			sCount++;
		} else {
			sTime = time;
			sCount = 0;
		}
		return (time * 1000) + sCount;
	}

	/**
	 * 0-9, a-b, A-Z�̒����烉���_���Ɉꕶ���擾����B
	 *
	 * @return 0-9, a-b, A-Z�̒����烉���_���Ɏ擾��������
	 */
	public static char randomChar() {
		int i = sRandom.nextInt(0, 61);
		return getNormalChar(i);
	}

	/**
	 * 0-9, a-b, A-Z�̕�����0����61�̐����Ɋ��蓖�āA
	 * �����̐��l�ɑΉ�����������Ԃ��B
	 *
	 * @param i
	 * @return 0����61�̐����ɑΉ�����0-9, a-b, A-Z�̕���
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
	 * long�l��0-9, a-b, A-Z�̕������g����62�i���̕�����ɕϊ�����B
	 *
	 * @param l
	 * @return 62�i���̕�����
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
