package jp.co.altonotes.webapp.form;

/**
 * コンボボックスのオプション。<br>
 * SelectOptionのシンプルな実装。
 *
 * @author Yamamoto Keita
 */
public class SelectItem implements ISelectOption {

	private static final long serialVersionUID = -7264284206604543056L;
	
	private String value;
	private String label;

	/**
	 * コンストラクター
	 *
	 * @param value
	 * @param label
	 */
	public SelectItem(String value, String label) {
		this.value = value;
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * @see jp.co.altonotes.webapp.form.SelectOption#getValue()
	 */
	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see jp.co.altonotes.webapp.form.SelectOption#getLabel()
	 */
	public String getLabel() {
		return label;
	}


}
