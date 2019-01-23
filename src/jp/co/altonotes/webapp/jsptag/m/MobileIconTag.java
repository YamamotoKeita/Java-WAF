package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.jsptag.TagUtil;

/**
 * UserAgent��iPhone�̏ꍇ�A���C�ɓ���o�^�����ۂɕ\�������A�C�R���̃^�O���o�͂���B
 *
 * @author Yamamoto Keita
 *
 */
public class MobileIconTag extends TagSupport {

	private static final long serialVersionUID = 8150812193478223984L;

	private String image;

	/**
	 * @param image �Z�b�g����image����
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * �^�O�J�n�̏���
	 */
	@Override
	public int doStartTag() throws JspTagException {

		MobileInfo mobileInfo = RequestContext.getMobileInfo();

		if (!mobileInfo.isIPhone()) {
			return SKIP_BODY;
		}

		boolean isXHTML = TagUtil.isXHTML(pageContext);
		JspWriter out = pageContext.getOut();

		try {

			if (mobileInfo.isIPhone()) {
				String end = ">\r\n";
				if (isXHTML) {
					end = " />\r\n";
				}
				out.write("<link rel=\"apple-touch-icon\" href=\"" + image + "\"");
				out.write(end);
			}
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		image = null;
	}

	@Override
	public int doEndTag() {
		release();
		return EVAL_PAGE;
	}
}
