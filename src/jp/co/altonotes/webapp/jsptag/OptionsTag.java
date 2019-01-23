package jp.co.altonotes.webapp.jsptag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.webapp.form.ISelectOption;

/**
 * SelectOptionの配列やコレクションを元に、複数のオプションタグを出力する
 * 
 * @author Yamamoto Keita
 *
 */
public class OptionsTag extends TagSupport {
	
	private static final long serialVersionUID = -933466310343772341L;
	
	private static final Class<ISelectOption> INTERFACE_CLASS = ISelectOption.class;
	
	private static final String TYPE_MISMATCH_MESSAGE = "items 属性には " + INTERFACE_CLASS.getName() + 
														" の配列か Collection<? extends " + INTERFACE_CLASS.getName() +
														" > しかセットできません。";
	
	private ISelectOption[] items;
	
	/**
	 * Itemsをセットする。
	 * 
	 * @param obj
	 */
	public void setItems(Object obj) {

		if (obj == null) {
			items = new ISelectOption[0];
			return;
		}

		Class<?> klass = obj.getClass();
		
		// 配列の場合
		if (klass.isArray() && INTERFACE_CLASS.isAssignableFrom(klass.getComponentType())) {
			items = (ISelectOption[]) obj;
		} 
		// Collectionの場合
		else if (obj instanceof Collection<?>) {
			
			Collection<?> collection = (Collection<?>) obj;
			List<ISelectOption> list = new ArrayList<ISelectOption>();

			for (Object element : collection) {
				if (element == null) {
					continue;
				} else if (INTERFACE_CLASS.isAssignableFrom(element.getClass())) {
					list.add((ISelectOption)element);
				} else {
					throw new IllegalArgumentException(TYPE_MISMATCH_MESSAGE);
				}
			}

			items = list.toArray(new ISelectOption[list.size()]);
			
		} else {
			throw new IllegalArgumentException(TYPE_MISMATCH_MESSAGE);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
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

			for (ISelectOption item : items) {
				
				out.write("<option value=\"" + item.getValue() + "\"");
				if (selectedValue != null && selectedValue.equals(item.getValue())){
					if (isXHTML) {
						out.write(" selected=\"selected\"");
					} else {
						out.write(" selected");
					}
				}
				out.write(">");

				// ラベルをセット
				String label = item.getLabel();
				if (label == null) {
					label = "";
				}
				out.write(label);
				
				out.write("</option>\r\n");
			}

		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		items = null;
	}
}
