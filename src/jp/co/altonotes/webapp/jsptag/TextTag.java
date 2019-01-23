package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �e�L�X�g�{�b�N�X�^�O
 *
 * @author Yamamoto Keita
 *
 */
public class TextTag extends TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 1724679512900154156L;

	private String name;
	private String scopeName;//scope�Ƃ������O�͗\�񂳂�Ă�̂�scopeName�ɂ���

	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	@Override
	public int doStartTag() throws JspTagException {
		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String value = ScopeAccessor.extractString(scope, name);

		value = TextUtils.htmlEscape(value);

		JspWriter out = pageContext.getOut();
		try {
			out.write("<input type=\"text\" name=\"" + name + "\"");
			out.write(" value=\"" + value + "\"");

			Set<Entry<String, String>> set = attributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

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

	@Override
	public int doEndTag() {
		//TODO Tomcat�̓^�O�C���X�^���X���v�[�����Ă���I
		//���̂��ߒl�����������Ă��K�v������̂����ǁAJa-jakarta�̃K�C�h���C���ɂ́usetXXX() ���\�b�h���Z�b�g�����v���p�e�B��ύX���Ă͂Ȃ�܂��� �v�Ƃ���B
		//�ł��A�߂�ǂ������̂ō��̂Ƃ��낻��ɏ]���Ă��Ȃ��Bc�^�O���]���ĂȂ������悤�ȋC�����邩��B
		//���̂Ƃ��� doEndTag() �ŏ��������Ă��邯��ǁA�ǂ��ŏ��������邩�čl���ׂ��B
		release();
		return EVAL_PAGE;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		name = null;
		scopeName = null;
		attributes = new HashMap<String, String>();
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		attributes.put(name, value.toString());
	}

}
