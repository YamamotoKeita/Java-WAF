package webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.altonotes.util.TextUtils;

/**
 * HttpServletRequestをラップし、フレームワーク管理のセッション機能を提供する。
 *
 * @author Yamamoto Keita
 *
 */
public class ZigzagFireRequest implements HttpServletRequest {

	private HttpServletRequest mRequest = null;
	private SessionContainer mContainer = null;
	private ZigzagFireSession mAppSession = null;

	/**
	 * コンストラクター。
	 *
	 * @param req
	 */
	public ZigzagFireRequest(HttpServletRequest req) {
		mRequest = req;
	}

	/**
	 * コンストラクター。
	 *
	 * @param req
	 * @param container
	 */
	public ZigzagFireRequest(HttpServletRequest req, SessionContainer container) {
		mRequest = req;
		mContainer = container;
		mAppSession = container.getSession(req);
		if (mAppSession != null) {
			mAppSession.update();
		}
	}

	/**
	 * コンストラクター。
	 *
	 * @param req
	 * @param session
	 * @param container
	 */
	public ZigzagFireRequest(HttpServletRequest req, ZigzagFireSession session, SessionContainer container) {
		mRequest = req;
		mContainer = container;
		mAppSession = session;
		if (mAppSession != null) {
			mAppSession.update();
		}
	}

	/**
	 * フレームワーク管理のセッションを使っているか判定する。
	 *
	 * @return
	 */
	public boolean isUsingAppSession() {
		return mContainer != null;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (mContainer == null) {
			return mRequest.getSession(create);
		} else {
			if (mAppSession != null) {
				if (!mAppSession.isExpire()) {
					return mAppSession;
				} else {
					mAppSession = null;
				}
			}

			if (create) {
				mAppSession = mContainer.createSession();
				return mAppSession;
			} else {
				return null;
			}
		}
	}

	@Override
	public String getAuthType() {
		return mRequest.getAuthType();
	}

	@Override
	public String getContextPath() {
		return mRequest.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return mRequest.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return mRequest.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return mRequest.getHeader(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getHeaderNames() {
		return mRequest.getHeaderNames();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getHeaders(String arg0) {
		return mRequest.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return mRequest.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return mRequest.getMethod();
	}

	@Override
	public String getPathInfo() {
		return mRequest.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return mRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return mRequest.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return mRequest.getRemoteUser();
	}

	@Override
	public String getRequestURI() {
		return mRequest.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return mRequest.getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return mRequest.getRequestedSessionId();
	}

	@Override
	public String getServletPath() {
		return mRequest.getServletPath();
	}

	@Override
	public Principal getUserPrincipal() {
		return mRequest.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return mRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return mRequest.isRequestedSessionIdFromURL();
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return mRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return mRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return mRequest.isUserInRole(arg0);
	}

	@Override
	public Object getAttribute(String arg0) {
		return mRequest.getAttribute(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAttributeNames() {
		return mRequest.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return mRequest.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return mRequest.getContentLength();
	}

	@Override
	public String getContentType() {
		return mRequest.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return mRequest.getInputStream();
	}

	@Override
	public String getLocalAddr() {
		return mRequest.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return mRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return mRequest.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return mRequest.getLocale();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getLocales() {
		return mRequest.getLocales();
	}

	@Override
	public String getParameter(String arg0) {
		return mRequest.getParameter(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map getParameterMap() {
		return mRequest.getParameterMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getParameterNames() {
		return mRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return mRequest.getParameterValues(arg0);
	}

	@Override
	public String getProtocol() {
		return mRequest.getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return mRequest.getReader();
	}

	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		return mRequest.getRealPath(arg0);
	}

	@Override
	public String getRemoteAddr() {
		return mRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return mRequest.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return mRequest.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return mRequest.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return mRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return mRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return mRequest.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return mRequest.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		mRequest.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		mRequest.setAttribute(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		mRequest.setCharacterEncoding(arg0);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("unchecked")
	public String toString() {
		StringBuffer temp = new StringBuffer(100);
		temp.append("[" + mRequest.getMethod() + "] ");
		temp.append(mRequest.getRequestURL());
		String query = mRequest.getQueryString();
		if (query != null) {
			temp.append("?" + query);
		}

		Enumeration<String> nameList = mRequest.getParameterNames();

		while (nameList.hasMoreElements()) {
			String name = nameList.nextElement();
			String[] values = mRequest.getParameterValues(name);
			temp.append("\n");
			if (values.length == 1) {
				temp.append(name + ": " + values[0]);
			} else {
				temp.append(name + ": " + TextUtils.combine(values, ", "));
			}
		}

		return temp.toString();
	}
}
