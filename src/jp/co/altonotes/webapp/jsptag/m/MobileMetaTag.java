package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.jsptag.TagUtil;

/**
 * 携帯の機種に応じて必要なmetaタグを出力する。
 *
 * @author Yamamoto Keita
 *
 */
public class MobileMetaTag extends TagSupport {

	private static final long serialVersionUID = 8150812193478223984L;

	@Override
	public int doStartTag() throws JspTagException {

		boolean isXHTML = TagUtil.isXHTML(pageContext);
		MobileInfo mobileInfo = RequestContext.getMobileInfo();

		JspWriter out = pageContext.getOut();

		try {

			if (mobileInfo.isIPhone()) {
				String end = ">\r\n";
				if (isXHTML) {
					end = " />\r\n";
				}
				out.write("<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0;\"");
				out.write(end);
				out.write("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\"");
				out.write(end);
				out.write("<meta name=\"format-detection\" content=\"telephone=no\"");
				out.write(end);
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
}
