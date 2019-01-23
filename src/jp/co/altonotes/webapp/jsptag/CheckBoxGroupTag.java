package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �����̃`�F�b�N�{�b�N�X�𓯂�name�ŃO���[�s���O����^�O
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
	 * @param name �Z�b�g����name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope �Z�b�g����scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/**
	 * @return �`�F�b�N���ꂽ�{�b�N�X��value�l�B��Ȃ�String�A�����Ȃ�String�z���
	 */
	public Object getCheckedValues() {
		return checkedValues;
	}

	/**
	 * @return name�̒l
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return scope�̒l
	 */
	public String getScope() {
		return scopeName;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		checkedValues = ScopeAccessor.extract(scope, name);

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

			//�`�F�b�N�����̏ꍇ�ɋ�̒l�𑗐M����悤�A������hidden�^�O������B
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
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		name = null;
		scopeName = null;
		checkedValues = null;
	}
}
