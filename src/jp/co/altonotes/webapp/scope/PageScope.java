package jp.co.altonotes.webapp.scope;

import javax.servlet.jsp.PageContext;

/**
 * Page�̃X�R�[�v
 *
 * @author Yamamoto Keita
 *
 */
public class PageScope implements IScope {
	PageContext page;

	/**
	 *
	 * �R���X�g���N�^�[
	 *
	 * @param page
	 */
	public PageScope(PageContext page) {
		this.page = page;
	}

	/*
	 * (�� Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return page.getAttribute(name);
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PageContext";
	}

}
