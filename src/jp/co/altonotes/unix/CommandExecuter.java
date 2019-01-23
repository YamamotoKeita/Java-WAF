package jp.co.altonotes.unix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * シェルコマンドを実行する
 *
 * @author Yamamoto Keita
 *
 */
public class CommandExecuter {

	/**
	 * コマンドを実行し結果を取得する。
	 *
	 * @param command
	 * @return コマンド実行の結果
	 */
	public static String exec(String command) {

		Runtime runtime = Runtime.getRuntime();
		if (runtime == null) {
			throw new IllegalStateException("Runtimeが取得できませんでした。");
		}

		Process process = null;

		try {
			process = runtime.exec(command);
			process.waitFor(); // 実行待ち

			InputStream in = process.getInputStream();
			String res = readString(in, "UTF-8");
			return res;
		} catch (IOException e) {
			throw (IllegalStateException) new IllegalStateException("IOエラーが発生しました。").initCause(e);
		} catch (InterruptedException e) {
			throw (IllegalStateException) new IllegalStateException("コマンド完了を待機中に処理が中断されました。").initCause(e);
		}
	}

	/**
	 * コマンドを実行し結果を取得する。
	 *
	 * @param command
	 * @return コマンド実行の結果
	 */
	public static String exec(String[] command) {

		Runtime runtime = Runtime.getRuntime();
		if (runtime == null) {
			throw new IllegalStateException("Runtimeが取得できませんでした。");
		}

		Process process = null;

		try {
			process = runtime.exec(command);
			process.waitFor(); // 実行待ち

			InputStream in = process.getInputStream();
			String res = readString(in, "UTF-8");
			return res;
		} catch (IOException e) {
			throw (IllegalStateException) new IllegalStateException("IOエラーが発生しました。").initCause(e);
		} catch (InterruptedException e) {
			throw (IllegalStateException) new IllegalStateException("コマンド完了を待機中に処理が中断されました。").initCause(e);
		}
	}

	private static String readString(InputStream in, String charset) throws IOException {
		byte[] data = read(in);
		return new String(data, charset);
	}

	private static byte[] read(InputStream in) throws IOException{
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

	private static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
