package jp.co.altonotes.webapp.form;

import jp.co.altonotes.util.Checker;

/**
 * フォームのチェックボックス
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBox {

	protected static String EMPTY_VALUE = "false";
	
	private String value;

	/**
	 * 配列の値を一つセットする。
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
	 * 値をセットする。
	 *
	 * @param value
	 */
	public void setValue(String value) {
		if (Checker.isNotEmpty(value)) {
			this.value = value;
		}
	}

	/**
	 * @return チェックボックスのvalue
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 指定したvalueが含まれているか判定する。
	 *
	 * @param value
	 * @return 指定したvalueがチェックされている場合<code>true</code>
	 */
	public boolean contains(String value) {
		return this.value != null && this.value.equals(value);
	}

	/**
	 * チェックボックスがチェックされているか判定する。
	 * @return チェックボックスがチェックされている場合<code>true</code>
	 */
	public boolean isChecked() {
		return Checker.isNotEmpty(value) && !EMPTY_VALUE.equalsIgnoreCase(value);
	}

	/**
	 * チェックを外す。
	 */
	public void removeCheck() {
		value = null;
	}

	@Override
	public String toString() {
		return "CheckBox [value=" + value + "]";
	}
}
