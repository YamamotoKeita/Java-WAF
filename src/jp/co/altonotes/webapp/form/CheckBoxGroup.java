package jp.co.altonotes.webapp.form;

import java.util.ArrayList;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;

/**
 * フォームのチェックボックスグループ
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBoxGroup {
	private ArrayList<String> valueList = new ArrayList<String>();

	/**
	 * 配列の値をセットする。
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
	 * 値をセットする。
	 *
	 * @param value
	 */
	public void setValues(String value) {
		setValues(new String[]{value});
	}

	/**
	 * チェックされた全てのチェックボックスのvalueを取得する。
	 *
	 * @return チェックされたチェックボックスのvalue
	 */
	public String[] getValues() {
		return valueList.toArray(new String[valueList.size()]);
	}

	/**
	 * 引数の値がチェックされているか判定する。
	 *
	 * @param value
	 * @return 引数の値がチェックされている場合<code>true</code>
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
	 * チェックがあるか判定する。
	 *
	 * @return チェックボックスが一つ以上チェックされている場合<code>true</code>
	 */
	public boolean hasCheck() {
		return 0 < valueList.size();
	}

	/**
	 * 全てのチェックを外す。
	 */
	public void removeAllCheck() {
		valueList = new ArrayList<String>();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckBoxGroup [valueList=" + TextUtils.combine(valueList, ",") + "]";
	}

}
