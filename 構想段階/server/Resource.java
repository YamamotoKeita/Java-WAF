
package jp.co.altonotes.webapp.server;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * コンテントの実データを保持する。
 *
 * @author Yamamoto Keita
 *
 */
public class Resource {

	protected byte[] binaryContent = null;
	protected InputStream inputStream = null;

	/**
	 * コンストラクター。
	 */
	public Resource() {}

	/**
	 * コンストラクター。
	 *
	 * @param inputStream
	 */
	public Resource(InputStream inputStream) {
		setContent(inputStream);
	}

	/**
	 * コンストラクター。
	 *
	 * @param binaryContent
	 */
	public Resource(byte[] binaryContent) {
		setContent(binaryContent);
	}

	/**
	 * コンテントの入力Streamを取得する。
	 *
	 * @return
	 * @throws IOException
	 */
	public InputStream streamContent()
		throws IOException {
		if (binaryContent != null) {
			return new ByteArrayInputStream(binaryContent);
		}
		return inputStream;
	}

	/**
	 * コンテントのバイナリデータを取得する。
	 *
	 * @return
	 */
	public byte[] getContent() {
		return binaryContent;
	}

	/**
	 * コンテントのInputStreamをセットする。
	 *
	 * @param inputStream
	 */
	public void setContent(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * コンテントのバイナリデータをセットする。
	 *
	 * @param binaryContent
	 */
	public void setContent(byte[] binaryContent) {
		this.binaryContent = binaryContent;
	}

}
