package jp.co.altonotes.webapp.scope;

import javax.servlet.http.HttpSession;

/**
 * Sessionのスコープ
 *
 * @author Yamamoto Keita
 *
 */
public class SessionScope implements IScope {

	private HttpSession session;

	/**
	 *
	 * コンストラクター
	 *
	 * @param session
	 */
	public SessionScope(HttpSession session) {
		this.session = session;
	}

	/*
	 * (非 Javadoc)
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
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Session";
	}

}
