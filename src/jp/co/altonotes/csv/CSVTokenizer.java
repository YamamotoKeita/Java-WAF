package jp.co.altonotes.csv;

import java.util.NoSuchElementException;

/**
 * 1�s��CSV�`���̃f�[�^����͂��A���ꂼ��̍��ڂɕ�������N���X�B
 * CSV�`���ɑΉ����� java.util.StringTokenizer �̂悤�Ȃ��́B
 *
 * @author Yamamoto Keita
 *
 */
public class CSVTokenizer {
	private String source;                    // �ΏۂƂȂ镶����
	private int currentPosition;              // ���̓ǂݏo���ʒu
	private int maxPosition;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param line CSV�`���̕�����  ���s�R�[�h���܂܂Ȃ��B
	 */
	public CSVTokenizer(String line) {
		source = line;
		maxPosition = line.length();
	}

	/**
	 * ���̍��ڂ̕������Ԃ��B
	 *
	 * @return ���̍���
	 * @exception NoSuchElementException ���ڂ��c���Ă��Ȃ��Ƃ�
	 */
	public String nextToken() {
		if (currentPosition > maxPosition) {
			return null;
		}
		int idx = currentPosition;
		int end = nextComma(currentPosition);

		StringBuffer temp = new StringBuffer();
		while (idx < end) {
			char ch = source.charAt(idx);
			// "���A���ő����ꍇ
			if (ch == '"' && (idx < end - 1) && (source.charAt(idx + 1) == '"')) {
				temp.append(ch);
				idx ++;
			} else {
				temp.append(ch);
			}
			idx++;
		}

		if (temp.length() > 0) {
			if (temp.charAt(0) == '"' && temp.charAt(temp.length() - 1) == '"') {
				temp.deleteCharAt(temp.length() - 1);
				temp.deleteCharAt(0);
			}
		}
		currentPosition = end + 1;
		return temp.toString();
	}

	/**
	 * ���̃J���}������ʒu��Ԃ��B
	 * �J���}���c���Ă��Ȃ��ꍇ�� nextComma() == maxPosition �ƂȂ�B
	 * �܂��Ō�̍��ڂ���̏ꍇ�� nextComma() == maxPosition �ƂȂ�B
	 *
	 * @param idx �������J�n����ʒu
	 * @return ���̃J���}������ʒu�B�J���}���Ȃ��ꍇ�́A�������
	 * �����̒l�ƂȂ�B
	 */
	private int nextComma(int idx) {
		boolean inQuote = false;
		while (idx < maxPosition) {
			char ch = source.charAt(idx);
			if (!inQuote && ch == ',') {
				break;
			} else if ('"' == ch) {
				inQuote = !inQuote;       // ""�̏����������OK
			}
			idx ++;
		}
		return idx;
	}
}
