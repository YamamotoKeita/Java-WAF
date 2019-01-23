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

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * パスワード入力ボックスのタグ
 *
 * @author Yamamoto Keita
 *
 */
public class PasswordTag extends TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = -5851814036139929189L;

	private String name;
	private String scopeName;
	private String autofill;

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
	 * @param autofill
	 */
	public void setAutofill(String autofill) {
		this.autofill = autofill;
	}

	@Override
	public int doStartTag() throws JspTagException {
		String value = "";
		if (Checker.isNotEmpty(autofill) && autofill.equalsIgnoreCase("on")) {
			IScope scope = ScopeAccessor.create(pageContext, scopeName);
			value = ScopeAccessor.extractString(scope, name);
			value = TextUtils.htmlEscape(value);
		}

		JspWriter out = pageContext.getOut();
		try {
			boolean isXHTML = TagUtil.isXHTML(pageContext);
			out.write("<input type=\"password\" name=\"" + name + "\"");
			out.write(" value=\"" + value + "\"");

			Set<Entry<String, String>> set = mAttributes.entrySet();
			boolean hasAutocomplete = false;
			for (Entry<String, String> entry : set) {
				if (entry.getKey().equalsIgnoreCase("autocomplete")) {
					hasAutocomplete = true;
				}
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			if (!hasAutocomplete) {
				out.write(" autocomplete=\"off\"");// autocomplete属性はXHTML規格を遵守してないが、実用性の方を重視してつける。
			}

			if (isXHTML) {
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
		autofill = null;
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
