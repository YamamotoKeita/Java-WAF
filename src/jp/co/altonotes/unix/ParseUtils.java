package jp.co.altonotes.unix;

import java.util.ArrayList;
import java.util.List;

/**
 * Linux�R�}���h�̏o�͂��p�[�X���邽�߂̃��[�e�B���e�B�[
 *
 * @author Yamamoto Keita
 *
 */
public class ParseUtils {

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

}
