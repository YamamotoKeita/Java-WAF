package jp.co.altonotes.webapp.scope;

import javax.servlet.ServletContext;

/**
 * Applicationのスコープ
 *
 * @author Yamamoto Keita
 *
 */
public class ApplicationScope implements IScope {

	private ServletContext application;

	/**
	 * コンストラクター
	 *
	 * @param application
	 */
	public ApplicationScope(ServletContext application) {
		this.application = application;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return application.getAttribute(name);
	}

}
