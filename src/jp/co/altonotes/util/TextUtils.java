package jp.co.altonotes.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * �l�������t�H�[�}�b�g��String�ւ̕ϊ�����сA
 * ����t�H�[�}�b�g��String����l�ւ̎擾���s���B
 *
 * @author Yamamoto Keita
 *
 */
public class TextUtils {

	/**
	 * �����񒆂́u&lt;�v�u&gt;�v�u &amp;�v�u&nbsp;�v�u&quot;�v��HTML���ꕶ���ɕϊ�����B
	 *
	 * @param str
	 * @return HTML�G�X�P�[�v���ꂽ������
	 */
	public static String htmlEscape(String str) {
		String res = str;

		// & �̓G�X�P�[�v�V�[�P���X�Ƃ��Ďg�p����̂ň�Ԑ�ɕϊ�����K�v������
		res = res.replace("&", "&amp;");

		res = res.replace("<", "&lt;");
		res = res.replace(">", "&gt;");
		res = res.replace(" ", "&nbsp;");
		res = res.replace("\"", "&quot;");

		return res;
	}

	/**
	 * ������̔z���A�����Ĉ�̕�����ɂ���B
	 *
	 * @param strs
	 * @param separater
	 * @return �A�����ꂽ������
	 */
	public static String combine(String[] strs, String separater) {
		if (strs == null || strs.length == 0) {
			return null;
		}else if (strs.length == 1) {
			return strs[0];
		}

		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str + separater);
		}
		return sb.substring(0, sb.length() - separater.length());
	}

	/**
	 * �������List��A�����Ĉ�̕�����ɂ���B
	 *
	 * @param strs
	 * @param separater
	 * @return ���X�g��A������������
	 */
	public static String combine(List<String> strs, String separater) {
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str + separater);
		}
		return sb.substring(0, sb.length() - separater.length());
	}

	/**
	 * �w�肵�������̘A���������p�X�y�[�X��Ԃ��B
	 *
	 * @param length
	 * @return �w�肵�������̘A���������p�X�y�[�X
	 */
	public static String blank(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * �w��̒����܂ō��[�ɓ���̕�����������������쐬����B
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return ���[���w��̕����Ŗ��߂�������
	 */
	public static String padLeft(long source, int size, char padding) {
		String str = String.valueOf(source);
		return padLeft(str, size, padding);
	}

	/**
	 * �w��̒����܂Ő擪�ɓ���̕�����������������쐬����B
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return �w��̒����܂ō��[�ɓ���̕���������������
	 */
	public static String padLeft(String source, int size, char padding) {
		int srcLen = source.length();
		if (size < srcLen) {
			source = source.substring(srcLen - size, srcLen);
		} else if (srcLen < size) {
			StringBuilder sb = new StringBuilder(size);
			for (int j = 0; j < size - srcLen; j++) {
				sb.append(padding);
			}
			sb.append(source);
			source = sb.toString();
		}
		return source;
	}

	/**
	 * �w��̒����܂ŉE�[�ɓ���̕�����������������쐬����B
	 *
	 * @param source
	 * @param size
	 * @param padding
	 * @return �w��̒����܂ŉE�[�ɓ���̕���������������
	 */
	public static String padRight(String source, int size, char padding) {
		int srcLen = source.length();
		if (size < srcLen) {
			source = source.substring(0, size);
		} else if (srcLen < size) {
			StringBuilder sb = new StringBuilder(size);
			sb.append(source);
			for (int j = 0; j < size - srcLen; j++) {
				sb.append(padding);
			}
			source = sb.toString();
		}
		return source;
	}

	/**
	 * �����񖖔��ɂ���A[]�Ɉ͂܂ꂽ���l��int�Ƃ��Ď擾����B<br>
	 * ��jarray[1]�̏ꍇ�A1���Ԃ�B<br>
	 * <br>
	 * �ȉ��̏ꍇ��-1��Ԃ��B
	 * <ul>
	 * <li>������[]�����݂��Ȃ�</li>
	 * <li>[]���𐔒l�Ƃ��ĔF���ł��Ȃ�</li>
	 * <li>[]�������̐�</li>
	 * </ul>
	 *
	 * @param str
	 * @return �����񖖔���[]�Ɉ͂܂ꂽ���l
	 */
	public static int parseArrayIndex(String str) {
		int aryIdx = -1;
		String idxStr = null;
		int head = 0;
		int tail = 0;

		head = str.indexOf("[");
		if (head == -1) {
			return -1;
		}

		tail = str.indexOf("]", head + 2);
		if (tail != str.length() - 1) {
			return -1;
		}

		idxStr = str.substring(head + 1, tail);
		if (Checker.isNumber(idxStr)) {
			aryIdx = Integer.parseInt(idxStr);
		} else {
			return -1;
		}

		if (0 <= aryIdx) {
			return aryIdx;
		} else {
			return -1;
		}
	}

	/**
	 * URL����v���g�R�����擾����B
	 *
	 * @param url
	 * @return �v���g�R��
	 */
	public static String parseProtocol(String url) {
		int idx = url.indexOf("://");
		if (idx == -1) {
			return null;
		}
		return url.substring(0, idx);
	}

	/**
	 * URL����z�X�g�����擾����B
	 *
	 * @param url
	 * @param getPort 
	 * @return �z�X�g��
	 */
	public static String parseHostName(String url, boolean getPort) {
		String name = null;
		if (url.startsWith("http://")) {
			name = url.substring(7);
		} else if (url.startsWith("https://")) {
			name = url.substring(8);
		} else {
			return null;
		}

		int idx = name.indexOf("/");
		if (idx != -1) {
			name = name.substring(0, idx);
		}
		if (!getPort) {
			idx = name.indexOf(":");
			if (idx != -1) {
				name = name.substring(0, idx);
			}
		}
		return name;
	}

	/**
	 * URL����z�X�g���A�N�G������菜�����h�L�������g�p�X�����o���B
	 *
	 *
	 * @param url
	 * @return �h�L�������g�p�X
	 */
	public static String parseDocumentPath(String url) {
		String path = null;
		if (url.startsWith("http://")) {
			path = url.substring(7);
		} else if (url.startsWith("https://")) {
			path = url.substring(8);
		} else {
			return null;
		}

		int idx = path.indexOf("/");
		if (idx == -1) {
			return "";
		}
		path = path.substring(idx);
		idx = path.indexOf("?", path.lastIndexOf("/"));
		if (idx != -1) {
			path = path.substring(0, idx);
		}
		return path;
	}

	/**
	 * �Ō�̃X���b�V���ȍ~�̕�������擾����B
	 * �X���b�V�����܂܂�Ȃ��ꍇ�A���������̂܂ܕԂ��B
	 *
	 * @param path
	 * @return �p�X�̍Ō�̃X���b�V���ȍ~�̕�����
	 */
	public static String getLastPath(String path) {
		int idx = path.lastIndexOf("/");
		if (idx == -1) {
			return path;
		}
		path = path.substring(idx + 1);
		idx = path.indexOf("?", idx);
		if (idx != -1) {
			path = path.substring(0, idx);
		}
		return path;
	}

	/**
	 * �t�@�C������p�X����g���q�i�����̃h�b�g�ȍ~�̕�����j���擾����B
	 *
	 * @param name
	 * @return �g���q
	 */
	public static String getExtention(String name) {
		int idx = name.lastIndexOf('.');
		if (idx == -1) {
			return null;
		} else {
			return name.substring(idx + 1, name.length());
		}
	}

	/**
	 * ������̉E�[�������̕��������̂����B
	 *
	 * @param str
	 * @param c
	 * @return �E�[�̓���̕������̂�����������
	 */
	public static String rightTrim(String str, char c) {
		char[] chars = str.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			if (chars[i] != c) {
				return str.substring(0, i+1);
			}
		}
		return "";
	}

	/**
	 * ������̍��[�������̕��������̂����B
	 *
	 * @param str
	 * @param c
	 * @return ���[�̓���̕������̂�����������
	 */
	public static String leftTrim(String str, char c) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != c) {
				return str.substring(0, chars.length - i);
			}
		}
		return "";
	}

	/**
	 * ������̗��[���畡����ނ̕���������̂����B
	 *
	 * @param str
	 * @param excludes
	 * @return �g��������������
	 */
	//TODO System.arraycopy ���g���Ď�������
