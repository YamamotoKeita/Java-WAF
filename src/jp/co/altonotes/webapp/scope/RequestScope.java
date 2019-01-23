package jp.co.altonotes.webapp.scope;

import javax.servlet.ServletRequest;

/**
 * Requestのスコープ
 *
 * @author Yamamoto Keita
 *
 */
public class RequestScope implements IScope {
	ServletRequest request;

	/**
	 *
	 * コンストラクター
	 *
	 * @param req
	 */
	public RequestScope(ServletRequest req) {
		this.request = req;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Request";
	}

}
