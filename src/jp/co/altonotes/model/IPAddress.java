package jp.co.altonotes.model;

import java.io.Serializable;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;

/**
 * IP�A�h���X�ƃT�u�l�b�g�}�X�N��\���B
 * ���̂Ƃ���IPv4�ɂ����Ή����Ă��Ȃ��B
 * ������ƒ�������IPv6�ɂ��Ή��ł��邩��A����āB
 *
 * @author Yamamoto Keita
 *
 */
public class IPAddress implements Serializable {

	private static final long serialVersionUID = 4446886141307602657L;

	private static final int UNDIFINED_SUBNETMASK = -1;
	private static final int BINARY_LENGTH = 32;
	
	private static final IPAddress INTRANET_IP = new IPAddress("192.168.0.0", 16);

	/** �h�b�g4�u���b�N��؂��IP�A�h���X */
	private String ip;

	/** 2�i����IP�A�h���X 8�~4=32�� */
	private String binaryAddress;

	/** �T�u�l�b�g�}�X�N */
	private int subnetMask = UNDIFINED_SUBNETMASK;

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param ip
	 * @param subnetMask
	 */
	public IPAddress(String ip, int subnetMask) {
		this.ip = ip;
		this.binaryAddress = toBinaryAddress(ip);
		this.subnetMask = subnetMask;
	}

	/**
	 * �R���X�g���N�^�[�B
	 * @param ip
	 */
	public IPAddress(String ip) {
		this.ip = ip;
		binaryAddress = toBinaryAddress(ip);
	}

	/**
	 * ����IP�̈�Ɏw�肵��IP���܂܂�邩���肷��B
	 *
	 * @param ip
	 * @return ����IP�̈�Ɏw�肵��IP���܂܂��ꍇ<code>true</code>
	 */
	public boolean match(String ip) {
		if (subnetMask != UNDIFINED_SUBNETMASK) {
			String binaryIp = toBinaryAddress(ip);
			String networkIp = binaryAddress.substring(0, subnetMask);
			return binaryIp.startsWith(networkIp);
		} else {
			return this.ip.equals(ip);
		}
	}

	/**
	 * ����IP�����W�Ɋ܂܂��ŏ���IP���擾����
	 *
	 * @return ����IP�����W�Ɋ܂܂��ŏ���IP
	 */
	public String min() {
		if (subnetMask != UNDIFINED_SUBNETMASK) {
			String networkIp = binaryAddress.substring(0, subnetMask);
			String b = TextUtils.padRight(networkIp, BINARY_LENGTH, '0');
			return binaryToDecimalFormat(b);
		} else {
			return ip;
		}
	}

	/**
	 * ����IP�����W�Ɋ܂܂��ő��IP���擾����
	 *
	 * @return ����IP�����W�Ɋ܂܂��ő��IP���擾����
	 */
	public String max() {
		if (subnetMask != UNDIFINED_SUBNETMASK) {
			String networkIp = binaryAddress.substring(0, subnetMask);
			String b = TextUtils.padRight(networkIp, BINARY_LENGTH, '1');
			return binaryToDecimalFormat(b);
		} else {
			return ip;
		}
	}

	/**
	 * IP�A�h���X��2�i����\��������ɕϊ�����B
	 *
	 * @param ip
	 * @return 2�i���ŕ\���ꂽIP�A�h���X
	 */
	public static String toBinaryAddress(String ip) {
		String[] blocks = ip.split("\\.");
		if (blocks.length != 4) {
			throw new IllegalArgumentException("IP�F" + ip + " ��4�u���b�N�ɕ����ł��܂���B");
		}

		StringBuilder binaryIP = new StringBuilder(50);
		int i = 0;
		for (String block : blocks) {
			if (!Checker.isNumber(block)) {
				throw new IllegalArgumentException("IP�F" + ip + " �𐔒l�Ƃ��ĔF���ł��܂���B");
			}

			i = Integer.parseInt(block);

			if (i < 0 || 255 < i) {
				throw new IllegalArgumentException("IP�F" + ip + " �͕s����IP�A�h���X�ł��B");
			}

			String binary = Integer.toBinaryString(i);

			binaryIP.append(TextUtils.padLeft(binary, 8, '0'));
		}
		return binaryIP.toString();
	}

	/**
	 * 2�i���\�L��IP�A�h���X��10�i���\�L�ɕϊ�����B
	 *
	 * @param binary
	 * @return 10�i���ŕ\���ꂽIP�A�h���X
	 */
	public static String binaryToDecimalFormat(String binary) {
		if (binary.length() != BINARY_LENGTH) {
			throw new IllegalArgumentException(BINARY_LENGTH + "���ł͂���܂���F" + binary);
		}

		for (int i = 0; i < binary.length(); i++) {
			char c = binary.charAt(i);
			if (c != '0' && c != '1') {
				throw new IllegalArgumentException("�s���ȃt�H�[�}�b�g�ł��F" + binary);
			}
		}

		StringBuilder sb = new StringBuilder(16);
		int idx = 0;
		for (int i = 0; i < 4; i++) {
			String block = binary.substring(idx, idx + 8);
			block = toDecimal(block);
			sb.append(block);
			sb.append('.');
			idx += 8;
		}

		return sb.substring(0, sb.length()-1);
	}
	
	/**
	 * ������IP�����[�J���z�X�g�̂��̂����肷��B
	 * @param ip
	 * @return ������IP�����[�J���z�X�g�̂��̂Ȃ� <code>true</code>
	 */
	public static boolean isLocalHost(String ip) {
		return "127.0.0.1".equals(ip);
	}

	/**
	 * ������IP���C���g���l�b�g�̂��̂����肷��B
	 * @param ip
	 * @return ������IP���C���g���l�b�g�̂��̂Ȃ� <code>true</code>
	 */
	public static boolean isIntranet(String ip) {
		return INTRANET_IP.match(ip);
	}
	
	/**
	 * 2�i����\���������10�i����\��������ɕϊ�����
	 *
	 * @param binary
	 * @return 10�i���̕�����
	 */
	private static String toDecimal(String binary) {

		int rate = 1;
		int decimal = 0;

		for (int i = binary.length()-1; i >= 0; i--) {
			char c = binary.charAt(i);
			int j = 0;
			if (c == '1') {
				j = 1;
			}
			decimal += j * rate;
			rate <<= 1;
		}
		return String.valueOf(decimal);
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = ip;
		if (subnetMask != UNDIFINED_SUBNETMASK) {
			str += "/" + subnetMask;
			str += " (" + min() + " �` " + max() + ")";
		}
		return str;
	}
}
