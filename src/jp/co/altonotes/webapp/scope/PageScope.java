package jp.co.altonotes.webapp.scope;

import javax.servlet.jsp.PageContext;

/**
 * Pageのスコープ
 *
 * @author Yamamoto Keita
 *
 */
public class PageScope implements IScope {
	PageContext page;

	/**
	 *
	 * コンストラクター
	 *
	 * @param page
	 */
	public PageScope(PageContext page) {
		this.page = page;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return page.getAttribute(name);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PageContext";
	}

}
