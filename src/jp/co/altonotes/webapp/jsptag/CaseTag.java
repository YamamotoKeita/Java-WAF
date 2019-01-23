package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Case�^�O�BIf Elese�u���b�N�̐e�^�O�ɂȂ�B
 *
 * @author Yamamoto Keita
 *
 */
public class CaseTag extends BodyTagSupport {

	private static final long serialVersionUID = 927452726847429613L;

	private boolean completedFlag;

	/**
	 * @return ���ɂ����ꂩ��If�u���b�N���K�����`�悳��Ă���ꍇ<code>true</code>
	 */
	public boolean isCompleted() {
		return completedFlag;
	}

	/**
	 * �����ꂩ��If�u���b�N�������ɊY�������ꍇ�ɁA����Case�u���b�N������������B
	 */
	public void complete() {
		completedFlag = true;
	}

	@Override
	public int doStartTag() throws JspTagException {
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() throws JspTagException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		completedFlag = false;
	}
}
