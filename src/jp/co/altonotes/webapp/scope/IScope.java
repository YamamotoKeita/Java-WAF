package jp.co.altonotes.webapp.scope;

/**
 * Request, Session, Page, Applicationら各種スコープを画一的に扱うためのインターフェース。
 *
 * @author Yamamoto Keita
 *
 */
public interface IScope {
	/**
	 * @param name
	 * @return 引数の名前のattribute
	 */
	public Object getAttribute(String name);
}
