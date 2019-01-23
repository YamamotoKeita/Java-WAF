package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;


/**
 * セレクトタグ
 *
 * @author Yamamoto Keita
 *
 */
public class SelectTag extends BodyTagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 3830767460264137575L;

	String name;
	String value;
	String scopeName;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/*
	 * 開始タグでの処理
	 *
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {
		JspWriter out = pageContext.getOut();

		try {
			out.write("<select");
			out.write(" name=\""+ name + "\"");

			Set<Entry<String, String>> set = attributes.entrySet();
			boolean hasStyle = false;
			for (Entry<String, String> entry : set) {
				if (entry.getKey().equalsIgnoreCase("style") || entry.getKey().equalsIgnoreCase("class")) {
					hasStyle = true;
				}
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			if (!hasStyle) {
				out.write(" style=\"width:auto\"");
			}

			out.write(">\r\n");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		value = ScopeAccessor.extractString(scope, name);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspTagException{
		JspWriter out = pageContext.getOut();
		try {
			out.write("</select>\r\n");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		name = null;
		value = null;
		scopeName = null;
		attributes = new HashMap<String, String>();
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		attributes.put(name, value.toString());
	}
}
