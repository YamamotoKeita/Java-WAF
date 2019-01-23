package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * セッションおよびリクエストに保存されたAttributeをテキストとして取り出すタグ。
 *
 * @author Yamamoto Keita
 *
 */
public class OutTag extends TagSupport {
	private static final long serialVersionUID = -7398300983848083941L;

	private String name;
	private String scopeName;
	private String escape;

	/**
	 * nameをセットする。
	 * @param name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 読み取るattributeのスコープをセットする。
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}
	
	/**
	 * 出力にHTMLエスケープを行うかを設定する
	 * @param escape
	 */
	public void setEscape(String escape) {
		this.escape = escape;
	}

	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String value = ScopeAccessor.extractString(scope, name);

		//escape="false"以外の場合はhtmlエスケープを行う
		if (escape == null || !escape.equalsIgnoreCase("false")) {
			value = TextUtils.htmlEscape(value);
		}

		JspWriter out = pageContext.getOut();
		try {
			out.write(value);
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
		escape = null;
	}
}
