package jp.co.altonotes.webapp.form;

import java.io.Serializable;

/**
 * コンボボックスのオプションのインターフェース
 *
 * @author Yamamoto Keita
 *
 */
public interface ISelectOption extends Serializable {

	/**
	 * オプションの value の値を取得する。
	 * @return オプションの value の値
	 */
	public String getValue();

	/**
	 * オプションの表示ラベルを取得する。
	 * @return オプションの表示ラベル
	 */
	public String getLabel();
}
