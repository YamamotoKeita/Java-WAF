package jp.co.altonotes.webapp.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;

/**
 * �R���e���c��񋟂���T�[�u���b�g�B
 * HTTP�T�[�o�[�̖�ڂ�����B
 *
 * AP�T�[�o�[�̃R���e���c�o�̓T�[�u���b�g���g�p����ƁA
 * �t���[�����[�N��404�����m�ł��Ȃ�����
 *
 * @author Yamamoto Keita
 *
 */
public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -3425858095631492382L;

	private transient MimeMapps mimeMaps;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serveResource(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serveResource(req, resp);
	}

	/**
	 * �Ή����郊�\�[�X���N���C�A���g�ɏo�͂���B
	 *
	 * @param req
	 * @param resp
	 */
	protected void serveResource(HttpServletRequest req, HttpServletResponse resp) {
		setResponseHeader(req, resp);
		outputResource(req, resp);
	}

	private void setResponseHeader(HttpServletRequest req, HttpServletResponse resp) {
		//Content-Length
		// Content-Type
		//Cach-Control
		//Pragma
		//Expires
		//Content-Encoding
		//Etag

		//�e�L�X�g�̃��X�|���X�͈��k���鉿�l������܂��B
		//�C���[�W��PDF�t�@�C���͈��k���Ă͂����܂���BCPU��Q��邾���ł͂Ȃ��A�t�@�C���T�C�Y���������đ傫���Ȃ��Ă��܂��܂��B

	}

	private void outputResource(HttpServletRequest req, HttpServletResponse resp) {

	}

	/**
	 * Request��̑��΃p�X���擾����B
	 *
	 * @param req
	 * @return
	 */
	protected String getRelativePath(HttpServletRequest req) {

		// RequestDispatcher.include() �Ŏw�肳�ꂽ path ���`�F�b�N����B
		if (req.getAttribute(Globals.INCLUDE_REQUEST_URI_ATTR) != null) {
			String result = (String) req.getAttribute(Globals.INCLUDE_PATH_INFO_ATTR);
			if (result == null) {
				result = (String) req.getAttribute(Globals.INCLUDE_SERVLET_PATH_ATTR);
			}
			if ((result == null) || (result.equals(""))) {
				result = "/";
			}
			return (result);
		}

		//�Ȃ���΃p�X�擾
		String result = req.getPathInfo();
		if (result == null) {
			result = req.getServletPath();
		}
		if ((result == null) || (result.equals(""))) {
			result = "/";
		}
		return (result);

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.doDelete(req, resp);
	}


	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.doHead(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.doOptions(arg0, arg1);
	}


	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.doPut(req, resp);
	}

}
