package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * �R���{�{�b�N�X�̃I�v�V�������L�q����^�O
 * 
 * @author Yamamoto Keita
 *
 */
public class OptionTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 2494933891976898865L;

	private String value;

	private HashMap<String, String> attributes = new HashMap<String, String>();

	/**
	 * @param obj value�ɃZ�b�g����l
	 */
	public void setValue(Object obj) {
		this.value = String.valueOf(obj);
	}

	@Override
	public int doStartTag() throws JspTagException {
		JspWriter out = pageContext.getOut();
		SelectTag parent = null;
		String selectedValue = null;
		Object parentObj = findAncestorWithClass(this, SelectTag.class);
		if (parentObj != null) {
			parent = (SelectTag) parentObj;
			selectedValue = parent.getValue();
		}

		try {
			boolean isXHTML = TagUtil.isXHTML(pageContext);

			out.write("<option value=\"" + value + "\"");

			Set<Entry<String, String>> set = attributes.entrySet();
			for (Entry<String, String> entry : set) {
				out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}

			if (selectedValue != null && value.equals(selectedValue)){
				if (isXHTML) {
					out.write(" selected=\"selected\"");
				} else {
					out.write(" selected");
				}
			}
			out.write(">");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspTagException{
		JspWriter out = pageContext.getOut();
		try {
			out.write("</option>\r\n");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		value = null;
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
