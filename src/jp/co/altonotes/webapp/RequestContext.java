package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.util.MessageList;

/**
 * HTTP���N�G�X�g�̃R���e�L�X�g�ւ̃A�N�Z�X��񋟂���B<br>
 * ThreadLocal �ɃR���e�L�X�g�̏���ۑ����邱�Ƃɂ��A<br>
 * ���̃N���X�� static ���\�b�h�ŃR���e�L�X�g�̏��ɃA�N�Z�X�ł���B<br>
 *
 * HttpServletRequest, HttpServletResponse�̎��@�\�ɉ����āA
 * ���N�G�X�g�p�����[�^�[���I�u�W�F�N�g�ւ̖��ߍ��݋@�\��A
 * ���b�Z�[�W�̊Ǘ��Ȃǂ̋@�\�����B
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestContext {

	/** �R���e�L�X�g�I�u�W�F�N�g��ۑ�����ServletRequest�̑����� */
	public static final String FRAMEWORK_CONTEXT = "FRAMEWORK_CONTEXT";

	private static ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<RequestInfo>();

	/**
	 * ThreadLocal �Ƀ��N�G�X�g�̃R���e�L�X�g���쐬����B
	 * �쐬�����R���e�L�X�g�̏��͂��̃N���X�̐ÓI���\�b�h�œ���X���b�h����擾�ł���悤�ɂȂ�B
	 *
	 * @param req
	 * @return �R���e�L�X�g�̏���ێ����� RequestDelegate �C���X�^���X
	 */
	protected static RequestInfo createContext(HttpServletRequest req) {
		RequestInfo requestInfo = new RequestInfo(req);
		threadLocal.set(requestInfo);
		req.setAttribute(FRAMEWORK_CONTEXT, requestInfo);
		return requestInfo;
	}

	/**
	 * �J�����g�X���b�h��Context��j������
	 */
	public static void destroy() {
		threadLocal.remove();
	}

	/**
	 * Context�����ɑ��݂��邩���肷��B
	 *
	 * @return
	 */
	public static boolean hasContext() {
		return threadLocal.get() != null;
	}

	/**
	 * ���ɑ��݂���RequestDelegate���擾����
	 *
	 * @return
	 */
	protected static RequestInfo getCurrentContext() {
		return threadLocal.get();
	}

	/**
	 * ServletRequest�ɕR�Â�Request�����擾����
	 *
	 * @param request
	 * @return ServletRequest�ɕR�Â�Request���
	 */
	public static RequestInfo getCurrentContext(ServletRequest request) {
		return (RequestInfo) request.getAttribute(FRAMEWORK_CONTEXT);
	}

	/**
	 * �w�肵�����O�̃p�X�ϐ����擾����B
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O�ɑΉ�����p�X�ϐ�
	 */
	public static String getPathVariable(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getPathVariable(name);
	}

	/**
	 * �V�X�e���G���[���Z�b�g����B
	 *
	 * @param t
	 * @param message
	 */
	protected static void setError(Throwable t, String message) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setError(t, message);
	}

	/**
	 * Cookie����JSESSIONID�̒l���擾����
	 *
	 * @return ���N�G�X�g��Cookie�ɃZ�b�g���ꂽJSESSIONID�̒l
	 */
	public static String getJSessionID() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getJSessionID();
	}

	/**
	 * �V�X�e���G���[���擾����B
	 *
	 * @return ���������V�X�e���G���[
	 */
	public static Throwable getError() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getError();
	}

	/**
	 * ���������G���[�̃^�C�g�����擾����B
	 *
	 * @return ���������V�X�e���G���[�̃^�C�g��
	 */
	public static String getErrorMessage() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getSystemErrorMessage();
	}

	/**
	 * �V�X�e���G���[���������Ă��邩���肷��B
	 *
	 * @return �V�X�e���G���[���������Ă���ꍇ<code>true</code>
	 */
	public static boolean hasError() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.hasThrowable();
	}

	/**
	 * �����Ɏw�肵�����O�̃��N�G�X�g�p�����[�^�[���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O�̃��N�G�X�g�p�����[�^�[
	 */
	public static String getParameter(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getParameter(name);
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�̑Ή�����v���p�e�B�ɃZ�b�g���AContext�ɃZ�b�g����B
	 *
	 * @param initial
	 * @param key
	 * @return �K�؂Ȓl���Z�b�g�����A�����œn���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T bind(String key, T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bind(key, initial);
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁AContext�ɃZ�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param initial
	 * @return �K�؂Ȓl���Z�b�g�����A�����œn���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T bind(T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bind(initial);
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁AContext��Session�ɃZ�b�g����B
	 *
	 * @param initial
	 * @param key
	 * @return �K�؂Ȓl���Z�b�g�����A�����œn���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T bindInSession(String key, T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bindInSession(key, initial);
	}

	/**
	 * ���N�G�X�g�p�����[�^�������̃C���X�^���X�ɖ��ߍ��݁AContext��Session�ɃZ�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param initial
	 * @return �K�؂Ȓl���Z�b�g�����A�����œn���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T bindInSession(T initial) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.bindInSession(initial);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�g����B
	 *
	 * @param name
	 * @param obj
	 */
	public static void set(String name, Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.set(name, obj);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�g����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param obj
	 */
	public static void set(Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.set(obj);
	}

	/**
	 * �I�u�W�F�N�g���擾����B
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return �����̖��O��attribute�ɃZ�b�g���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T get(String name, Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.get(name, klass);
	}

	/**
	 * �I�u�W�F�N�g���擾����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param klass
	 * @return �N���X���̐擪���������ɂ������O��attribute�ɃZ�b�g���ꂽ�I�u�W�F�N�g
	 */
	public static <T> T get(Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.get(klass);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V�����ɃZ�b�g����B
	 *
	 * @param name
	 * @param obj
	 */
	public static void setInSession(String name, Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setInSession(name, obj);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V�����A�g���r���[�g�ɃZ�b�g����B
	 * �N���X���̐擪���������ɂ��������񂪃A�g���r���[�g���Ƃ��Ďg�p�����B
	 *
	 * @param obj
	 */
	public static void setInSession(Object obj) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.setInSession(obj);
	}

	/**
	 * �����̖��O�̃A�g���r���[�g���Z�b�V��������擾����B
	 * �A�g���r���[�g�͈����Ɏw�肵���N���X�ɃL���X�g�����B
	 *
	 * @param <T>
	 * @param name
	 * @param klass
	 * @return �����̖��O�ŃZ�b�V����Attribute����擾�����I�u�W�F�N�g
	 */
	public static <T> T getFromSession(String name, Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getFromSession(name, klass);
	}

	/**
	 * �I�u�W�F�N�g���Z�b�V��������擾����B
	 * �L�[�̓N���X���̐擪���������ɂ������̂��g�p����B
	 *
	 * @param <T>
	 * @param klass
	 * @return �Z�b�V�����AAttribute����擾�����I�u�W�F�N�g
	 */
	public static <T> T getFromSession(Class<T> klass) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getFromSession(klass);
	}

	/**
	 * �R���e�L�X�g�Ƀ��b�Z�[�W��ǉ�����B
	 *
	 * @param message
	 */
	public static void addMessage(String message) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.addMessage(message);
	}

	/**
	 * �R���e�L�X�g�Ƀ��b�Z�[�W�����邩���肷��B
	 *
	 * @return �R���e�L�X�g�Ƀ��b�Z�[�W������ꍇ<code>true</code>
	 */
	public static boolean hasMessage() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.hasMessage();
	}

	/**
	 * �R���e�L�X�g�̃��b�Z�[�W���擾����B
	 *
	 * @return �R���e�L�X�g�ɃZ�b�g���ꂽ���b�Z�[�W��
	 */
	public static MessageList getMessages() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getMessages();
	}

	/**
	 * �R���e�L�X�g��HttpServletRequest���擾����B
	 *
	 * @return �R���e�L�X�g��HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest();
	}

	/**
	 * �R���e�L�X�g��HttpSession���擾����B
	 * ���݂��Ȃ��ꍇnull��Ԃ��B
	 *
	 * @return �R���e�L�X�g��HttpSession
	 */
	public static HttpSession getSession() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getSession();
	}

	/**
	 * �R���e�L�X�g��HttpServletResponse���擾����B
	 *
	 * @return �R���e�L�X�g��HttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getResponse();
	}

	/**
	 * �g�ђ[�������擾����B
	 *
	 * @return �g�ђ[�����
	 */
	public static MobileInfo getMobileInfo() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getMobileInfo();
	}

	/**
	 * �R���e�L�X�g�̃��X�|���X��cookie��ǉ�����B
	 *
	 * @param cookie
	 */
	public static void addResponseCookie(Cookie cookie) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.addResponseCookie(cookie);
	}

	/**
	 * ���N�G�X�g�Ɋ܂܂��N�b�L�[�����擾����B
	 *
	 * @return ���N�G�X�g�Ɋ܂܂��N�b�L�[���
	 */
	public static Cookie[] getCookies() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookies();
	}

	/**
	 * �R���e�L�X�g�̃Z�b�V���������폜����B
	 *
	 */
	public static void deleteSession() {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.deleteSession();
	}

	/**
	 * ���N�G�X�g��IP�A�h���X���擾����B
	 *
	 * @return ���N�G�X�g��IP�A�h���X
	 */
	public static String getIPAddress() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getIPAddress();
	}

	/**
	 * �\������y�[�W��XHTML�����肷��B
	 *
	 * @return �\������y�[�W��XHTML�̏ꍇ<code>true</code>
	 */
	public static boolean isXHTML() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.isXHTMLPage();
	}

	/**
	 * ���N�G�X�g��InputStream���擾����
	 *
	 * @return ���N�G�X�g��InputStream
	 * @throws IOException
	 */
	public static InputStream getInputStream() throws IOException {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getInputStream();
	}

	/**
	 * ���_�C���N�g�v���𑗐M����
	 *
	 * @param location
	 */
	public static void sendRedirect(String location) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.sendRedirect(location);
	}

	/**
	 * �t�H���[�h����
	 *
	 * @param path
	 */
	public static void forward(String path) {
		RequestInfo requestInfo = threadLocal.get();
		requestInfo.forward(path);
	}

	/**
	 * �����Ɏw�肵�����O��Cookie�̒l���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O��Cookie�̒l
	 */
	public static String getCookieValue(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookieValue(name);
	}

	/**
	 * �����Ɏw�肵�����O��Cookie���擾����
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O��Cookie
	 */
	public static Cookie getCookie(String name) {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getCookie(name);
	}

	/**
	 * �R���e�L�X�g�p�X���擾����
	 * @return �R���e�L�X�g�p�X
	 */
	public static String getContextPath() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest().getContextPath();
	}
	
	/**
	 * ServletContext���擾����
	 * @return ServletContext
	 */
	public static ServletContext getServletContext() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getServletContext();
	}
	
	/**
	 * User-Agent���擾����
	 * @return User-Agent
	 */
	public static String getUserAgent() {
		RequestInfo requestInfo = threadLocal.get();
		return requestInfo.getRequest().getHeader("User-Agent");
	}
}
