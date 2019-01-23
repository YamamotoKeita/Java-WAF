package jp.co.altonotes.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.zip.CompressionUtils;

/**
 * Http��Response��\���N���X�B
 * �w�b�_�ƃ{�f�B�̂Q�ō\�������B
 *
 * @author Yamamoto Keita
 *
 */
public class HttpResponse {

	private HttpHeader header;
	private byte[] body;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param header ���X�|���X�̃w�b�_
	 * @param body ���X�|���X�̃{�f�B
	 */
	public HttpResponse(HttpHeader header, byte[] body) {
		this.header = header;
		this.body = body;
	}

	/**
	 * �w�b�_��Content-Type�����āA���̃��X�|���X��HTML�����肷��B
	 *
	 * @return ���̃��X�|���X��HTML�̏ꍇtrue
	 */
	public boolean isHtml() {
		return header.isHtml();
	}

	/**
	 * �w�b�_��Content-Type�����āA���̃��X�|���X�̕����R�[�h���擾����B
	 *
	 * @return ���̃��X�|���X�̕����R�[�h
	 */
	public String getCharset() {
		return header.getCharset();
	}

	/**
	 * �w�b�_��Content-Encoding�����āA���̃��X�|���X�f�[�^�̈��k�������擾����B
	 *
	 * @return Content-Encoding�̒l
	 */
	public String getContentEncoding() {
		return header.getContentEncoding();
	}

	/**
	 * ���̃��X�|���X��HttpHeader�����o���B
	 *
	 * @return ���̃��X�|���X�̃w�b�_��\��HttpHeader�C���X�^���X
	 */
	public HttpHeader getHeader() {
		return header;
	}

	/**
	 * ���̃��X�|���X�̃I���W�i���̃{�f�B�f�[�^���擾����B
	 * ���k����Ă����ꍇ���𓀂��Ă��Ȃ����ߒ��ӂ��K�v�B
	 *
	 * @return ���̃��X�|���X�̃{�f�B�̃o�C�i���f�[�^
	 */
	public byte[] getRawBody() {
		return body;
	}

	/**
	 * ���̃��X�|���X�̃{�f�B�f�[�^���擾����B
	 * �w�b�_��Content-Encoding�Ɉ��k�������L����Ă����ꍇ�A
	 * ���ꂪ���m�̕����ł���Ή𓀂����f�[�^��Ԃ��B
	 *
	 * @return �𓀂��ꂽ���X�|���X�f�[�^
	 */
	public byte[] getDecodedBody() {
		String encoding = getContentEncoding();
		if (encoding == null) {
			return body;
		}
		if (encoding.equalsIgnoreCase("gzip")) {
			return CompressionUtils.decodeGZIP(body);
		} else if (encoding.equalsIgnoreCase("deflate")) {
			return CompressionUtils.decodeDeflate(body);
		} else if (encoding.equalsIgnoreCase("compress")) {
			//TODO compress�̉𓀂���������
			System.out.println("[�x��]compress���k�f�[�^�̉𓀂͖������ł��B");
			return body;
		} else {
			System.out.println("[�x��]" + encoding + "�͖��m�̈��k�����̂��߉𓀂ł��܂���B");
			return body;
		}
	}

	/**
	 * ���̃��X�|���X�̃{�f�B�f�[�^���AContent-Encoding�̈��k�����AContent-Type�̕����R�[�h��K�p���ĕ�����Ƃ��ĕԂ��B
	 * Content-Type��charset�������ꍇ�Ajava�f�t�H���g�����R�[�h�ŕ����񉻂��邽�߁A���������e�ł���ۏ؂͖����B
	 *
	 * @return ���̃��X�|���X�̃{�f�B������
	 */
	public String getBodyString() {
		String res = null;
		String charset = getCharset();
		if (charset == null) {
			res = new String(getDecodedBody());
		} else {
			try {
				res = new String(getDecodedBody(), charset);
			} catch (UnsupportedEncodingException ignored) {
				res = new String(getDecodedBody());
			}
		}
		return res;
	}

	/**
	 * Set-Cookie, Set-Cookie2�̒l��String�z��Ƃ��Ď擾����B
	 *
	 * @return Cookie�̒l�̔z��
	 */
	public String[] getCookieValues() {
		return header.getResponseCookies();
	}

	/**
	 * �{�f�B�f�[�^���Z�b�g����
	 *
	 * @param body
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * �w�b�_�[�̒l���V�X�e���ɏo�͂���
	 */
	public void printHeader() {
		header.print();
	}

	/**
	 * HTTP�w�b�_�ɋL�q���ꂽLocation�i���_�C���N�g��URL�j�̒l�����o���B
	 *
	 * @return Location�i���_�C���N�g��URL�j�̒l
	 */
	public String getLocationURL() {
		return header.getParameter(HeaderNames.LOCATION);
	}

	/**
	 * HttpServletResponse �ɂ��̃C���X�^���X���ێ����郌�X�|���X�f�[�^���o�͂���B
	 *
	 * @param resp
	 * @throws IOException
	 */
	public void write(HttpServletResponse resp) throws IOException {
		header.write(resp);
		IOUtils.write(resp, body);
	}

}
