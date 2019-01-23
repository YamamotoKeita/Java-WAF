package jp.co.altonotes.webapp.scope;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

/**
 * Request, Session, Page, Application全てを含めたScope。
 * getAttributeのみ対応。setAttributeはできない。
 *
 * @author Yamamoto Keita
 *
 */
public class FullScope implements IScope {
	private ServletContext application;
	private HttpSession session;
	private ServletRequest request;
	private PageContext page;

	/**
	 * コンストラクタ
	 *
	 * @param application
	 * @param session
	 * @param request
	 * @param page
	 */
	public FullScope(ServletContext application, HttpSession session, ServletRequest request, PageContext page) {
		this.application = application;
		this.session = session;
		this.request = request;
		this.page = page;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.scope.Scope#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		Object obj = null;
		if (page != null) {
			obj = page.getAttribute(name);
		}
		if (obj != null) {
			return obj;
		}

		if (request != null) {
			obj = request.getAttribute(name);
		}
		if (obj != null) {
			return obj;
		}

		if (session != null) {
			obj = session.getAttribute(name);
		}
		if (obj != null) {
			return obj;
		}

		if (application != null) {
			obj = application.getAttribute(name);
		}

		return null;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Request, Session, PageContext, ServletContext";
	}

}
