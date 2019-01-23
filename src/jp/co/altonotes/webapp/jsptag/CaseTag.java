package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Caseタグ。If Eleseブロックの親タグになる。
 *
 * @author Yamamoto Keita
 *
 */
public class CaseTag extends BodyTagSupport {

	private static final long serialVersionUID = 927452726847429613L;

	private boolean completedFlag;

	/**
	 * @return 既にいずれかのIfブロックが適応し描画されている場合<code>true</code>
	 */
	public boolean isCompleted() {
		return completedFlag;
	}

	/**
	 * いずれかのIfブロックが条件に該当した場合に、このCaseブロックを完了させる。
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
