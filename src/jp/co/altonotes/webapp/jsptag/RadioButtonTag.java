package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;


/**
 * ラジオボタンのタグ
 *
 * @author Yamamoto Keita
 *
 */
public class RadioButtonTag extends TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = -955373093526257017L;

	private String name;
	private String scopeName;
	private String value;

	private HashMap<String, String> mAttributes = new HashMap<String, String>();

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String status = ScopeAccessor.extractString(scope, name);

		JspWriter out = pageContext.getOut();
		try {
			out.write("<input type=\"radio\" name=\"" + name + "\"");
			out.write(" value=\"" + value + "\"");

			Set<Entry<String, String>> set = mAttributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			if (status != null && status.equals(value)) {
				out.write(" checked");
			}

			if (TagUtil.isXHTML(pageContext)) {
				out.write(" />");
			} else {
				out.write(">");
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
		name = null;
		scopeName = null;
		value = null;
		mAttributes = new HashMap<String, String>();
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		mAttributes.put(name, value.toString());
	}

}
