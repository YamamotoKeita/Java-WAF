package jp.co.altonotes.zip;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import jp.co.altonotes.io.IOUtils;

/**
 * データの圧縮、解凍を行うクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class CompressionUtils {

	/**
	 * 指定したファイルまたはフォルダをZIPファイルにする。
	 * @param srcPath
	 * @param targetPath
	 * @throws IOException
	 */
	public static void createZipFile(String srcPath, String targetPath, String charset) throws IOException{
		ZipOutputStream zipout = null;
		File srcFile = new File(srcPath);
		byte[] buf = new byte[1024];

		if (!srcFile.canRead()) {
			throw new FileNotFoundException(srcPath + " が読み取れません");
		}

		try {
			zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(targetPath))));
			zipout.setEncoding(charset);
			putZipEntry(zipout, srcFile, "/", buf);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(targetPath + " に書き込めません");
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if(zipout!=null) try{zipout.close();} catch(Exception e) {}
		}
	}

	private static void putZipEntry(ZipOutputStream zipout, File src, String parentPath, byte[] buf) throws IOException{

		if (src.isDirectory()) {
			parentPath = parentPath + src.getName() + "/";
			zipout.putNextEntry(new ZipEntry(parentPath));

			File[] files = src.listFiles();
			for (File file : files) {
				putZipEntry(zipout, file, parentPath, buf);
			}
		} else {
			ZipEntry entry = new ZipEntry(parentPath + src.getName());
			zipout.putNextEntry(entry);

			InputStream in = null;
			int rsize = 0;
			try {
				in = new BufferedInputStream(new FileInputStream(src));
				while ((rsize=in.read(buf)) > 0) {
					zipout.write(buf, 0, rsize);
				}
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				IOUtils.close(in);
			}
		}
	}

	/**
	 * deflate形式の入力ストリームからデコードしたバイナリデータを取り出す。
	 *
	 * @param in
	 * @return デコードしたバイナリデータ
	 * @throws IOException
	 */
	public static byte[] decodeDeflate(InputStream in) throws IOException{
		InflaterInputStream iin = null;
		byte[] data = null;
		try {
			iin = new InflaterInputStream(in);
			data = IOUtils.read(iin);
		} finally {
			IOUtils.close(iin);
		}
		return data;
	}

	/**
	 * deflate形式のバイナリデータからデコードしたバイナリデータを取り出す。
	 *
	 * @param src
	 * @return デコードしたバイナリデータ
	 */
	public static byte[] decodeDeflate(byte[] src) {
		byte[] decoded = null;
		try {
			decoded = decodeDeflate(new ByteArrayInputStream(src));
		} catch (IOException e) {
			//メモリ上のデータからの解凍なのでIOExceptionはほぼ有り得ないと思われる。
			e.printStackTrace();
		}
		return decoded;
	}

	/**
	 * GZIP形式の入力ストリームからデコードしたバイナリデータを取り出す。
	 *
	 * @param in
	 * @return デコードしたバイナリデータ
	 * @throws IOException
	 */
	public static byte[] decodeGZIP(InputStream in) throws IOException{
		GZIPInputStream gin = null;
		byte[] data = null;
		try {
			gin = new GZIPInputStream(in);
			data = IOUtils.read(gin);
		} finally {
			IOUtils.close(gin);
		}
		return data;
	}

	/**
	 * GZIP形式のバイナリデータからデコードしたバイナリデータを取り出す。
	 *
	 * @param src
	 * @return デコードしたバイナリデータ
	 */
	public static byte[] decodeGZIP(byte[] src) {
		byte[] decoded = null;
		try {
			decoded = decodeGZIP(new ByteArrayInputStream(src));
		} catch (IOException ignored) {}//メモリ上のデータからの解凍なのでIOExceptionはほぼ有り得ないと思われる。
		return decoded;
	}
}