//	public static String trim(String str, String[] excludes) {
//		// �����̃f�[�^���Ԃ��󂳂Ȃ��悤�R�s��
//		String[] copy = Arrays.copyOf(excludes, excludes.length);
//
//		// �Ⴆ�� "a"��"ab"����菜���ꍇ�A"a"���Ɏ�菜����"b"�������c��"ab"����菜���Ȃ��B
//		// ���̂��ߕ��������������Ƀ}�b�`����悤���בւ���
//		Arrays.sort(copy, new StringLengthComparatorASC());
//		excludes = copy;
//
//		int start = 0;
//		boolean goNext = true;
//		while (goNext) {
//			goNext = false;
//			for (int i = 0; i < excludes.length; i++) {
//				if (str.startsWith(excludes[i], start)) {
//					start += excludes[i].length();
//					goNext = true;
//					break;
//				}
//			}
//		}
//
//		int end = str.length();
//		goNext = true;
//		while (goNext) {
//			goNext = false;
//			for (int i = 0; i < excludes.length; i++) {
//				int len = excludes[i].length();
//				if (str.indexOf(excludes[i], end - len) == end - len) {
//					end -= len;
//					goNext = true;
//					break;
//				}
//			}
//		}
//
//		return str.substring(start, end);
//	}

	/**
	 * ��������w�肵�������R�[�h�Ńo�C�g�z��ɂ���B
	 * UnsupportedEncodingException��throw�����A�ϊ��Ɏ��s�����ꍇnull��Ԃ��B
	 *
	 * @param str
	 * @param charset
	 * @return �������\���o�C�g�z��
	 */
	public static byte[] getBytes(String str, String charset) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes(charset);
		} catch (UnsupportedEncodingException ignoted) {}
		return bytes;
	}

	/**
	 * ��������X�y�[�X�ŕ�������
	 * @param src
	 * @return �X�y�[�X�ŕ�������������̔z��
	 */
	public static String[] splitBySpace(String src) {
		List<String> list = new ArrayList<String>();
		int head = 0;
		int tail = 0;
		boolean space = true;

		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) == ' ') {
				if (!space) {
					tail = i;
					list.add(src.substring(head, tail));
					space = true;
				}
			} else if (space){
				head = i;
				space = false;
			}
		}

		if (!space) {
			list.add(src.substring(head, src.length()));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Throwable����X�^�b�N�g���[�X�𕶎���Ƃ��Ď��o���B
	 *
	 * @param ex
	 * @return �X�^�b�N�g���[�X�̕�����
	 */
	public static String stackTraceToString(Throwable ex) {
		StringWriter sw = null;
		PrintWriter  pw = null;

		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			return sw.toString();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ���p�J�^�J�i�A�S�p�J�^�J�i��S�p�Ђ炪�Ȃɕϊ�����
	 *
	 * @param str
	 * @return �S�p�Ђ炪�Ȃɕϊ�����������
	 */
	public static String toZenkakuHiragana(String str) {
		//TODO ��������
		return null;
	}

	/**
	 * ���p�J�^�J�i�A�S�p�J�^�J�i��S�p�Ђ炪�Ȃɕϊ�����
	 * @param c
	 * @return �S�p�Ђ炪�Ȃɕϊ���������
	 */
	public static char toZenkakuHiragana(char c) {
		if (12354 <= c && c <= 12435) {
			return (char)(c + 96);
		} else {
			return c;
		}
	}

	/**
	 * ���p�J�^�J�i�A�S�p�Ђ炪�Ȃ�S�p�J�^�J�i�ɕϊ�����
	 *
	 * @param str
	 * @return �S�p�J�^�J�i�ɕϊ�����������
	 */
	public static String toZenkakuKatakana(String str) {
		//TODO ��������
		return null;
	}

	/**
	 * ���p���Ȃɕϊ�����
	 *
	 * @param str
	 * @return ���p���Ȃɕϊ�����������
	 */
	public static String toHankakuKana(String str) {
		//TODO ��������
		return null;
	}

	/**
	 * X / _ / X�u ��̂��V�X�e���Ƀ��b�Z�[�W���o�͂���� �v
	 *
	 * @param obj
	 */
	public static void yunoOut(Object obj) {
		System.out.print("X / _ / X�u ");
		System.out.print(obj);
		System.out.println(" �v");
	}

}
