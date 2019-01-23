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
 * CSVファイルの読み込みを行う。
 *
 * @author Yamamoto Keita
 *
 */
public class CSVReader {

	private static final byte[] ZERO_BYTE = new byte[0];

	private ArrayList<CSVLine> lines = new ArrayList<CSVLine>();

	/**
	 * コンストラクター。
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
	 * Fileを読み込みインスタンスを作成する。
	 *
	 * @param file
	 * @param charset
	 * @return 指定したFileを読み込んだリーダー
	 * @throws IOException
	 */
	public static CSVReader readFromFile(File file, String charset) throws IOException {
		if (!isSupportedEncoding(charset)) {
			throw new IllegalArgumentException("サポートされていない文字コードです：" + charset);
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
	 * 全行を配列として取得する。
	 * @return CSVの全行の配列
	 */
	public CSVLine[] lines() {
		return lines.toArray(new CSVLine[lines.size()]);
	}

	/**
	 * 引数に指定した行を取り出す。
	 *
	 * @param row
	 * @return 引数に指定した行
	 */
	public CSVLine getLine(int row) {
		return lines.get(row);
	}

	/**
	 * １行をString配列で取り出す。
	 *
	 * @param row
	 * @return CSVの１行をカンマで分割した文字列配列
	 */
	public String[] getLineAsArray(int row) {
		return lines.get(row).toStringArray();
	}

	/**
	 * 行数を返す。
	 *
	 * @return CSVの行数
	 */
	public int lineCount() {
		return lines.size();
	}

	/**
	 * 引数の文字コードがサポートされているか判定する。
	 *
	 * @param charset
	 * @return 引数の文字コードがサポートされている場合 <code>true</code>
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
	 * (非 Javadoc)
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
