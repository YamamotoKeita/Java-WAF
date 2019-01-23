package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * IFタグの条件に当てはまらない場合に内容を出力するタグ
 *
 * @author Yamamoto Keita
 *
 */
public class ElseTag extends BodyTagSupport {

	private static final long serialVersionUID = -5092973572720742915L;

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		Object parentObj = findAncestorWithClass(this, CaseTag.class);
		CaseTag parent = null;
		if (parentObj != null) {
			parent = (CaseTag) parentObj;
		}

		if (parent != null && !parent.isCompleted()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspTagException {
		return SKIP_BODY;
	}

	/*
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		return EVAL_PAGE;
	}

}
