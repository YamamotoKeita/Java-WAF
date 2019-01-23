package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * テキストボックスタグ
 *
 * @author Yamamoto Keita
 *
 */
public class TextTag extends TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 1724679512900154156L;

	private String name;
	private String scopeName;//scopeという名前は予約されてるのでscopeNameにする

	private HashMap<String, String> attributes = new HashMap<String, String>();

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

	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String value = ScopeAccessor.extractString(scope, name);

		value = TextUtils.htmlEscape(value);

		JspWriter out = pageContext.getOut();
		try {
			out.write("<input type=\"text\" name=\"" + name + "\"");
			out.write(" value=\"" + value + "\"");

			Set<Entry<String, String>> set = attributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
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
		//TODO Tomcatはタグインスタンスをプールしている！
		//そのため値を初期化してやる必要があるのだけど、Ja-jakartaのガイドラインには「setXXX() メソッドがセットしたプロパティを変更してはなりません 」とある。
		//でも、めんどくさいので今のところそれに従っていない。cタグも従ってなかったような気がするから。
		//今のところ doEndTag() で初期化しているけれど、どこで初期化するか再考すべき。
		release();
		return EVAL_PAGE;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		name = null;
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
