package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.jsptag.TagUtil;

/**
 * 携帯の機種に適したContentTypeをHTMLのmetaタグとして出力する。<br>
 * 同時にそのContentTypeをHTTPレスポンスヘッダにもセットする。
 *
 * @author Yamamoto Keita
 *
 */
public class MobileContentTypeTag  extends TagSupport {

	private static final long serialVersionUID = 1746644141014403957L;

	private String charset = null;

	/**
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public int doStartTag() throws JspTagException {

		boolean isXHTML = TagUtil.isXHTML(pageContext);
		MobileInfo mobileInfo = RequestContext.getMobileInfo();

		JspWriter out = pageContext.getOut();

		try {
			String type = null;

			if (isXHTML && !mobileInfo.isIPhone() && !mobileInfo.isAndroid()) {
				type = "application/xhtml+xml; charset=" + charset;
			} else {
				type = "text/html; charset=" + charset;
			}

			pageContext.getResponse().setContentType(type);

			out.write("<meta http-equiv=\"Content-Type\" content=\"");
			out.write(type + "\"");

			if (isXHTML) {
				out.write(" />\r\n");
			} else {
				out.write(">\r\n");
			}
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		charset = null;
	}

}
