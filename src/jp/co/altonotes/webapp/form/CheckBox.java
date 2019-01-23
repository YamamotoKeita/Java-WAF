package jp.co.altonotes.webapp.form;

import jp.co.altonotes.util.Checker;

/**
 * �t�H�[���̃`�F�b�N�{�b�N�X
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBox {

	protected static String EMPTY_VALUE = "false";
	
	private String value;

	/**
	 * �z��̒l����Z�b�g����B
	 *
	 * @param values
	 */
	public void setValue(String[] values) {
		for (String value : values) {
			if (Checker.isNotEmpty(value)) {
				this.value = value;
				return;
			}
		}
	}

	/**
	 * �l���Z�b�g����B
	 *
	 * @param value
	 */
	public void setValue(String value) {
		if (Checker.isNotEmpty(value)) {
			this.value = value;
		}
	}

	/**
	 * @return �`�F�b�N�{�b�N�X��value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * �w�肵��value���܂܂�Ă��邩���肷��B
	 *
	 * @param value
	 * @return �w�肵��value���`�F�b�N����Ă���ꍇ<code>true</code>
	 */
	public boolean contains(String value) {
		return this.value != null && this.value.equals(value);
	}

	/**
	 * �`�F�b�N�{�b�N�X���`�F�b�N����Ă��邩���肷��B
	 * @return �`�F�b�N�{�b�N�X���`�F�b�N����Ă���ꍇ<code>true</code>
	 */
	public boolean isChecked() {
		return Checker.isNotEmpty(value) && !EMPTY_VALUE.equalsIgnoreCase(value);
	}

	/**
	 * �`�F�b�N���O���B
	 */
	public void removeCheck() {
		value = null;
	}

	@Override
	public String toString() {
		return "CheckBox [value=" + value + "]";
	}
}
