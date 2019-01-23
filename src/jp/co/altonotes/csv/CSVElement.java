package jp.co.altonotes.csv;

/**
 * CSV�̂P�s�Ɋ܂܂�邻�ꂼ��̍��ڂ�\���N���X�B
 *
 * @author Yamamoto Keita
 */
public class CSVElement {
	private String source;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param str
	 * @param enquote
	 */
	protected CSVElement(String str) {
		source = str;
	}

	/**
	 * �K�v�ȏꍇ�̓G���N�H�[�g���ĕ������Ԃ��B
	 *
	 * @return �K�X�G���N�H�[�g���ꂽ������
	 */
	public String getCSVString() {
		return enquote(source, false);
	}

	/**
	 * �����I�ɃG���N�H�[�g�����������Ԃ��B
	 *
	 * @return �G���N�H�[�g���ꂽ������
	 */
	public String getEnquotedString() {
		return enquote(source, true);
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return source;
	}

	/**
	 * �����̕������ CSV �ŏo�͂ł���`�ɕϊ�����B<br>
	 * ������ " �� , ���܂�ł���Ƃ��ɂ́A" �� "" �ɒu�������A������S�̂� " �ň͂ށB
	 *
	 * forceEnquote �� true �̏ꍇ�A " �� , ���܂܂Ȃ��ꍇ�ł������I�� " �ŕ�������͂ށB
	 *
	 * @param str ����������������
	 * @param forceEnquote true�Ȃ狭���I�ɃG���N�H�[�g����
	 * @return item ����������������
	 */
	private static String enquote(String str, boolean forceEnquote) {
		if (str.length() == 0) {
			return str;
		}
		if (str.indexOf('"') < 0 && str.indexOf(',') < 0 && forceEnquote == false) {
			return str;
        }

		// �S�Ă̕����� " ���� "" �ɕϊ�����ꍇ�̒�����z��
		StringBuffer sb = new StringBuffer(str.length() * 2 + 2);
		sb.append('"');
		sb.append(str.replaceAll("\"", "\"\""));
		sb.append('"');

		return new String(sb);
	}

}

