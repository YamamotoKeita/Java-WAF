package jp.co.altonotes.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

/**
 * データ入出力、ファイル操作総合ユーティリティー。
 * Commons IOUtils, FileUtilsにほぼ同等の機能がある。
 *
 * @author Yamamoto Keita
 *
 */
public class IOUtils {

	/**
	 * カレントディレクトリをFileインスタンスとして取得する。
	 *
	 * @return カレントディレクトリ
	 */
	public static File getCurrentDirectory() {
		return new File(".").getAbsoluteFile().getParentFile();
	}

	/**
	 * カレントディレクトリのパスを取得する。
	 *
	 * @return カレントディレクトリのパス
	 */
	public static String currentPath() {
		return getCurrentDirectory().getAbsolutePath();
	}

	/**
	 * ファイルからバイナリデータを読み取る。
	 * #FileUtils.readFileToByteArray(file)
	 *
	 * @param file
	 * @return ファイルから読み取ったバイナリデータ
	 * @throws IOException
	 */
	public static byte[] read(File file) throws IOException {
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
			close(in);
		}

		return baos.toByteArray();
	}

	/**
	 * 引数のパスにあたるファイルからバイナリデータを読み取る。
	 *
	 * @param path
	 * @return ファイルから読み取ったバイナリデータ
	 * @throws IOException
	 */
	public static byte[] read(String path) throws IOException{
		return read(new File(path));
	}

	/**
	 * ファイルから文字列を読み取る。
	 *
	 * @param file
	 * @param charset
	 * @return ファイルから読み取った文字列
	 * @throws IOException
	 */
	public static String readString(File file, String charset) throws IOException {
		byte[] data = read(file);
		return new String(data, charset);
	}

	/**
	 * 引数のパスのファイルから文字列を読み取る。
	 *
	 * @param path
	 * @param charset
	 * @return ファイルから読み取った文字列
	 * @throws IOException
	 */
	public static String readStringFromPath(String path, String charset) throws IOException {
		byte[] data = read(path);
		return new String(data, charset);
	}

	/**
	 * InputStreamから文字列を読み込む
	 *
	 * @param in
	 * @param charset
	 * @return 読み込んだ文字列
	 * @throws IOException
	 */
	public static String readString(InputStream in, String charset) throws IOException {
		byte[] data = read(in);
		return new String(data, charset);
	}

	/**
	 * InputStreamから読み取れる全バイナリデータを読み取り、読み取り後Closeする。
	 *
	 * @param in
	 * @return InputStreamから読み取ったバイナリデータ
	 * @throws IOException
	 */
	public static byte[] read(InputStream in) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			byte[] buf = new byte[1024];
			int size = 0;
			while((size = in.read(buf)) > 0){
				baos.write(buf, 0, size);
			}
		} finally {
			close(in);
		}

		return baos.toByteArray();
	}

	/**
	 * ファイルからオブジェクトを読み込む。
	 *
	 * @param file
	 * @return ファイルから読み取ったオブジェクト
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream in = null;
		Object obj = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			obj = in.readObject();
		} finally {
			close(in);
		}
		return obj;
	}

	/**
	 * ファイルからプロパティを読み込む。
	 *
	 * @param file
	 * @return ファイルから読み取ったプロパティ
	 * @throws IOException
	 */
	public static Properties readProperties(File file) throws IOException {
		Properties prop = new Properties();

		InputStream in = null;

		try {
			in = new FileInputStream(file);
			prop.load(in);
		} finally {
			close(in);
		}

		return prop;
	}

	/**
	 * ファイルにバイナリデータを書き込む。
	 *
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void write(File file, byte[] data) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);

		try {
			fout.write(data, 0, data.length);
		} finally {
			close(fout);
		}
	}

	/**
	 * ファイルの終端にデータを追記する。
	 * @param file
	 * @param str
	 * @param charset 
	 * @throws IOException
	 */
	public static void append(File file, String str, String charset) throws IOException {
		FileOutputStream fo = null;

		try {
			fo = new FileOutputStream(file, true); //追記フラグ true で作成
			fo.write(str.getBytes(charset));
		} finally {
			close(fo);
		}
	}

	/**
	 * ファイルにバイナリデータを書き込む。
	 *
	 * @param path
	 * @param data 
	 * @throws IOException
	 */
	public static void write(String path, byte[] data) throws IOException {
		write(new File(path), data);
	}

	/**
	 * ファイルに文字列データを書き込む。
	 *
	 * @param file
	 * @param str
	 * @param charset
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void writeString(File file, String str, String charset) throws IOException {
		write(file, str.getBytes(charset));
	}

	/**
	 * ファイルにオブジェクトを書き込む。
	 *
	 * @param file
	 * @param obj
	 * @throws IOException
	 */
	public static void writeObject(File file, Object obj) throws IOException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(obj);
		} finally {
			close(out);
		}
	}

	/**
	 * OutputStreamにデータを書き込み、書き込み後Closeする。
	 *
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public static void write(OutputStream out, byte[] data) throws IOException{
		try {
			out.write(data);
		} finally {
			close(out);
		}
	}

	/**
	 * リソースをbyte[]で取得する。
	 *
	 * @param klass
	 * @param path
	 * @return 読み込んだリソースのバイナリデータ
	 * @throws IOException
	 */
	public static byte[] loadResource(Class<?> klass, String path) throws IOException{
		InputStream in = null;
		byte[] data = null;
		in = klass.getResourceAsStream(path);

		if (in == null) {
			URL url = null;
			try {
				url = klass.getResource(path);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			if (url != null) {
				throw new FileNotFoundException(url + " が見つかりません。");
			} else {
				throw new FileNotFoundException(path + " が見つかりません。");
			}
		}

		data = read(in);
		return data;
	}

	/**
	 * リソースをbyte[]で取得する。
	 *
	 * @param loader
	 * @param path
	 * @return 読み込んだリソースのバイナリデータ
	 * @throws IOException
	 */
	public static byte[] loadResource(ClassLoader loader, String path) throws IOException{
		InputStream in = null;
		byte[] data = null;
		in = loader.getResourceAsStream(path);

		if (in == null) {
			URL url = null;
			try {
				url = loader.getResource(path);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			if (url != null) {
				throw new FileNotFoundException(url + " が見つかりません。");
			} else {
				throw new FileNotFoundException(path + " が見つかりません。");
			}
		}

		data = read(in);
		return data;
	}

	/**
	 * 引数のパスが表すリソースにデータを書き込む
	 *
	 * @param klass
	 * @param path
	 * @param data
	 * @throws IOException
	 */
	public static void writeResource(Class<?> klass, String path, byte[] data) throws IOException {
		File file = new File(klass.getResource(path).getPath());
		write(file, data);
	}

	/**
	 * HttpServletResponseにバイナリデータを書き込む。
	 * いかなる場合にも書き込みストリームはcloseされる。
	 *
	 * @param resp
	 * @param data
	 * @throws IOException
	 */
	public static void write(HttpServletResponse resp, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = resp.getOutputStream();
			out.write(data);
		} finally {
			close(out);
		}
	}

	/**
	 * InputStreamをcloseする。
	 * 引数がnullの場合何もしない。
	 * IOExceptionが発生してもExceptionをスローしない。
	 *
	 * @param in
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * OutputStreamをcloseする。
	 * 引数がnullの場合何もしない。
	 * IOExceptionが発生してもExceptionをスローしない。
	 *
	 * @param out
	 */
	public static void close(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writerをクローズする。
	 * 引数がnullの場合何もしない。
	 * IOExceptionが発生してもExceptionをスローしない。
	 *
	 * @param writer
	 */
	public static void close(Writer writer) {
		if (writer == null) {
			return;
		}

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Readerをcloseする。
	 * 引数がnullの場合何もしない。
	 * IOExceptionが発生してもExceptionをスローしない。
	 *
	 * @param reader
	 */
	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {}
		}
	}
}
