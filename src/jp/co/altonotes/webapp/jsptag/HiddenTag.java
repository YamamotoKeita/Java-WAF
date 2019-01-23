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

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * Hidden�^�O
 *
 * @author Yamamoto Keita
 *
 */
public class HiddenTag extends TagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 6504067675563877777L;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	private String name;
	private String scopeName;

	/**
	 * @param name �v���p�e�B�̖��O
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope �v���p�e�B�̃X�R�[�v
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
			out.write("<input type=\"hidden\" name=\"" + name + "\"");
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
		release();
		return EVAL_PAGE;
	}

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
		if ("value".equalsIgnoreCase(name)) {
			throw new JspException("�J�X�^���^�O hidden �� value �������w�肷�邱�Ƃ͂ł��܂���Bvalue �l�� Gateway �ŃZ�b�g�����l�������ŏo�͂���܂��B\n" + 
								   "[�q���g]hidden�l�ŉ��炩�̏ꍇ�������������ƍl���Ă���ꍇ�́A�����URL��ʂɂ���AURL�ɃN�G���p�^���[�^�[������A�Ȃǂ̎�i���g���܂��B\n" +
								   "����t�H�[�����Ń{�^���ɂ��Ăяo�����\�b�h��ς������ꍇ�A@Gateway �A�m�e�[�V������ params�l���w�肷�邱�ƂŃ��\�b�h�̐U�蕪�����\�ł��B\n" +
								   "�܂��A�ǂ����Ă��Œ��Hidden�l���L�q�������ꍇ�́A�ʏ�� HTML �^�O <input type=\"hidden\" /> ���g���܂��B");
		}
		attributes.put(name, value.toString());
	}

}
