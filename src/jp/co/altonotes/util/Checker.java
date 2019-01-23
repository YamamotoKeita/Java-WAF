package jp.co.altonotes.util;

import java.util.Collection;
import java.util.Map;

/**
 * ������Ȃǂ̌��؂��s���B
 *
 * @author Yamamoto Keita
 *
 */
public class Checker {

	/**
	 * �����Ɏw�肵��������null�܂��͋󕶎����ǂ������肷��B
	 *
	 * @param str
	 * @return �����̕�����null�܂��͋󕶎��̏ꍇ<code>true</code>
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �����Ɏw�肵��������null�܂��͋󕶎��łȂ����ǂ������肷��B
	 *
	 * @param str
	 * @return �����Ɏw�肵��������null�܂��͋󕶎��łȂ��ꍇ<code>true</code>
	 */
	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	/**
	 * �����Ɏw�肵��������null�A�󕶎��܂��͋󔒂����肷��B
	 *
	 * @param str
	 * @return �����Ɏw�肵��������null�A�󕶎��܂��͋󔒂̏ꍇ<code>true</code>
	 */
	public static boolean isBlank(CharSequence str) {
		if (isEmpty(str)) {
			return true;
		}

		int strLen = str.length();

		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * �����̕����񂪋󔒈ȊO�̕������܂ނ����肷��B
	 *
	 * @param str
	 * @return �����̕����񂪋󔒈ȊO�̕������܂ޏꍇ<code>true</code>
	 */
	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	/**
	 * �����񂪑S�Đ������ǂ������肷��
	 *
	 * @param str
	 * @return �����񂪑S�Đ����̏ꍇ<code>true</code>
	 */
	public static boolean isNumber(String str) {
		if (isNotEmpty(str)) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!Character.isDigit(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * �������������ǂ������肷��
	 *
	 * @param c
	 * @return �����������̏ꍇ<code>true</code>
	 */
	public static boolean isNumber(char c) {
		return Character.isDigit(c);
	}

	/**
	 * �����񂪐��l��\�����ǂ������肷��B
	 * +, -, �����_���܂߂Ă��悢�B
	 *
	 * @param str
	 * @return �����񂪐��l�\���ꍇ<code>true</code>
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * ������"http://"�܂���"https://"�Ŏn�܂邩���肷��B
	 *
	 * @param str
	 * @return ������"http://"�܂���"https://"�Ŏn�܂�ꍇ<code>true</code>
	 */
	public static boolean isHttpURL(String str) {
		if (str != null) {
			return str.startsWith("http://") || str.startsWith("https://");
		} else {
			return false;
		}
	}

	/**
	 * �����񂪏�����or�啶���A���t�@�x�b�g�����肷��B
	 *
	 * @param str
	 * @return �����񂪏�����or�啶���A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * ������������or�啶���A���t�@�x�b�g�����肷��B
	 * @param c
	 * @return ������������or�啶���A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isAlpha(char c) {
		return isLowerAlpha(c) || isUpperAlpha(c);
	}

	/**
	 * �����񂪑S�ď������A���t�@�x�b�g�����肷��B
	 *
	 * @param str
	 * @return �����񂪑S�ď������A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isLowerAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isLowerAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * �������������A���t�@�x�b�g�����肷��B
	 *
	 * @param c
	 * @return �������������A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isLowerAlpha(char c) {
		if (97 <= c && c <= 122) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �����񂪑S�đ啶���A���t�@�x�b�g�����肷��B
	 *
	 * @param str
	 * @return �����񂪑S�đ啶���A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isUpperAlpha(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isUpperAlpha(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * �������啶���A���t�@�x�b�g�����肷��B
	 *
	 * @param c
	 * @return �������啶���A���t�@�x�b�g�̏ꍇ<code>true</code>
	 */
	public static boolean isUpperAlpha(char c) {
		if (65 <= c && c <= 90) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �����񂪑S�Ĕ��p�p��������сA�L�[�{�[�h�œ��͂ł���L�������肷��B
	 *
	 * @param str
	 * @return �����񂪑S�Ĕ��p�p��������сA�L�[�{�[�h�œ��͂ł���L���̏ꍇ<code>true</code>
	 */
	public static boolean isKeybordCharacter(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isKeybordCharacter(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * ���������p�p��������сA�L�[�{�[�h�œ��͂ł���L�������肷��B
	 *
	 * @param c
	 * @return ���������p�p��������сA�L�[�{�[�h�œ��͂ł���L���̏ꍇ<code>true</code>
	 */
	public static boolean isKeybordCharacter(char c) {
		if (33 <= c && c <= 126) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �����񂪔��p�J�i�i���p���_�A���p�����_�A���p�����܂ށj�����肷��B
	 *
	 * @param str
	 * @return �����񂪔��p�J�i�̏ꍇ<code>true</code>
	 */
	public static boolean isHankakuKana(String str) {
		if (str != null) {
			char[] chs = str.toCharArray();
			for (char c : chs) {
				if (!isHankakuKana(c)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * ���������p�J�i�i���p���_�A���p�����_�A���p�����܂ށj�����肷��B
	 *
	 * @param c
	 * @return ���������p�J�i�̏ꍇ<code>true</code>
	 */
	public static boolean isHankakuKana(char c) {
		if (65383 <= c && c <= 65439) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �����񃊃X�g�Ɏw�肵�������񂪊܂܂�邩���肷��B
	 *
	 * @param list
	 * @param checked
	 * @return �����񃊃X�g�Ɏw�肵�������񂪊܂܂��ꍇ<code>true</code>
	 */
	public static boolean contains(Collection<String> list, String checked) {
		return contains(list.toArray(new String[list.size()]), checked, true);
	}

	/**
	 * ������z��Ɏw�肵�������񂪊܂܂�邩���肷��B
	 *
	 * @param array
	 * @param checked
	 * @return ������z��Ɏw�肵�������񂪊܂܂��ꍇ<code>true</code>
	 */
	public static boolean contains(String[] array, String checked) {
		return contains(array, checked, true);
	}

	/**
	 * �����񃊃X�g�Ɏw�肵�������񂪊܂܂�邩���肷��B
	 * �啶������������ʂ��Ȃ��B
	 *
	 * @param list
	 * @param checked
	 * @return �����񃊃X�g�Ɏw�肵�������񂪊܂܂��ꍇ<code>true</code>
	 */
	public static boolean containsIgnoreCase(Collection<String> list, String checked) {
		return contains(list.toArray(new String[list.size()]), checked, false);
	}

	/**
	 * ������z��Ɏw�肵�������񂪊܂܂�邩���肷��B
	 * �啶������������ʂ��Ȃ��B
	 *
	 * @param array
	 * @param checked
	 * @return ������z��Ɏw�肵�������񂪊܂܂��ꍇ<code>true</code>
	 */
	public static boolean containsIgnoreCase(String[] array, String checked) {
		return contains(array, checked, false);
	}

	/**
	 * ������z��Ɏw�肵�������񂪊܂܂�邩���肷��B
	 *
	 * @param array
	 * @param checked
	 * @param caseSensitive
	 * @return
	 */
	private static boolean contains(String[] array, String checked, boolean caseSensitive) {
		boolean hit = false;
		for (String string : array) {
			if (caseSensitive) {
				if (string.equals(checked)) {
					hit = true;
					break;
				}
			} else {
				if (string.equalsIgnoreCase(checked)) {
					hit = true;
					break;
				}
			}
		}
		return hit;
	}

	/**
	 * �z�񂪋󂩔��肷��B
	 *
	 * @param array
	 * @return �z�񂪋�̏ꍇ<code>true</code>
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * �R���N�V�������󂩔��肷��B
	 *
	 * @param collection
	 * @return �R���N�V��������̏ꍇ<code>true</code>
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Map���󂩔��肷��B
	 *
	 * @param map
	 * @return Map����̏ꍇ<code>true</code>
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * ���p/�S�p�A�Ђ炪��/�J�^�J�i����ʂ����A�Q�̕�����̓��{��̓ǂݕ��������Ȃ�true��Ԃ��B
	 *
	 * @param str1
	 * @param str2
	 * @return ���p/�S�p�A�Ђ炪��/�J�^�J�i����ʂ����A�Q�̕�����̓��{��̓ǂݕ��������Ȃ�<code>true</code>
	 */
	public static boolean soundsSameInJapanese(String str1, String str2) {
		//�X�y�[�X�𔲂�
		str1 = str1.replaceAll(" ", "");
		str1 = str1.replaceAll("�@", "");
		str2 = str2.replaceAll(" ", "");
		str2 = str2.replaceAll("�@", "");

		//TODO ��������B

		return str1.equals(str2);
	}

}
