package jp.co.altonotes.webapp.form;

import java.util.ArrayList;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;

/**
 * �t�H�[���̃`�F�b�N�{�b�N�X�O���[�v
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBoxGroup {
	private ArrayList<String> valueList = new ArrayList<String>();

	/**
	 * �z��̒l���Z�b�g����B
	 *
	 * @param values
	 */
	public void setValues(String[] values) {
		valueList = new ArrayList<String>();
		for (String value : values) {
			if (Checker.isNotEmpty(value) && !CheckBox.EMPTY_VALUE.equalsIgnoreCase(value)) {
				valueList.add(value);
			}
		}
	}

	/**
	 * �l���Z�b�g����B
	 *
	 * @param value
	 */
	public void setValues(String value) {
		setValues(new String[]{value});
	}

	/**
	 * �`�F�b�N���ꂽ�S�Ẵ`�F�b�N�{�b�N�X��value���擾����B
	 *
	 * @return �`�F�b�N���ꂽ�`�F�b�N�{�b�N�X��value
	 */
	public String[] getValues() {
		return valueList.toArray(new String[valueList.size()]);
	}

	/**
	 * �����̒l���`�F�b�N����Ă��邩���肷��B
	 *
	 * @param value
	 * @return �����̒l���`�F�b�N����Ă���ꍇ<code>true</code>
	 */
	public boolean contains(String value) {
		for (String contain : valueList) {
			if (contain.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �`�F�b�N�����邩���肷��B
	 *
	 * @return �`�F�b�N�{�b�N�X����ȏ�`�F�b�N����Ă���ꍇ<code>true</code>
	 */
	public boolean hasCheck() {
		return 0 < valueList.size();
	}

	/**
	 * �S�Ẵ`�F�b�N���O���B
	 */
	public void removeAllCheck() {
		valueList = new ArrayList<String>();
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckBoxGroup [valueList=" + TextUtils.combine(valueList, ",") + "]";
	}

}
