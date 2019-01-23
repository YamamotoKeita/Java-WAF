
package jp.co.altonotes.webapp.server;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * �R���e���g�̎��f�[�^��ێ�����B
 *
 * @author Yamamoto Keita
 *
 */
public class Resource {

	protected byte[] binaryContent = null;
	protected InputStream inputStream = null;

	/**
	 * �R���X�g���N�^�[�B
	 */
	public Resource() {}

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param inputStream
	 */
	public Resource(InputStream inputStream) {
		setContent(inputStream);
	}

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param binaryContent
	 */
	public Resource(byte[] binaryContent) {
		setContent(binaryContent);
	}

	/**
	 * �R���e���g�̓���Stream���擾����B
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
	 * �R���e���g�̃o�C�i���f�[�^���擾����B
	 *
	 * @return
	 */
	public byte[] getContent() {
		return binaryContent;
	}

	/**
	 * �R���e���g��InputStream���Z�b�g����B
	 *
	 * @param inputStream
	 */
	public void setContent(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * �R���e���g�̃o�C�i���f�[�^���Z�b�g����B
	 *
	 * @param binaryContent
	 */
	public void setContent(byte[] binaryContent) {
		this.binaryContent = binaryContent;
	}

}
