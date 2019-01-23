package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * URL�ɃZ�b�V����ID��t�^����<a>�^�O�������o���B
 *
 * @author Yamamoto Keita
 *
 */
public class IDLinkTag extends BodyTagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 2633630766294027072L;

	private String href = null;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @param href
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {
		JspWriter out = pageContext.getOut();

		try {
			out.write("<a href=\"");

			if (TagUtil.isAvailableURLSession(pageContext)) {
				HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
				href = response.encodeURL(href);
			}
			out.write(href + "\"");

			Set<Entry<String, String>> set = attributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			out.write(">");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return EVAL_BODY_AGAIN;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspTagException {
		JspWriter out = bodyContent.getEnclosingWriter();
		try {
			out.write(bodyContent.getString());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		return SKIP_BODY;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		JspWriter out = pageContext.getOut();
		try {
			out.write("</a>");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		href = null;
		attributes = new HashMap<String, String>();
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		attributes.put(name, value.toString());
	}

}
