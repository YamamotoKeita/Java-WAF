package jp.co.altonotes.cipher;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import jp.co.altonotes.net.util.Base64;

/**
 * ARC4アルゴリズムによる暗号化・複合化を行うクラス
 * 上手く動いてるのかよく分からん。
 *
 * @author Yamamoto Keita
 *
 */
public class ARC4 {
	/** 共通鍵による配列		*/
	private int[] box;

	private final static String DEFAULT_CHARSET = "UTF-8";

	/**
	 * メインメソッド。テスト用。
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		ARC4 proc = ARC4.getProcessor("てきとう文字列");
		byte[] data = "999999".getBytes();
		byte[] encoded = proc.process(data);
		String string = Base64.encodeBase64URLSafeString(encoded);
		System.out.println(string);
		byte[] decoded = proc.process(encoded);
		System.out.println(new String(decoded));
	}

	/**
	 * 鍵をセットしてARC4インスタンスを生成する。
	 * 暗号化・複合化はこのメソッドによって生成されたインスタンスを使用して行う。
	 *
	 * 鍵の長さは任意だが、一般に128ビット(16バイト)以上が十分強固と考えられている。
	 * ただし、2056ビット(257バイト)以上の鍵は、鍵の後部256バイトしか使用されないので無意味。
	 *
	 * @param key
	 * @return 引数のkeyで作成された<code>ARC4</code>インスタンス
	 */
	public static ARC4 getProcessor(byte[] key){
		ARC4 arc4 = new ARC4();
		arc4.setKey(key);
		return arc4;
	}

	/**
	 * 文字列をキーにしてARC4インスタンスを生成
	 *
	 * @param key
	 * @return 引数のkeyで作成された<code>ARC4</code>インスタンス
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
	 * 共通鍵をセットする
	 * @param key
	 */
	private void setKey(byte[] key){

		//ボックス初期化
		box = new int[256];
		for (int i = 0; i < box.length; i++) {
			box[i] = i;
		}

		//ボックス2に鍵セット
		int[] box2 = new int[256];
		int keyLength = key.length;
		for (int i = 0; i < box2.length; i++) {
			box2[i] = 0xFF & key[i%keyLength];
		}

		//ボックスシャッフル
		int j = 0;
		int temp = 0;
		for (int i = 0; i < box.length; i++) {
			j = (j + box[i] + box2[i]) % 256;

			//mBox[i]とmBox[j]を入れ替え
			temp = box[i];
			box[i] = box[j];
			box[j] = temp;
		}
	}

	/**
	 * データを暗号化・複合化する
	 * ARC4において暗号化・複合化は全く同じ処理
	 * 平文を引数に渡せば暗号化文が、暗号化文を引数に渡せば元の平文が返る
	 *
	 * @param data
	 * @return 暗号化または複合化されたデータ
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
