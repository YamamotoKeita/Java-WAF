package jp.co.altonotes.unix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * �V�F���R�}���h�����s����
 *
 * @author Yamamoto Keita
 *
 */
public class CommandExecuter {

	/**
	 * �R�}���h�����s�����ʂ��擾����B
	 *
	 * @param command
	 * @return �R�}���h���s�̌���
	 */
	public static String exec(String command) {

		Runtime runtime = Runtime.getRuntime();
		if (runtime == null) {
			throw new IllegalStateException("Runtime���擾�ł��܂���ł����B");
		}

		Process process = null;

		try {
			process = runtime.exec(command);
			process.waitFor(); // ���s�҂�

			InputStream in = process.getInputStream();
			String res = readString(in, "UTF-8");
			return res;
		} catch (IOException e) {
			throw (IllegalStateException) new IllegalStateException("IO�G���[���������܂����B").initCause(e);
		} catch (InterruptedException e) {
			throw (IllegalStateException) new IllegalStateException("�R�}���h������ҋ@���ɏ��������f����܂����B").initCause(e);
		}
	}

	/**
	 * �R�}���h�����s�����ʂ��擾����B
	 *
	 * @param command
	 * @return �R�}���h���s�̌���
	 */
	public static String exec(String[] command) {

		Runtime runtime = Runtime.getRuntime();
		if (runtime == null) {
			throw new IllegalStateException("Runtime���擾�ł��܂���ł����B");
		}

		Process process = null;

		try {
			process = runtime.exec(command);
			process.waitFor(); // ���s�҂�

			InputStream in = process.getInputStream();
			String res = readString(in, "UTF-8");
			return res;
		} catch (IOException e) {
			throw (IllegalStateException) new IllegalStateException("IO�G���[���������܂����B").initCause(e);
		} catch (InterruptedException e) {
			throw (IllegalStateException) new IllegalStateException("�R�}���h������ҋ@���ɏ��������f����܂����B").initCause(e);
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
