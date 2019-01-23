package jp.co.altonotes.util;

/**
 * バイナリデータに関するユーティリティー。
 *
 * @author Yamamoto Keita
 *
 */
public class ByteUtils {
	
	/** 2の10乗	= 1024*/
	public static long KILO = 1 << 10;
	/** 2の20乗	= 1048576 */
	public static long MEGA = 1 << 20;
	/** 2の30乗	*/
	public static long GIGA = 1 << 30;

	/**
	 * バイトサイズより、キロ、メガ、ギガ単位つきの文字列を作成する。<br>
	 * その際、小数点以下のサイズは四捨五入する。
	 *
	 * @param size
	 * @return 単位つきのサイズ
	 */
	public static String formatSize(long size) {
		float fsize = size;
		String suffix = null;

		if (size < KILO) {
			suffix = "B";
		} else if (size < MEGA) {//K
			fsize /= KILO;
			suffix = "KB";
		} else if (size < GIGA) {//M
			fsize /= MEGA;
			suffix = "MB";
		} else {
			fsize /= GIGA;
			suffix = "GB";
		}

		return String.valueOf(Math.round(fsize)) + suffix;
	}

	/**
	 * リトルエンディアンでintをバイト配列にする。
	 *
	 * @param val
	 * @return intを変換したバイト配列
	 */
	public static byte[] intToBytes(int val) {
		byte[] bytes = new byte[4];

		for (int i = 0; i < bytes.length; i++) {
			long shift = i * 8;
			long mask = 0xFF << shift;
			bytes[i] = (byte) ((val & mask) >> shift);
		}

		return bytes;
	}
	
	/**
	 * リトルエンディアンのバイト配列をintにする。
	 *
	 * @param bytes
	 * @return バイト配列を変換したint値
	 */
	public static int bytesToInt(byte[] bytes) {
		int result = 0;

		for (int i = 0; i < bytes.length ; i++) {
			int shift = i * 8;
			int val = bytes[i] & 0xFF;
			result += val << shift;
		}

		return result;
	}
	
	/**
	 * リトルエンディアンでlongをバイト配列にする。
	 *
	 * @param l
	 * @return longを変換したバイト配列
	 */
	public static byte[] longToBytes(long l) {
		byte[] bytes = new byte[8];

		for (int i = 0; i < bytes.length; i++) {
			long shift = i * 8;
			long mask = 0xFFL << shift;
			bytes[i] = (byte) ((l & mask) >> shift);
		}

		return bytes;
	}

	/**
	 * バイト配列をリトルエンディアンでlongにする。
	 *
	 * @param bytes
	 * @return バイト配列を変換したlong値
	 */
	public static long bytesToLong(byte[] bytes) {
		long l = 0;

		for (int i = 0; i < bytes.length ; i++) {
			long shift = i * 8;
			long val = bytes[i] & 0xFF;
			l += val << shift;
		}

		return l;
	}

	/**
	 * バイト配列をコピーする。
	 *
	 * @param src
	 * @param srcPos
	 * @param dest
	 */
	public static void copy(byte[] src, int srcPos, byte[] dest) {
		int length = dest.length;
		int srcLength = src.length - srcPos;
		if (srcLength < length) {
			length = srcLength;
		}
		System.arraycopy(src, srcPos, dest, 0, length);
	}

	/**
	 * 16進数文字列に変換する。
	 *
	 * @param b
	 * @return 引数のbyte値を表す16進数
	 */
	public static String toHexString(byte b) {
		int i = b & 0xFF;
		String str = Integer.toHexString(i).toUpperCase();
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * カンマ区切りの16進数文字列に変換する。
	 *
	 * @param data
	 * @return 引数のbyte配列を表す16進数(カンマ区切り)
	 */
	public static String toHexStringWithComma(byte[] data) {
		StringBuffer buf = new StringBuffer(data.length*2);

		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(toHexString(data[i]));
		}
		return buf.toString();
	}

	/**
	 * 16進数文字列に変換する。
	 *
	 * @param data
	 * @return 引数のbyte配列を表す16進数
	 */
	public static String toHexString(byte[] data) {
		StringBuffer buf = new StringBuffer(data.length*2);

		for (int i = 0; i < data.length; i++) {
			buf.append(toHexString(data[i]));
		}
		return buf.toString();
	}
	
	/**
	 * 2進数文字列に変換する
	 * 
	 * @param b
	 * @return 引数のbyteを表す2進数文字列
	 */
	public static String toBinaryString(byte b) {
		int i = b & 0xFF;
		String str = Integer.toBinaryString(i);
		
		while (str.length() < 8) {
			str = "0" + str;
		}
		return str;
	}
	
	/**
	 * 2進数文字列をbyteに変換する
	 * 
	 * @param binary
	 * @return 2進数文字列を表すbyte
	 */
	public static byte binaryToByte(String binary) {
		return (byte) (int) Integer.valueOf(binary, 2);
	}

	/**
	 * 16進数の文字列をバイト配列に変換する。
	 * 
	 * @param hex 16進数の文字列
	 * @return バイト配列
	 */
	public static byte[] hexToByteArray(String hex) {
		// 文字列長の1/2の長さのバイト配列を生成。
		byte[] bytes = new byte[hex.length() / 2];

		// バイト配列の要素数分、処理を繰り返す。
		for (int index = 0; index < bytes.length; index++) {
			// 16進数文字列をバイトに変換して配列に格納
			String str = hex.substring(index * 2, (index + 1) * 2);
			bytes[index] = (byte) Integer.parseInt(str, 16);
		}

		// バイト配列を返す。
		return bytes;
	}
}
