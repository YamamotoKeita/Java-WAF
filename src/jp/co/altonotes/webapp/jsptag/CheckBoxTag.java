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
 * 単一のチェックボックス
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBoxTag extends TagSupport implements DynamicAttributes{
	private static final long serialVersionUID = -2303531167136783981L;

	private String name;
	private String scopeName;
	private String value;

	private HashMap<String, String> mAttributes = new HashMap<String, String>();

	/**
	 * @param name セットするname
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value セットするvalue
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param scope セットするscope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {

		// 親の CheckBoxGroupTag を取得
		CheckBoxGroupTag parent = null;
		Object parentObj = findAncestorWithClass(this, CheckBoxGroupTag.class);
		if (parentObj != null) {
			parent = (CheckBoxGroupTag) parentObj;
		}

		Object checkedValues = null;

		if (parent == null) {
			IScope scope = ScopeAccessor.create(pageContext, scopeName);
			checkedValues = ScopeAccessor.extract(scope, name);
		} else {
			checkedValues = parent.getCheckedValues();
			setName(parent.getName());
			setScope(parent.getScope());
		}

		// 単一チェックボックスの場合は値を指定しない
		if (value == null) {
			value = "true";
		}

		JspWriter out = pageContext.getOut();
		try {
			out.write("<input type=\"checkbox\" name=\"" + name + "\"");
			out.write(" value=\"" + value + "\"");

			Set<Entry<String, String>> set = mAttributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			if (checkedValues != null) {
				if (checkedValues instanceof Boolean) {
					if ((Boolean)checkedValues) {
						out.write(" checked");
					}
				} else if (checkedValues instanceof String) {
					if (checkedValues.equals(value)) {
						out.write(" checked");
					}
				} else if (checkedValues instanceof String[]) {
					String[] checks = (String[]) checkedValues;
					for (String checkedValue : checks) {
						if (checkedValue.equals(value)) {
							out.write(" checked");
							break;
						}
					}
				}
			}

			boolean isXHTML = TagUtil.isXHTML(pageContext);
			if (isXHTML) {
				out.write(" />");
			} else {
				out.write(">");
			}

			// CheckBoxGroupタグがないなら（ある場合はグループで一つダミー値をいれる）
			if (parent == null) {
				//チェック無しの場合に空の値を送信するよう、同名のhiddenタグを入れる。
				out.write("<input type=\"hidden\" name=\"" + name + "\" value=\"false\"");
				if (isXHTML) {
					out.write(" />");
				} else {
					out.write(">");
				}
			}
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() {
		release();
		return EVAL_PAGE;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		mAttributes.put(name, value.toString());
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		name = null;
		scopeName = null;
		value = null;
		mAttributes = new HashMap<String, String>();
	}

}
