package jp.co.altonotes.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jp.co.altonotes.io.LineIterator;

/**
 * CSV�t�@�C���̓ǂݍ��݂��s���B
 *
 * @author Yamamoto Keita
 *
 */
public class CSVReader {

	private static final byte[] ZERO_BYTE = new byte[0];

	private ArrayList<CSVLine> lines = new ArrayList<CSVLine>();

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param str
	 */
	public CSVReader(String str) {
		LineIterator iter = new LineIterator(str);
		while (iter.hasNext()) {
			lines.add(new CSVLine(iter.nextLine()));
		}
	}

	/**
	 * File��ǂݍ��݃C���X�^���X���쐬����B
	 *
	 * @param file
	 * @param charset
	 * @return �w�肵��File��ǂݍ��񂾃��[�_�[
	 * @throws IOException
	 */
	public static CSVReader readFromFile(File file, String charset) throws IOException {
		if (!isSupportedEncoding(charset)) {
			throw new IllegalArgumentException("�T�|�[�g����Ă��Ȃ������R�[�h�ł��F" + charset);
		}

		InputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			byte[] buf = new byte[1024];
			int size = 0;
			in = new FileInputStream(file);
			while ((size = in.read(buf)) > 0) {
				baos.write(buf, 0, size);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String str = new String(baos.toByteArray(), charset);
		return new CSVReader(str);
	}

	/**
	 * �S�s��z��Ƃ��Ď擾����B
	 * @return CSV�̑S�s�̔z��
	 */
	public CSVLine[] lines() {
		return lines.toArray(new CSVLine[lines.size()]);
	}

	/**
	 * �����Ɏw�肵���s�����o���B
	 *
	 * @param row
	 * @return �����Ɏw�肵���s
	 */
	public CSVLine getLine(int row) {
		return lines.get(row);
	}

	/**
	 * �P�s��String�z��Ŏ��o���B
	 *
	 * @param row
	 * @return CSV�̂P�s���J���}�ŕ�������������z��
	 */
	public String[] getLineAsArray(int row) {
		return lines.get(row).toStringArray();
	}

	/**
	 * �s����Ԃ��B
	 *
	 * @return CSV�̍s��
	 */
	public int lineCount() {
		return lines.size();
	}

	/**
	 * �����̕����R�[�h���T�|�[�g����Ă��邩���肷��B
	 *
	 * @param charset
	 * @return �����̕����R�[�h���T�|�[�g����Ă���ꍇ <code>true</code>
	 */
	public static boolean isSupportedEncoding(String charset) {
		try {
			new String(ZERO_BYTE, charset);
			return true;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (CSVLine line : lines) {
			temp.append(line.toString() + "\n");
		}
		return temp.toString();
	}
}
