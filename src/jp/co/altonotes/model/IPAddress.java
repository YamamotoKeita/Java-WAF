package jp.co.altonotes.model;

import java.io.Serializable;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;

/**
 * IPアドレスとサブネットマスクを表す。
 * 今のところIPv4にしか対応していない。
 * ちょっと直したらIPv6にも対応できるから、やって。
 *
 * @author Yamamoto Keita
 *
 */
public class IPAddress implements Serializable {

	private static final long serialVersionUID = 4446886141307602657L;

	private static final int UNDIFINED_SUBNETMASK = -1;
	private static final int BINARY_LENGTH = 32;
	
	private static final IPAddress INTRANET_IP = new IPAddress("192.168.0.0", 16);

	/** ドット4ブロック区切りのIPアドレス */
	private String ip;

	/** 2進数のIPアドレス 8×4=32桁 */
	private String binaryAddress;

	/** サブネットマスク */
	private int subnetMask = UNDIFINED_SUBNETMASK;

	/**
	 * コンストラクター。
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
	 * コンストラクター。
	 * @param ip
	 */
	public IPAddress(String ip) {
		this.ip = ip;
		binaryAddress = toBinaryAddress(ip);
	}

	/**
	 * このIP領域に指定したIPが含まれるか判定する。
	 *
	 * @param ip
	 * @return このIP領域に指定したIPが含まれる場合<code>true</code>
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
	 * このIPレンジに含まれる最小のIPを取得する
	 *
	 * @return このIPレンジに含まれる最小のIP
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
	 * このIPレンジに含まれる最大のIPを取得する
	 *
	 * @return このIPレンジに含まれる最大のIPを取得する
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
	 * IPアドレスを2進数を表す文字列に変換する。
	 *
	 * @param ip
	 * @return 2進数で表されたIPアドレス
	 */
	public static String toBinaryAddress(String ip) {
		String[] blocks = ip.split("\\.");
		if (blocks.length != 4) {
			throw new IllegalArgumentException("IP：" + ip + " を4ブロックに分割できません。");
		}

		StringBuilder binaryIP = new StringBuilder(50);
		int i = 0;
		for (String block : blocks) {
			if (!Checker.isNumber(block)) {
				throw new IllegalArgumentException("IP：" + ip + " を数値として認識できません。");
			}

			i = Integer.parseInt(block);

			if (i < 0 || 255 < i) {
				throw new IllegalArgumentException("IP：" + ip + " は不正なIPアドレスです。");
			}

			String binary = Integer.toBinaryString(i);

			binaryIP.append(TextUtils.padLeft(binary, 8, '0'));
		}
		return binaryIP.toString();
	}

	/**
	 * 2進数表記のIPアドレスを10進数表記に変換する。
	 *
	 * @param binary
	 * @return 10進数で表されたIPアドレス
	 */
	public static String binaryToDecimalFormat(String binary) {
		if (binary.length() != BINARY_LENGTH) {
			throw new IllegalArgumentException(BINARY_LENGTH + "桁ではありません：" + binary);
		}

		for (int i = 0; i < binary.length(); i++) {
			char c = binary.charAt(i);
			if (c != '0' && c != '1') {
				throw new IllegalArgumentException("不正なフォーマットです：" + binary);
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
	 * 引数のIPがローカルホストのものか判定する。
	 * @param ip
	 * @return 引数のIPがローカルホストのものなら <code>true</code>
	 */
	public static boolean isLocalHost(String ip) {
		return "127.0.0.1".equals(ip);
	}

	/**
	 * 引数のIPがイントラネットのものか判定する。
	 * @param ip
	 * @return 引数のIPがイントラネットのものなら <code>true</code>
	 */
	public static boolean isIntranet(String ip) {
		return INTRANET_IP.match(ip);
	}
	
	/**
	 * 2進数を表す文字列を10進数を表す文字列に変換する
	 *
	 * @param binary
	 * @return 10進数の文字列
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
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = ip;
		if (subnetMask != UNDIFINED_SUBNETMASK) {
			str += "/" + subnetMask;
			str += " (" + min() + " ～ " + max() + ")";
		}
		return str;
	}
}
