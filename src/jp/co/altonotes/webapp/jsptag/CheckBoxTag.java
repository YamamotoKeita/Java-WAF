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
 * �P��̃`�F�b�N�{�b�N�X
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
	 * @param name �Z�b�g����name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value �Z�b�g����value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param scope �Z�b�g����scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspTagException {

		// �e�� CheckBoxGroupTag ���擾
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

		// �P��`�F�b�N�{�b�N�X�̏ꍇ�͒l���w�肵�Ȃ�
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

			// CheckBoxGroup�^�O���Ȃ��Ȃ�i����ꍇ�̓O���[�v�ň�_�~�[�l�������j
			if (parent == null) {
				//�`�F�b�N�����̏ꍇ�ɋ�̒l�𑗐M����悤�A������hidden�^�O������B
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
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() {
		release();
		return EVAL_PAGE;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		mAttributes.put(name, value.toString());
	}

	/*
	 * (�� Javadoc)
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
