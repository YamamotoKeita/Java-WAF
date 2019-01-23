package jp.co.altonotes.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 3DESによる暗号化・複合化を行う。
 * まだ、できてない。
 *
 * @author Yamamoto Keita
 *
 */
public class ThreeDESUtil {

	private static final String KEY_ALGORITHM = "DESede";
	private String algorithm = "DESede/CBC/NoPadding";
//	private String mode = "CBC";
//	private String padding = "NoPadding";

	/**
	 * データを暗号化する。
	 *
	 * @param data
	 * @param initialValue
	 * @param key
	 * @return 暗号化されたデータ
	 */
	public byte[] encrypt(byte[] data, byte[] initialValue, byte[] key) {
		return process(data, initialValue, key, false);
	}

	/**
	 * データを複合化する。
	 *
	 * @param data
	 * @param initialValue
	 * @param key
	 * @return 複合化されたデータ
	 */
	public byte[] decrypt(byte[] data, byte[] initialValue, byte[] key) {
		return process(data, initialValue, key, true);
	}

	/**
	 * データを暗号化/複合化する。
	 *
	 * @param data
	 * @param initialValue
	 * @param key
	 * @param decryptMode
	 * @return
	 */
	private byte[] process(byte[] data, byte[] initialValue, byte[] key, boolean decryptMode) {

		Cipher cipher = null;
		byte[] processedData = null;

		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		IvParameterSpec ivSpec = new IvParameterSpec(initialValue);

		try {
			cipher = Cipher.getInstance(algorithm);
			if (decryptMode) {
				cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
			}
			processedData = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException ignored) {
		} catch (NoSuchPaddingException ignored) {
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return processedData;
	}
}
