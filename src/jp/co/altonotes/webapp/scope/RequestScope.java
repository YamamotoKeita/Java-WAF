package jp.co.altonotes.webapp.scope;

import javax.servlet.ServletRequest;

/**
 * Request�̃X�R�[�v
 *
 * @author Yamamoto Keita
 *
 */
public class RequestScope implements IScope {
	ServletRequest request;

	/**
	 *
	 * �R���X�g���N�^�[
	 *
	 * @param req
	 */
	public RequestScope(ServletRequest req) {
		this.request = req;
	}

	/*
	 * (�� Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Request";
	}

}
