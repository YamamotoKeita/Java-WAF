package jp.co.altonotes.csv;

import java.util.ArrayList;

/**
 * CSV�f�[�^�̂P�s��\���N���X�B
 *
 * @author Yamamoto Keita
 *
 */
public class CSVLine {
	private ArrayList<CSVElement> items = new ArrayList<CSVElement>();

	/**
	 * �R���X�g���N�^
	 */
	public CSVLine() {}

	/**
	 * �R���X�g���N�^
	 *
	 * @param line
	 */
	public CSVLine(String line) {
		CSVTokenizer ct = new CSVTokenizer(line);
		String str = null;
		while ((str = ct.nextToken()) != null) {
			items.add(new CSVElement(str));
		}
	}

	/**
	 * ������z��ɕϊ�����B
	 *
	 * @return CSV�̈�s���J���}��؂�ŕ�������������̔z��
	 */
	public String[] toStringArray() {
		ArrayList<String> list = new ArrayList<String>(items.size());
		for (CSVElement element : items) {
			list.add(element.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * �������ǉ�����B
	 *
	 * @param str �ǉ����镶����
	 */
	public void addItem(String str) {
		items.add(new CSVElement(str));
	}

	/**
	 * 1�s�̍��ڐ���Ԃ��B
	 *
	 * @return CSVLine�Ɋ܂�ł��鍀�ڂ̐�
	 */
	public int size() {
		return items.size();
	}

	/**
	 * n�Ԗڂ̍��ڂ� String �ŕԂ��B
	 *
	 * @param idx ���ڂ̔ԍ� [0 �` size()-1]
	 * @return n�Ԗڂ̕�����B�G���N�H�[�g�͂��Ȃ��B
	 */
	public String getItem(int idx) {
		CSVElement element = items.get(idx);
		return element.toString();
    }

	/**
	 * ���ڂ��폜����B
	 *
	 * @param n ���ڂ̔ԍ� [0 �` size()-1]
	 */
	public void removeItem(int n) {
		items.remove(n);
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer list = new StringBuffer();
		for (int i = 0; i < items.size(); i ++) {
			CSVElement element = items.get(i);
			String item = element.toString();
			list.append(item);
			if (items.size() - 1 != i) {
				list.append(',');
			}
		}
		return list.toString();
	}

}
