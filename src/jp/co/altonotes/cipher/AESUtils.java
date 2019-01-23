package jp.co.altonotes.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jp.co.altonotes.net.util.Base64;

/**
 * @author Yamamoto Keita
 */
public class AESUtils {

	private static final String ALGORITHM = "AES";
	private static final String ECB_METHOD = "AES/ECB/PKCS5Padding";
	private static final String CBC_METHOD = "AES/CBC/PKCS5Padding";

	// テスト用
	private static final String CHARSET = "UTF-8";
	private static final String KEY = "cyg1K58AkJnBiUIvKImnEA==";
	private static final String IV  = "L3nzJad+KqjD67d8zkFHMg==";
	
	public static void main(String[] args) throws Exception {
		String srcStr = "ABCDEFあいう1234567890";
		
		byte[] srcData = srcStr.getBytes(CHARSET);
		byte[] keyData = Base64.decodeBase64(KEY);
		byte[] iv = Base64.decodeBase64(IV);
        
        byte[] encrypt = encrypt(srcData, keyData, iv);

        System.out.println("[encrypt] " + encrypt.length + " : " + Base64.encodeBase64String(encrypt));
        
	}
	
    public static byte[] encrypt(byte[] src, byte[] keyData, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CBC_METHOD);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(keyData, ALGORITHM);
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }
 
    public static byte[] decrypt(byte[] src, byte[] key, byte[] iv) throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CBC_METHOD);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(src);
    }
    
	/**
	 * データを暗号化する
	 * 
	 * @param src 暗号化するデータ
	 * @param keyData 鍵データ
	 * @return 暗号化したデータ
	 * @throws Exception
	 */
	public static byte[] encryptECB(byte[] src, byte[] keyData) throws Exception {
		Cipher cipher = Cipher.getInstance(ECB_METHOD);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyData, ALGORITHM));
		return cipher.doFinal(src);
	}

	/**
	 * データを復号化する
	 * 
	 * @param src 復号化するデータ
	 * @param keyData 鍵データ
	 * @return 復号化したデータ
	 * @throws Exception
	 */
	public static byte[] decryptECB(byte[] src, byte[] keyData) throws Exception {
		Cipher cipher = Cipher.getInstance(ECB_METHOD);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyData, ALGORITHM));
		return cipher.doFinal(src);
	}
}
