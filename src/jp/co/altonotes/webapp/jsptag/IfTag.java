
package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �����ɂ���ă^�O���̕�������A�o�͂��邩���Ȃ����؂�ւ���B
 *
 * @author Yamamoto Keita
 *
 */
public class IfTag extends BodyTagSupport {
	private static final long serialVersionUID = -6565093533872750469L;

	/** type�����̒l�Fnull ���� */
	public static final String TYPE_EMPTY = "empty";

	/** type�����̒l�Fnot null���� */
	public static final String TYPE_NOT_EMPTY = "not-empty";

	/** type�����̒l�Ftrue ������ */
	public static final String TYPE_TRUE = "true";

	/** type�����̒l�Ffalse ������ */
	public static final String TYPE_FALSE = "false";

	protected boolean test;
	protected String var;
	protected String scopeName;
	protected String methodName;
	protected String name;
	protected String type = TYPE_TRUE;

	/**
	 * @param test
	 */
	public void setTest(boolean test) {
		this.test = test;
	}

	/**
	 * @param var
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param name
	 */
	public void setMethod(String name) {
		this.methodName = name;
	}

	/**
	 * @param scope
	 */
	public void setScope(String scope) {
		scopeName = scope;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int doStartTag() throws JspException {
		boolean result = test();

		exposeVariables();

		Object parentObj = findAncestorWithClass(this, CaseTag.class);
		if (parentObj != null && result) {
			CaseTag parent = (CaseTag) parentObj;
			parent.complete();
		}

		if (result) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
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
		test = false;
		var = null;
		scopeName = null;
		methodName = null;
		type = TYPE_TRUE;

	}

	/**
	 * name�Ɏw�肵���l�����؂���
	 *
	 * @return
	 */
	protected boolean test() {
		boolean result = test;

		if (methodName != null || name != null) {
			IScope scope = ScopeAccessor.create(pageContext, scopeName);
			if (methodName != null) {
				result = ScopeAccessor.doCheckMethod(scope, methodName);
			} else if (name != null) {
				if (type.equalsIgnoreCase(TYPE_EMPTY)) {
					result = (ScopeAccessor.extract(scope, name) == null);
				} else if (type.equalsIgnoreCase(TYPE_NOT_EMPTY)) {
					result = (ScopeAccessor.extract(scope, name) != null);
				} else {
					result = ScopeAccessor.extractFlag(scope, name);
				}
			}
		}

		if (type.equalsIgnoreCase(TYPE_FALSE)) {
			result = !result;
		}

		return result;
	}

	/**
	 * �^�O�Ɏw�肳�ꂽattribute�ɔ��茋�ʂ�boolean�l��ۑ�����
	 */
	protected void exposeVariables() {
		int scopeID = PageContext.PAGE_SCOPE;
		if (scopeName == null) {
			scopeID = PageContext.PAGE_SCOPE;
		} else if (scopeName.equalsIgnoreCase("page")) {
			scopeID = PageContext.PAGE_SCOPE;
		} else if (scopeName.equalsIgnoreCase("request")) {
			scopeID = PageContext.REQUEST_SCOPE;
		} else if (scopeName.equalsIgnoreCase("session")) {
			scopeID = PageContext.SESSION_SCOPE;
		} else if (scopeName.equalsIgnoreCase("application")) {
			scopeID = PageContext.APPLICATION_SCOPE;
		}
		if (var != null) {
			pageContext.setAttribute(var, Boolean.valueOf(test), scopeID);
		}
	}
}
