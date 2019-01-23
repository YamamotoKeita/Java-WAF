package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * HTMLのフォームを表すタグ
 *
 * @author Yamamoto Keita
 *
 */
public class FormTag extends BodyTagSupport implements DynamicAttributes {

	private static final long serialVersionUID = -8542748878038375799L;

	private String action;
	private String method;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @param action セットする action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param method セットする method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();

		try {
			out.write("<form");

			String url = action;
			if (TagUtil.isAvailableURLSession(pageContext)) {
				HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
				url = response.encodeURL(url);
			}

			out.write(" action=\""+ url + "\"");

			if (method != null) {
				out.write(" method=\""+ method + "\"");
			}

			Set<Entry<String, String>> set = attributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			out.write(">\r\n");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspTagException{
		JspWriter out = pageContext.getOut();
		try {
			out.write("</form>\r\n");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		action = null;
		method = null;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		attributes.put(name, value.toString());
	}

}
