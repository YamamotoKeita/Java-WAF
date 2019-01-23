package jp.co.altonotes.util;


/**
 * �����Z���k�E�c�C�X�^�[�ɂ�藐���𐶐�����N���X�B
 * java.util.Random ��萸�x�̍��������𐶐��ł���B
 * �������Ԃ�JRE6.0 java.util.Random ��2.2�{���x�Ƃ��x�����߁A
 * �����x�̗������K�v�ȏꍇ�ȊO�͎g���K�v���Ȃ��B
 *
 * @author Yamamoto Keita
 *
 */
public class MersenneTwister {
    private static final int N = 624;
    private static final int M = 397;

    private final int mt[] = new int[N];

    private int mtIndex = N + 1;

    /**
     * �R���X�g���N�^�[�B
     */
    public MersenneTwister() {
        init((int)System.currentTimeMillis());
    }


    /**
     * �R���X�g���N�^�[�B
     *
     * @param seed �V�[�h
     */
    public MersenneTwister(int seed) {
        init(seed);
    }

    /**
     * �R���X�g���N�^�[
     * @param seedArray 
     *
     * @param seed �V�[�h�z��
     */
    public MersenneTwister(int[] seedArray) {
        init(seedArray);
    }

    /**
     * true��Ԃ��p�Z���e�[�W���w�肵�āAtrue�܂���false���擾����B
     *
     * @param percentage
     * @return �����_���ɐ������ꂽ boolean �l
     */
    public boolean nextBoolean(int percentage) {
    	if (percentage < 0 || 100 < percentage) {
			throw new IllegalArgumentException("0�`100�͈̔͂Ŏw�肵�ĉ������B");
		}
    	int rand = nextInt(1, 100);
    	return rand <= percentage;
    }

	/**
	 * �ŏ��l�ƍő�l���w�肵�āA�����_���Ȑ����𐶐�����B
	 *
	 * @param min
	 * @param max
	 * @return �����_���Ȑ���
	 */
	public int nextInt(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("�ŏ��l�ɍő�l���傫���l���ݒ肳��Ă��܂��B");
		}
		long dif = max - min;
		if (dif > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("�ŏ��l�ƍő�l�̍���int�̍ő�l�𒴂��邱�Ƃ͂ł��܂���B");
		}
		int result = min + nextNaturalInt((int)dif);
		return result;
	}
	
	public String nextIntString(int min, int max) {
		return String.valueOf(nextInt(min, max));
	}

	/**
	 * �o�C�g�l�𐶐�����B
	 *
	 * @return �����_���� byte �l
	 */
	public byte nextByte() {
		return (byte) nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	/**
	 * �ő�l���w�肵�āA�����_����0�ȏ�̐����𐶐�����B
	 *
	 * @param min
	 * @param max
	 * @return �����_���Ȏ��R��
	 */
	public int nextNaturalInt(int max) {
		int base = nextInt();
		if (base < 0) {
			base = -base;
		}
		return base % (max+1);
	}


    /**
     * ��l���z�� int �^�̋[��������Ԃ��B
     *
     * @return ��l���z�� int �^�̎��̋[�������l
     */
    public int nextInt() {
        int nextInt = generateInt();

        // ���x���グ��ׂ̒���
        nextInt = temper(nextInt);

        return nextInt;
    }

    /**
     * seed��Mersenne Twister�z�������������
     *
     * @param seed �V�[�h
     */
    public void init(int seed) {
        long[] longMt = createLongMt(seed);
        setMt(longMt);
    }

    /**
     * �z��seedArray��Mersenne Twister�z�������������
     *
     * @param seedArray �V�[�h�z��
     */
    public void init(int[] seedArray) {
        long[] longMt = createLongMt(19650218);

        final int max = (N > seedArray.length ? N : seedArray.length);
        for (int i = 1, j = 0, counter = 0; counter < max; i++, j++, counter++) {
            if (N <= i) {
                longMt[0] = longMt[N - 1];
                i = 1;
            }
            if (seedArray.length <= j) {
                j = 0;
            }
            longMt[i] ^= ((longMt[i-1] ^ (longMt[i-1] >>> 30)) * 1664525);
            longMt[i] += seedArray[j] + j;
            longMt[i] &= 0xffffffffL;
        }

        final int initial = max % (N -1) + 1;
        for (int i = initial,counter = 0; counter < N - 1; i++, counter++) {
            if (N <= i) {
                longMt[0] = longMt[N - 1];
                i = 1;
            }
            longMt[i] ^= ((longMt[i-1] ^ (longMt[i-1] >>> 30)) * 1566083941);
            longMt[i] -= i;
            longMt[i] &= 0xffffffffL;
        }

        longMt[0] = 0x80000000L;

        setMt(longMt);
    }

    /**
     * Mersenne Twister�A���S���Y���ŁA��l���z�� int �^�̋[��������Ԃ��B
     *
     * @return
     */
    private synchronized int generateInt() {
        twist();

        int ret = mt[mtIndex];
        mtIndex++;

        return ret;
    }

    /**
     * �z���twist����
     *
     */
    private void twist() {
        final int[] BIT_MATRIX = new int[] {0x0, 0x9908b0df};
        final int UPPER_MASK = 0x80000000;
        final int LOWER_MASK = 0x7fffffff;

        if (mtIndex < N) {
            return;
        }

        if(mtIndex > N) {
            init(5489);
        }

        for (int i = 0; i < N; i++) {
            int x = (mt[i] & UPPER_MASK) | (mt[(i + 1) % N] & LOWER_MASK);
            mt[i] = mt[(i + M) % N] ^ (x >>> 1) ^ BIT_MATRIX[x & 0x1];
        }

        mtIndex = 0;
    }

    /**
     * ��������
     *
     * @param num ��������int
     * @return �������ꂽint
     */
    private static int temper(int num) {

        num ^= (num >>> 11);
        num ^= (num << 7) & 0x9d2c5680;
        num ^= (num << 15) & 0xefc60000;
        num ^= (num >>> 18);

        return num;
    }

    /**
     * 2^32 - 1�ȉ���long���A�E32�r�b�g�񂾂�int�ɕϊ�����
     *
     * @param num 2^32 - 1�ȉ���long
     * @return �ϊ�����int
     */
    private static int toInt(long num) {
        return (int) (num > Integer.MAX_VALUE ? num - 0x100000000L : num);
    }

    /**
     * seed����A����������mt��long�ō��
     *
     * @param seed �V�[�h
     * @return ����������mt
     */
    private static long[] createLongMt(int seed) {
        long[] longMt = new long[N];

        longMt[0] = seed & 0xffffffffL;

        for (int i = 1; i < N; i++) {
            longMt[i] = longMt[i - 1];

            longMt[i] >>>= 30;
            longMt[i] ^= longMt[i - 1];
            longMt[i] *= 0x6C078965L;
            longMt[i] += i;
            longMt[i] &= 0xffffffffL;
        }

        return longMt;
    }

    /**
     * long�^�̔z����A�r�b�g�z��Ƃ���int�ɕϊ����āA�C���X�^���X�ϐ�mt�ɃZ�b�g����
     *
     * @param longMt �z��
     */
    synchronized private void setMt(long[] longMt) {
        for (int i = 0; i < N; i++) {
            mt[i] = toInt(longMt[i]);
        }

        mtIndex = N;
    }

}
