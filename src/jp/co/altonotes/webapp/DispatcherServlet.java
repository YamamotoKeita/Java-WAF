package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.http.HeaderNames;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.handler.IRequestHandler;

/**
 * Web Application �̃R���g���[���N���X�B
 * �S�Ă�HTTP���N�G�X�g���󂯁A�O�������s������K�؂ȃ��W���[����JSP�Ƀf�B�X�p�b�`����B
 *
 * @author Yamamoto Keita
 *
 */
public final class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -1777739678425294015L;

	/** JSP�̃f�B�X�p�b�`���[	*/
	private transient RequestDispatcher jspDispathcer;

	/** HTML�ȂǐÓI���\�[�X�̃f�B�X�p�b�`���[	*/
	private transient RequestDispatcher defaultDispathcer;

	/** �t�B���^�[�����̃}�b�v	*/
	private transient RequestMap<IRequestHandler> filterMap;

	/** GateWay�̃}�b�v	*/
	private transient RequestMap<IRequestHandler> gatewayMap;

	/** �G���[�����̃}�b�v	*/
	private transient RequestMap<IRequestHandler> errorHandlerMap;

	/** Forward, Rdirect�����̃}�b�v	*/
	private transient RequestMap<IRequestHandler> viewMap;

	/** View�̃p�X���]���o�[	*/
	private transient ViewResolver viewRsolver;

	/**
	 * ������
	 */
	@Override
	public void init() {

		try {
			ServletContext servletContext  = getServletContext();

			// config�t�@�C���̓ǂݍ���
			ApplicationConfig config = new ApplicationConfig(getServletContext());

			errorHandlerMap = config.getErrorHandlerMap();
			viewRsolver = new ViewResolver(servletContext);
			gatewayMap = config.loadGatewayMap();
			filterMap = config.getFilterMap();
			viewMap = config.getViewMap();
			jspDispathcer = servletContext.getNamedDispatcher("jsp");
			String defaultServletName = config.getDefaultServletName();
			defaultDispathcer =  servletContext.getNamedDispatcher(defaultServletName);

		} catch (RuntimeException e) {
			destroy();
			// throw ����� APServer �� Exception ���O���o���Ă����͂�
			throw e;
		}
	}

	/**
	 * �I�����������s����B
	 */
	@Override
	public void destroy() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
		doProcess(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req, resp);
	}

	/**
	 * �S�Ẵ��N�G�X�g����������B
	 *
	 * @param req
	 * @param resp
	 * @param requestMethod
	 */
	private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
		
		RequestInfo context = RequestContext.getCurrentContext();
		context.setServletContext(getServletContext());
		try {
			doFilter(req, resp, context);
			
			if (doForward(req, resp, context)) {
				return;
			} else if (doJSP(req, resp, context)) {
				return;
			} else if (doGatewayMethod(req, resp, context)) {
				return;
			} else {
				doDefault(req, resp, context);
				return;
			}
		} catch (Throwable e) {
			doError(req, resp, "�f�B�X�p�b�`�G���[", e, context);
		} finally {
			RequestContext.destroy();
		}
	}

	/**
	 * Gateway���\�b�h�����s����B
	 *
	 * @param requestDelegate
	 * @return
	 */
	private boolean doGatewayMethod(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {
		IRequestHandler gateway = gatewayMap.get(delegate, req);

		if (gateway != null) {
			try {
				String viewPath = gateway.process(req, resp);
				if (viewPath != null) {
					viewRsolver.showView(viewPath, req, resp, delegate.isAvailableURLSession());
				}
			} catch (Throwable th) {
				doError(req, resp, "�Q�[�g�E�F�C�G���[", th, delegate);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * �t�H���[�h���������s����
	 *
	 * @param context
	 * @return
	 */
	private boolean doForward(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		IRequestHandler view = viewMap.get(req);

		if (view != null) {
			try {
				view.process(req, resp);
			} catch (Throwable t) {
				doError(req, resp, "�r���[�G���[", t, delegate);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * JSP�̃��N�G�X�g����������B
	 *
	 * @param req
	 * @param resp
	 */
	private boolean doJSP(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		String extention = TextUtils.getExtention(RequestInfo.getRequestPath(req));

		if ("jsp".equals(extention) || "jspx".equals(extention)) {
			try {
				jspDispathcer.forward(req, resp);
			} catch (Exception e) {
				doError(req, resp, "�y�[�W�G���[", e, delegate);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * �T�[�u���b�g�AJSP�ȊO�̃��N�G�X�g����������B
	 *
	 * @param req
	 * @param resp
	 */
	private void doDefault(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		try {
			defaultDispathcer.forward(req, resp);
		} catch (Exception e) {
			doError(req, resp, "�t�@�C���G���[", e, delegate);
		}
	}

	/**
	 * Filter���������s����
	 *
	 * @param context
	 */
	private void doFilter(HttpServletRequest req, HttpServletResponse resp, RequestInfo delegate) {

		IRequestHandler filter = filterMap.get(req);
		if (filter != null) {
			try {
				filter.process(req, resp);
			} catch (Throwable t) {
				doError(req, resp, "�t�B���^�[�G���[", t, delegate);
			}
		}
	}

	/**
	 * �G���[�������s���B
	 *
	 * @param req
	 * @param resp
	 * @param message
	 * @param e
	 */
	private void doError(HttpServletRequest req, HttpServletResponse resp, String message, Throwable t, RequestInfo delegate) {

		// �ȉ��̃G���[������forward���Ă���ɂ����ŃG���[�ɂȂ�ƁA�܂����̃��\�b�h���Ă΂ꖳ�����[�v����̂ŁA
		// ��x������ʂ������N�G�X�g�ɑ΂��Ă͉������Ȃ��B
		if (RequestContext.hasError()) {
			doDefaultError(req, resp, "�G���[�������̃G���[",t);
			return;
		} else {
			RequestContext.setError(t, message);
		}

		IRequestHandler exceptionHandler = errorHandlerMap.get(req);
		if (exceptionHandler != null) {
			try {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				String page = exceptionHandler.process(req, resp);
				viewRsolver.showView(page, req, resp, delegate.isAvailableURLSession());
				return;
			} catch (Throwable e) {
				doDefaultError(req, resp, "�G���[�������̃G���[", e.initCause(t));
			}
		} else {
			doDefaultError(req, resp, message, t);
		}
	}

	/**
	 * �f�t�H���g�̃G���[�������s���B<br>
	 * �G���[�������������[�v����̂ł��̃��\�b�h�͐�΂ɉ���Throw���Ă͂����Ȃ��I
	 *
	 * @param req
	 * @param resp
	 * @param title
	 * @param th
	 */
	private void doDefaultError(HttpServletRequest req, HttpServletResponse resp, String title, Throwable th) {
		OutputStream out = null;
		th.printStackTrace();
		try {
			if (RequestContext.hasError()) {
				System.err.println("�G���[�������̃G���[:" + th);
			}

			try {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				e.printStackTrace();
			}

			StringBuilder sb = new StringBuilder();
			putErrorHead(sb);

			//title
			sb.append("<div class=\"title\">");
			sb.append(title);
			sb.append("</div>\r\n");

			//exception
			sb.append("<div class=\"exception\">");
			sb.append(th.getClass().getName());
			sb.append("</div>\r\n");

			//message
			sb.append("<div class=\"message\">");

			String message = th.getMessage();
			if (message == null) {
				message = "";
			}
			message = TextUtils.htmlEscape(message);
			message = message.replaceAll("\\r\\n", "<br>");
			message = message.replaceAll("\\n", "<br>");

			sb.append(message);
			sb.append("</div>\r\n");

			//hr
			sb.append("<hr>\r\n");

			//stacktrace
			sb.append("<div class=\"stacktrace\">StackTrace:</div>\r\n");

			//stacks
			sb.append("<div class=\"stacks\">\r\n");
			StackTraceElement[] stacks = th.getStackTrace();
			for (StackTraceElement stack : stacks) {
				sb.append(stack.toString());
				sb.append("<br>\r\n");
			}

			Throwable cause = th.getCause();
			if (cause != null) {
				sb.append("<br>\r\ncause :<br>\r\n");
				stacks = cause.getStackTrace();
				for (StackTraceElement stack : stacks) {
					sb.append(stack.toString());
					sb.append("<br>\r\n");
				}
			}

			sb.append("</div>\r\n");

			//end
			sb.append("</body>\r\n</html>\r\n");

			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.setHeader(HeaderNames.CONTENT_TYPE, "text/html;charset=Shift_JIS");

			out = resp.getOutputStream();
			out.write(sb.toString().getBytes("Shift_JIS"));
		} catch (Throwable e) {
			// �G���[�������������[�v����̂ł��̃G���A�ł͐�΂ɉ���Throw���Ă͂Ȃ�Ȃ��I
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void putErrorHead(StringBuilder sb) {
		sb.append("\r\n");
		sb.append("<style type=\"text/css\">\r\n");
		sb.append(".title {\r\n");
		sb.append("	font-size: large;\r\n");
		sb.append("	background-color: #C0E0F0;\r\n");
		sb.append("	margin-top: 0px;\r\n");
		sb.append("	padding: 3px 5px;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("}\r\n");
		sb.append(".exception {\r\n");
		sb.append("	font-size: medium;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("	color: red;\r\n");
		sb.append("	margin-top: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".message {\r\n");
		sb.append("	font-size: medium;\r\n");
		sb.append("	font-weight: bold;\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 1em;\r\n");
		sb.append("	margin-top: 0.5em;\r\n");
		sb.append("	margin-bottom: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".stacktrace {\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 1em;\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");
		sb.append(".stacks {\r\n");
		sb.append("	position: relative;\r\n");
		sb.append("	left: 2em;\r\n");
		sb.append("}\r\n");
		sb.append("</style>\r\n");
		sb.append("\r\n");
		sb.append("</head>\r\n");
		sb.append("\r\n");
		sb.append("<body>");
	}
}
