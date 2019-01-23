package jp.co.altonotes.cipher;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import jp.co.altonotes.net.util.Base64;

/**
 * ARC4�A���S���Y���ɂ��Í����E���������s���N���X
 * ��肭�����Ă�̂��悭�������B
 *
 * @author Yamamoto Keita
 *
 */
public class ARC4 {
	/** ���ʌ��ɂ��z��		*/
	private int[] box;

	private final static String DEFAULT_CHARSET = "UTF-8";

	/**
	 * ���C�����\�b�h�B�e�X�g�p�B
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		ARC4 proc = ARC4.getProcessor("�Ă��Ƃ�������");
		byte[] data = "999999".getBytes();
		byte[] encoded = proc.process(data);
		String string = Base64.encodeBase64URLSafeString(encoded);
		System.out.println(string);
		byte[] decoded = proc.process(encoded);
		System.out.println(new String(decoded));
	}

	/**
	 * �����Z�b�g����ARC4�C���X�^���X�𐶐�����B
	 * �Í����E�������͂��̃��\�b�h�ɂ���Đ������ꂽ�C���X�^���X���g�p���čs���B
	 *
	 * ���̒����͔C�ӂ����A��ʂ�128�r�b�g(16�o�C�g)�ȏオ�\�����łƍl�����Ă���B
	 * �������A2056�r�b�g(257�o�C�g)�ȏ�̌��́A���̌㕔256�o�C�g�����g�p����Ȃ��̂Ŗ��Ӗ��B
	 *
	 * @param key
	 * @return ������key�ō쐬���ꂽ<code>ARC4</code>�C���X�^���X
	 */
	public static ARC4 getProcessor(byte[] key){
		ARC4 arc4 = new ARC4();
		arc4.setKey(key);
		return arc4;
	}

	/**
	 * ��������L�[�ɂ���ARC4�C���X�^���X�𐶐�
	 *
	 * @param key
	 * @return ������key�ō쐬���ꂽ<code>ARC4</code>�C���X�^���X
	 * @throws UnsupportedEncodingException 
	 */
	public static ARC4 getProcessor(String key) throws UnsupportedEncodingException{
		byte[] binaryKey = null;
		try {
			binaryKey = key.getBytes(DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
		return getProcessor(binaryKey);
	}

	/**
	 * ���ʌ����Z�b�g����
	 * @param key
	 */
	private void setKey(byte[] key){

		//�{�b�N�X������
		box = new int[256];
		for (int i = 0; i < box.length; i++) {
			box[i] = i;
		}

		//�{�b�N�X2�Ɍ��Z�b�g
		int[] box2 = new int[256];
		int keyLength = key.length;
		for (int i = 0; i < box2.length; i++) {
			box2[i] = 0xFF & key[i%keyLength];
		}

		//�{�b�N�X�V���b�t��
		int j = 0;
		int temp = 0;
		for (int i = 0; i < box.length; i++) {
			j = (j + box[i] + box2[i]) % 256;

			//mBox[i]��mBox[j]�����ւ�
			temp = box[i];
			box[i] = box[j];
			box[j] = temp;
		}
	}

	/**
	 * �f�[�^���Í����E����������
	 * ARC4�ɂ����ĈÍ����E�������͑S����������
	 * �����������ɓn���ΈÍ��������A�Í������������ɓn���Ό��̕������Ԃ�
	 *
	 * @param data
	 * @return �Í����܂��͕��������ꂽ�f�[�^
	 */
	public byte[] process(byte[] data){
		int[] key = Arrays.copyOf(box, box.length);
		byte[] encData = new byte[data.length];

		int j = 0;
		int temp = 0;
		int t = 0;
		for (int i = 0, v = 0; v < encData.length; i++, v++) {
			 i = (i+1) % 256;
			 j = (j + key[i]) % 256;
			 temp = key[i];
			 key[i] = key[j];
			 key[j] = temp;
			 t = (key[i] + key[j]) % 256;
			 encData[v] = (byte) (data[v] ^ (byte)key[t]);
		}
		return encData;
	}

}
