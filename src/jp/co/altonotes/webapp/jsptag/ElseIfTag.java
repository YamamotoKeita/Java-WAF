package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.JspException;

/**
 * IFタグの条件に当てはまらない場合に、再度条件判定を行い、当てはまる場合内容を出力するタグ
 *
 * @author Yamamoto Keita
 *
 */
public class ElseIfTag extends IfTag {

	private static final long serialVersionUID = -130885594469460113L;

	/*
	 * (非 Javadoc)
	 * @see jp.co.altonotes.webapp.jsptag.IfTag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		Object parentObj = findAncestorWithClass(this, CaseTag.class);
		CaseTag parent = null;
		if (parentObj != null) {
			parent = (CaseTag) parentObj;
		}

		if (parent == null || parent.isCompleted()) {
			return SKIP_BODY;
		}

		test = test();

		exposeVariables();

		if (test) {
			parent.complete();
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

}
