package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �Z�b�V��������у��N�G�X�g�ɕۑ����ꂽAttribute���e�L�X�g�Ƃ��Ď��o���^�O�B
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
	 * name���Z�b�g����B
	 * @param name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * �ǂݎ��attribute�̃X�R�[�v���Z�b�g����B
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}
	
	/**
	 * �o�͂�HTML�G�X�P�[�v���s������ݒ肷��
	 * @param escape
	 */
	public void setEscape(String escape) {
		this.escape = escape;
	}

	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String value = ScopeAccessor.extractString(scope, name);

		//escape="false"�ȊO�̏ꍇ��html�G�X�P�[�v���s��
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
