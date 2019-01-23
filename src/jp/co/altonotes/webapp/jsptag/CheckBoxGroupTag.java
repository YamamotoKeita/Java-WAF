package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * 複数のチェックボックスを同じnameでグルーピングするタグ
 *
 * @author Yamamoto Keita
 *
 */
public class CheckBoxGroupTag extends BodyTagSupport {

	private static final long serialVersionUID = -87598203899778465L;

	private String name;
	private String scopeName;
	private Object checkedValues;

	/**
	 * @param name セットするname
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope セットするscope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/**
	 * @return チェックされたボックスのvalue値。一つならString、複数ならString配列の
	 */
	public Object getCheckedValues() {
		return checkedValues;
	}

	/**
	 * @return nameの値
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return scopeの値
	 */
	public String getScope() {
		return scopeName;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		checkedValues = ScopeAccessor.extract(scope, name);

		return EVAL_BODY_AGAIN;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspTagException {
		JspWriter out = bodyContent.getEnclosingWriter();
		try {
			out.write(bodyContent.getString());

			//チェック無しの場合に空の値を送信するよう、同名のhiddenタグを入れる。
			out.write("<input type=\"hidden\" name=\"" + name + "\" value=\"false\"");
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

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		name = null;
		scopeName = null;
		checkedValues = null;
	}
}
