package jp.co.altonotes.webapp.scope;

import javax.servlet.ServletContext;

/**
 * Application�̃X�R�[�v
 *
 * @author Yamamoto Keita
 *
 */
public class ApplicationScope implements IScope {

	private ServletContext application;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param application
	 */
	public ApplicationScope(ServletContext application) {
		this.application = application;
	}

	/*
	 * (�� Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return application.getAttribute(name);
	}

}
