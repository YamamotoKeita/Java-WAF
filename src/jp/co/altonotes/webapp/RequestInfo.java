package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.PropertyAccessor.AccessResult;
import jp.co.altonotes.webapp.util.MessageList;

/**
 * HTTP���N�G�X�g�ɕR�Â�����ێ�����B<br>
 * HttpServletRequest, HttpServletResponse�Ȃǂ̋@�\���ւ���B
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestInfo {

	/** ���b�Z�[�W�̃A�g���r���[�g�� */
	public static final String ATTRIBUTE_MESSAGES = "messages";

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String originalPath;
	private String path;
	private String htmlType;
	private boolean isAvailableURLSession;
	private MobileInfo mobileHeader;
	private MessageList messageList = new MessageList();;
	private Throwable systemError;
	private String systemErrorMessage;
	private Map<String, String> variableMap;
	private ServletContext servletContext;

	/**
	 * 
	 * @param request
	 * @return ���N�G�X�g�̃p�X
	 */
	protected static String getRequestPath(HttpServletRequest request) {
		String path = request.getPathInfo();
		if (path == null) {
			path = request.getServletPath();
		}
		return path;
	}
	
	/**
	 * �R���X�g���N�^�[
	 *
	 * @param req
	 */
	protected RequestInfo(HttpServletRequest req) {
		originalPath = getRequestPath(req);
		this.request = req;
	}

	/**
	 * ���N�G�X�g�ƃ��X�|���X�̏����Z�b�g����
	 * @param req
	 */
	protected void setSource(HttpServletRequest req, HttpServletResponse resp) {
		this.request = req;
		this.response = resp;
		path = getRequestPath(req);
	}

	/**
	 * �p�X�ϐ����Z�b�g����
	 *
	 * @param variableMap
	 */
	protected void setPathVariable(Map<String, String> variableMap) {
		if (this.variableMap == null) {
			this.variableMap = variableMap;
		} else {
			Set<Entry<String, String>> entrySet = variableMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				variableMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * ���N�G�X�g���ꂽ�p�X���擾����B
	 * Context�p�X�̌�납��A�N�G��������̑O�܂ł̕�����B
	 * �ŏ��Ƀ��N�G�X�g���ꂽ�p�X�ł���A�ʂ̃p�X�Ƀ��N�G�X�g�� foward ���Ă��ύX����Ȃ��B
	 *
	 * @return ���N�G�X�g�p�X�BContext�p�X����уN�G���͊܂܂Ȃ��B
	 */
	protected String getOriginalPath() {
		return originalPath;
	}

	/**
	 * HTML�̃^�C�v��ݒ肷��
	 *
	 * @param type
	 */
	protected void setHTMLType(String type) {
		this.htmlType = type;
	}

	/**
	 * ���̃��N�G�X�g�ɑ΂��ĕ\������y�[�W��XHTML�����肷��B
	 *
	 * @return �\������y�[�W��XHTML�̏ꍇ<code>true</code>
	 */
	public boolean isXHTMLPage() {
		if (htmlType != null && htmlType.equalsIgnoreCase("xhtml")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * URL�ɕt�����ꂽ�Z�b�V����ID�ɂ��Z�b�V�����Ǘ����s�����ݒ肷��B<br>
	 * <code>true</code>��ݒ肵���ꍇ�AJSP�^�O��URL�������o���ۂɃZ�b�V����ID���t�������B<br>
	 * �������ACookie���L���ȏꍇ��Cookie�ɂ��Z�b�V�����Ǘ����s����AURL�ɃZ�b�V����ID�͕t������Ȃ��B
	 *
	 * @param flag
	 */
	protected void enableURLSession(boolean flag) {
		this.isAvailableURLSession = flag;
	}

	/**
	 * URL�̃Z�b�V����ID�ɂ��Z�b�V�����Ǘ����L�������肷��B<br>
	 * ���̃��\�b�h��<code>true</code>��Ԃ��ꍇ�AJSP�^�O��URL�������o���ۂɃZ�b�V����ID���t�������B<br>
	 * �������ACookie���L���ȏꍇ��Cookie�ɂ��Z�b�V�����Ǘ����s����AURL�ɃZ�b�V����ID�͕t������Ȃ��B<br>
	 *
	 * @return URL�ɕt�����ꂽ�Z�b�V����ID�ɂ��Z�b�V�����Ǘ����s���ꍇ<code>true</code>
	 */
	public boolean isAvailableURLSession() {
		return isAvailableURLSession;
	}

	/**
	 * �p�X�ϐ����擾����B
	 *
	 * @param name
	 * @return
	 */
	protected String getPathVariable(String name) {
		if (variableMap == null) {
			return null;
		}
		return variableMap.get(name);
	}

	/**
	 * �p�X�ϐ��}�b�v���擾����B
	 *
	 * @return
	 */
	protected Map<String, String> getPathVariableMap() {
		return variableMap;
	}

	/**
	 * �g�ђ[���̏����擾����B
	 *
	 * @return
	 */
	protected MobileInfo getMobileInfo() {
		if (mobileHeader == null) {
			mobileHeader = MobileInfo.createFromRequest(this.request);
		}
		return mobileHeader;
	}

	/**
	 * HttpServletRequest���擾����B
	 *
	 * @return ���̃��N�G�X�g�ɕR�Â�HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * HttpServletResponse���擾����B
	 *
	 * @return ���̃��N�G�X�g�ɕR�Â�HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * HttpSession���擾����B���݂��Ȃ��ꍇnull��Ԃ��B
	 *
	 * @return
	 */
	protected HttpSession getSession() {
		return request.getSession(false);
	}

	/**
	 * �G���[���b�Z�[�W��ǉ�����B
	 *
	 * @param message
	 */
	protected void addMessage(String message) {
		messageList.add(message);
		request.setAttribute(ATTRIBUTE_MESSAGES, messageList);
	}

	/**
	 * �G���[���b�Z�[�W���擾����B
	 *
	 * @return
	 */
	protected MessageList getMessages() {
		return messageList;
	}

	/**
	 * �G���[���b�Z�[�W�����邩���肷��B
	 *
	 * @return
	 */
	protected boolean hasMessage() {
		return messageList != null && messageList.hasMessage();
	}

	/**
	 * �V�X�e���G���[��ݒ肷��B
	 *
	 * @param t
	 * @param message
	 */
	protected void setError(Throwable t, String message) {
		this.systemError = t;
		this.systemErrorMessage = message;
	}

	/**
	 * �V�X�e���G���[�����邩���肷��B
	 *
	 * @return
	 */
	protected boolean hasThrowable() {
		return systemError != null;
	}

	/**
	 * �V�X�e���G���[���b�Z�[�W���擾����B
	 *
	 * @return
	 */
	protected String getSystemErrorMessage() {
		return systemErrorMessage;
	}

	/**
	 * �V�X�e���G���[���擾����B
	 *
	 * @return
	 */
	protected Throwable getError() {
		return systemError;
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁A���N�G�X�g�ɃZ�b�g����B
	 *
	 * @param <T>
	 * @param name
	 * @param object
	 * @return
	 */
	protected <T> T bind(String name, T object) {

		inject(name, object);

		request.setAttribute(name, object);

		return object;
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁A���N�G�X�g�ɃZ�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param initial
	 * @return
	 */
	protected <T> T bind(T initial) {
		return bind(toBeanName(initial), initial);
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁A�Z�b�V�����ɃZ�b�g����B
	 *
	 * @param <T>
	 * @param name
	 * @param initial
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T bindInSession(String name, T initial) {
		T target = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			target = (T) session.getAttribute(name);
		} else {
			session = request.getSession(true);
		}

		if (target == null) {
			target = initial;
			session.setAttribute(name, target);
		}

		inject(name, target);

		return target;
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁A�Z�b�V�����ɃZ�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param key
	 * @param initial
	 * @return
	 */
	protected <T> T bindInSession(T initial) {
		return bindInSession(toBeanName(initial), initial);
	}

	/**
	 * ���N�G�X�g�p�����[�^�ɂ��A�p�����[�^���ɑΉ�����ΏۃI�u�W�F�N�g�̒l���X�V����B
	 *
	 * @param attributeName
	 * @param target
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object inject(String attributeName, Object target) {
		String attrKey = attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR;
		int idx = attrKey.length();

		Enumeration<String> nameList = request.getParameterNames();

		// �p�����[�^�[��������������
		while (nameList.hasMoreElements()) {
			String paramName = nameList.nextElement();

			if (!paramName.startsWith(attrKey)) {
				continue;
			}

			String propertyName = paramName.substring(idx);
			if (propertyName.length() == 0) {
				System.out.println("[�x��]�v���p�e�B���̃t�H�[�}�b�g���s���ł��F" + paramName);
				continue;
			}

			String[] paramValues = request.getParameterValues(paramName);

			AccessResult result = null;

			if (paramValues.length == 1) {
				// String �l���Z�b�g����
				result = PropertyAccessor.setNestedProperty(attributeName, target, propertyName, paramValues[0]);
			} else {
				// String[] �l���Z�b�g����
				result = PropertyAccessor.setNestedProperty(attributeName, target, propertyName, paramValues);
			}

			if (!result.isSuccess()) {
				String message = "[�x��] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " �ɒl���Z�b�g�ł��܂���ł���";
				if (result.hasMessage()) {
					System.out.println(message + "�F" + result.message);
				} else {
					System.out.println(message);
				}
			}
		}
		return target;
	}

	/**
	 * �I�u�W�F�N�g���Z�b�g����B
	 *
	 * @param name
	 * @param obj
	 */
	protected void set(String name, Object obj) {
		request.setAttribute(name, obj);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param obj
	 */
	protected void set(Object obj) {
		set(toBeanName(obj), obj);
	}

	/**
	 * �I�u�W�F�N�g���擾����B
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T get(String name, Class<T> klass) {
		Object obj = request.getAttribute(name);
		if (obj == null) {
			return null;
		}

		return (T) obj;
	}

	/**
	 * �I�u�W�F�N�g���擾����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param klass
	 * @return
	 */
	protected <T> T get(Class<T> klass) {
		return get(toBeanName(klass), klass);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V�����ɃZ�b�g����B
	 *
	 * @param name
	 * @param obj
	 */
	protected void setInSession(String name, Object obj) {
		request.getSession().setAttribute(name, obj);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V�����ɃZ�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param obj
	 */
	protected void setInSession(Object obj) {
		setInSession(toBeanName(obj), obj);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V��������擾����B
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getFromSession(String name, Class<T> klass) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		Object obj = session.getAttribute(name);
		if (obj == null) {
			return null;
		}

		return (T) obj;
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V��������擾����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param klass
	 * @return
	 */
	protected <T> T getFromSession(Class<T> klass) {
		return getFromSession(toBeanName(klass), klass);
	}

	/**
	 * �I�u�W�F�N�g�̃N���X����Bean���ɕϊ�����B
	 *
	 * @param obj
	 * @return
	 */
	private static String toBeanName(Object obj) {
		return toBeanName(obj.getClass());
	}

	/**
	 * �N���X����Bean���ɕϊ�����B
	 *
	 * @param obj
	 * @return
	 */
	private static String toBeanName(Class<?> klass) {
		String beanName = klass.getSimpleName();
		beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1, beanName.length());
		return beanName;
	}

	/**
	 * ���X�|���X�ɃN�b�L�[��ǉ�����B
	 *
	 * @param cookie
	 */
	protected void addResponseCookie(Cookie cookie) {
		response.addCookie(cookie);
	}

	/**
	 * �N�b�L�[���擾����B
	 *
	 * @return
	 */
	protected Cookie[] getCookies() {
		return request.getCookies();
	}

	/**
	 * �Z�b�V���������폜����B
	 *
	 */
	protected void deleteSession() {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * IP�A�h���X���擾����B
	 *
	 * @return
	 */
	protected String getIPAddress() {
		return request.getRemoteAddr();
	}

	/**
	 * InputStream���擾����B
	 *
	 * @return
	 * @throws IOException
	 */
	protected InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	/**
	 * ���_�C���N�g�v����ԋp����B
	 *
	 * @param location
	 * @throws IOException
	 */
	protected void sendRedirect(String location) {
		try {
			response.sendRedirect(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �t�H���[�h����B
	 *
	 * @param path
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void forward(String path) {
		try {
			request.getRequestDispatcher(path).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����Ɏw�肵�����O�̃��N�G�X�g�p�����[�^�[���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O�̃��N�G�X�g�p�����[�^�[
	 */
	protected String getParameter(String name) {
		return request.getParameter(name);
	}

	/**
	 * JSESSIONID���擾����
	 *
	 * @return JSESSIONID
	 */
	protected String getJSessionID() {
		Cookie[] cookies = getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				return cookie.getValue();
			}
		}

		return null;
	}

	/**
	 * �����Ɏw�肵�����O��Cookie�̒l���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O��Cookie�̒l
	 */
	protected String getCookieValue(String name) {
		Cookie cookie = getCookie(name);

		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	/**
	 * �����Ɏw�肵�����O��Cookie���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O��Cookie
	 */
	protected Cookie getCookie(String name) {
		Cookie[] cookies = getCookies();

		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}

		return null;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	protected void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	protected ServletContext getServletContext() {
		return servletContext;
	}

//	public void setParameter(String name, String value) {
//	}
//
//	public void setParameterMap(Map<String, String> map) {
//	}
//
//	public void setParametersMap(Map<String, String[]> map) {
//	}
//
//	public void setParameterBean(Object obj) {
//	}
//
//	public void setPath(String path) {
//
//	}
//
//	public void setInputStream(InputStream in) {
//
//	}
//
//	public void setCookie(Cookie cookie) {
//
//	}
//
//	public void setMobileInfo(MobileInfo mobileInfo) {
//	}

}
