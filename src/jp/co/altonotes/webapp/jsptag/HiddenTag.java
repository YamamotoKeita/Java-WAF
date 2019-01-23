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

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * Hiddenタグ
 *
 * @author Yamamoto Keita
 *
 */
public class HiddenTag extends TagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 6504067675563877777L;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	private String name;
	private String scopeName;

	/**
	 * @param name プロパティの名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope プロパティのスコープ
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
			out.write("<input type=\"hidden\" name=\"" + name + "\"");
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
		release();
		return EVAL_PAGE;
	}

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
		if ("value".equalsIgnoreCase(name)) {
			throw new JspException("カスタムタグ hidden に value 属性を指定することはできません。value 値は Gateway でセットした値が自動で出力されます。\n" + 
								   "[ヒント]hidden値で何らかの場合分けをしたいと考えている場合は、代わりにURLを別にする、URLにクエリパタメーターをつける、などの手段が使えます。\n" +
								   "同一フォーム内でボタンにより呼び出すメソッドを変えたい場合、@Gateway アノテーションに params値を指定することでメソッドの振り分けが可能です。\n" +
								   "また、どうしても固定のHidden値を記述したい場合は、通常の HTML タグ <input type=\"hidden\" /> を使います。");
		}
		attributes.put(name, value.toString());
	}

}
