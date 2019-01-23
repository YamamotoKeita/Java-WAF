package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.webapp.exception.StartupException;

/**
 * ���N�G�X�g�R���e�L�X�g���쐬����t�B���^�[
 * @author Yamamoto Keita
 *
 */
public class RequestContextFilter implements Filter {
	
	/** �N�������s���W���[�������G���[���b�Z�[�W	*/
	private static final String ERROR_MESSAGE_STARTUP = "<initialize>�v���Z�X�̎��s���ɃG���[���������܂����F";

	/** �N�������s���W���[���ꗗ	*/
	private List<StartupProcess> startUps;

	/** �����R�[�h�̃}�b�v	*/
	private transient RequestMap<String> charsetMap;

	/** HTML TYPE�̃}�b�v	*/
	private transient RequestMap<String> htmlTypeMap;

	/** URL Session�̃}�b�v	*/
	private transient RequestMap<Boolean> urlSessionMap;

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			ServletContext servletContext  = filterConfig.getServletContext();
			System.out.println("\r\n[" +servletContext .getServletContextName() + "] �����������܂�");

			// config�t�@�C���̓ǂݍ���
			ApplicationConfig config = new ApplicationConfig(servletContext);

			charsetMap = config.getCharsetMap();
			htmlTypeMap = config.getHTMLTypeMap();
			urlSessionMap = config.getURLSessionMap();
			startUps = config.getStartups();

			for (StartupProcess process : startUps) {
				try {
					process.run();
				} catch (Throwable e) {
					throw (StartupException) new StartupException(ERROR_MESSAGE_STARTUP + process).initCause(e);
				}
			}

		} catch (RuntimeException e) {
			destroy();
			// throw ����� APServer �� Exception ���O���o���Ă����͂�
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		if (startUps != null) {
			for (StartupProcess startup : startUps) {
				startup.destroy();
			}
		}
	}
	
	/*
	 * Filter�������s��
	 *
	 * (�� Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srcReq, ServletResponse srcResp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) srcReq;
		HttpServletResponse resp = (HttpServletResponse) srcResp;

		RequestInfo context = RequestContext.getCurrentContext();
		context.setSource(req, resp);

		setCharacterEncoding(context);
		setHTMLType(context);
		setURLSessionFlag(context);
		
		chain.doFilter(req, resp);
	}

	/**
	 * ���N�G�X�g�p�����[�^�[�̃G���R�[�f�B���O��ݒ肷��B
	 *
	 * ���ӁFGET�p�����[�^�[�̕����R�[�h�̓y�[�W�G���R�[�f�B���O�Ɋւ�炸�AUTF-8�ɂ���̂����[�������A
	 * GET�p�����[�^�[���y�[�W�G���R�[�f�B���O�Ɠ����G���R�[�f�B���O�ő��M����u���E�U���̕��������̂�GET���N�G�X�g�ɂ��y�[�W�G���R�[�f�B���O��ݒ肷��B
	 * GET�N�G���ɑ΂���setCharacterEncoding��L���ɂ��邽�߂ɂ́ATomcat5.X�ȍ~�ł�server.xml��connector�̑�����useBodyEncodingForURI="true"��ݒ肷��K�v������B
	 *
	 * @param context
	 */
	private void setCharacterEncoding(RequestInfo context) {

		String charset = charsetMap.get(context.getRequest());

		if (charset != null) {
			try {
				context.getRequest().setCharacterEncoding(charset);
			} catch (UnsupportedEncodingException ignored) {} //�N�����Ƀ`�F�b�N����̂Ŗ���
		}
	}

	/**
	 * HTML TYPE��ݒ肷��B
	 *
	 * @param req
	 * @param path
	 */
	private void setHTMLType(RequestInfo context) {
		String type = htmlTypeMap.get(context.getRequest());
		if (type != null) {
			context.setHTMLType(type);
		}
	}

	/**
	 * URLSession�̃t���O���Z�b�g����
	 *
	 * @param context
	 */
	private void setURLSessionFlag(RequestInfo context) {
		Boolean flag = urlSessionMap.get(context.getRequest());
		if (flag != null && flag) {
			context.enableURLSession(true);
		}
	}
}
