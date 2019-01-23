package jp.co.altonotes.webapp.scope;

import javax.servlet.http.HttpSession;

/**
 * Session�̃X�R�[�v
 *
 * @author Yamamoto Keita
 *
 */
public class SessionScope implements IScope {

	private HttpSession session;

	/**
	 *
	 * �R���X�g���N�^�[
	 *
	 * @param session
	 */
	public SessionScope(HttpSession session) {
		this.session = session;
	}

	/*
	 * (�� Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (session != null) {
			return session.getAttribute(name);
		} else {
			return null;
		}
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Session";
	}

}
