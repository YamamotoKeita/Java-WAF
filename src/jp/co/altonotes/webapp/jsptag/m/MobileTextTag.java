package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.jsptag.TagUtil;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �g�уT�C�g�p�e�L�X�g�{�b�N�X�^�O�B<br>
 * �@��ɉ����đ����Ɏw�肵��format�ɏ��������e�L�X�g�{�b�N�X���쐬����B
 *
 * @author Yamamoto Keita
 *
 */
public class MobileTextTag extends TagSupport implements DynamicAttributes  {

//	��HTML
//	DoCoMo, ez-web
//		<input type="text" istyle="1"> ���S�p����
//		<input type="text" istyle="2"> �����p�J�i
//		<input type="text" istyle="3"> �����p�p��
//		<input type="text" istyle="4"> �����p����
//
//	SoftBank
//		<input type="text" mode="hiragana"> ���S�p����
//		<input type="text" mode="katakana"> �����p�J�i
//		<input type="text" mode="alphabet"> �����p�p��
//		<input type="text" mode="numeric"> �����p����
//
//	au
//		<input type="text" format="*M">  ���S�p����
//		<input type="text" format="*a">  �����p�p���i�������j
//		<input type="text" format="*N">  �����p����
//		<input type="text" format="*A">  �����p�p���i�啶���j
//		<input type="text" format="*X">  �����p�p���i�啶���j
//		<input type="text" format="*x">  �����p�p���i�������j
//		<input type="text" format="*m">  ���S�p�p��
//
//		<input type="text" format="4A">  �����p�p���i�啶���j
//
//	��XHTML
//	DoCoMo, SoftBank
//		style="-wap-input-format:&quot;*&lt;ja:h&gt;&quot;" ���S�p����
//		style="-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;" �����p�J�i
//		style="-wap-input-format:&quot;*&lt;ja:en&gt;&quot;" ���p��/���p����
//		style="-wap-input-format:&quot;*&lt;ja:n&gt;&quot;" ������/���p����
//
//	au
//		style="-wap-input-format:*M;" ���S�p����
//		style="-wap-input-format:*m;" ���p��/���p����
//		style="-wap-input-format:*N;" ������/���p����
//
//	�O�L�����A
//		style="-wap-input-format:&quot;*&lt;ja:h&gt;&quot;;-wap-input-format:*M;"	�S�p����
//		style="-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;;-wap-input-format:*M;"	���p�J�i�iAu�͖����̂őS�p���ȂŁj
//		style="-wap-input-format:&quot;*&lt;ja:en&gt;&quot;;-wap-input-format:*m;"	���p�p��
//		style="-wap-input-format:&quot;*&lt;ja:n&gt;&quot;;-wap-input-format:*N;"	���p����
//
//	��iPhone
//		�����啶����off	<input type="text" autocapitalize="off">
//		�I�[�g�R���N�g�I�t	<input type="text" autocorrect="off">
//
//		�f�t�H���g���͂𐔎��L�[�{�[�h�ɂ���Finput��name������ "zip" or "phone" ���܂߂�
//
//		���ȉ��̑�����HTML5�̎����ł���AiPhoneOS 3.1�ȍ~�ŃT�|�[�g�����B
//
//		Text: <input type="text" /> <!-- display a standard keyboard -->
//		Telephone: <input type="tel" /> <!-- display a telephone keypad -->
//		URL: <input type="url" /> <!-- display a URL keyboard -->
//		Email: <input type="email" /> <!-- display an email keyboard -->
//		Zip Code: <input type="text" pattern="[0-9]*" /> <!-- display a numeric keyboard -->
//		Number: <input type="number"
//		Search: <input type="search"

	private static final long serialVersionUID = 5292754667698314220L;

	private static final String ALPHABET = "alphabet";
	private static final String NUMBER = "number";
	private static final String ZENKAKU_KANA = "hiragana";
	private static final String HANKAKU_KANA = "katakana";

	private static final int CODE_UNDEFINED = -1;
	private static final int CODE_ALPHABET = 1;
	private static final int CODE_NUMBER = 2;
	private static final int CODE_ZENKAKU_KANA = 3;
	private static final int CODE_HANKAKU_KANA = 4;

	private String name;
	private String scopeName;//scope�Ƃ������O�͗\�񂳂�Ă�̂�scopeName�ɂ���
	private String style;
	private String format;

	private int formatCode = CODE_UNDEFINED;

	private HashMap<String, String> mAttributes = new HashMap<String, String>();

	/**
	 * @param name �Ή�����v���p�e�B
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scopeName �X�R�[�v�Bsession, request�Ȃ�
	 */
	public void setScope(String scopeName) {
		this.scopeName = scopeName;
	}

	/**
	 * @param style CSS�X�^�C��
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @param format ���͂��镶���̃t�H�[�}�b�g�Balphabet, number, hiragana, katakana���w��\�B
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public int doStartTag() throws JspTagException {
		MobileInfo mobileInfo = RequestContext.getMobileInfo();

		IScope scope = ScopeAccessor.create(pageContext, scopeName);
		String value = ScopeAccessor.extractString(scope, name);

		value = TextUtils.htmlEscape(value);

		boolean isXHTML = TagUtil.isXHTML(pageContext);

		JspWriter out = pageContext.getOut();

		try {
			out(mobileInfo, out, value, isXHTML);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	/**
	 * �^�O���o�͂���B
	 *
	 * @param mobileInfo
	 * @param out
	 * @param value
	 * @param isXHTML
	 * @throws IOException
	 * @throws JspTagException
	 */
	public void out(MobileInfo mobileInfo, Writer out, String value, boolean isXHTML) throws IOException, JspTagException {
		setFormatCode();

		out.write("<input name=\"" + name + "\" value=\"" + value + "\" ");

		if (mobileInfo.isIPhone()) {
			out.write("autocapitalize=\"off\" ");
		}

		if (mobileInfo.isIPhoneSupportingHTML5()) {
			html5(out);
		} else {
			out.write("type=\"text\"");

			if (isXHTML) {
				xhtml(mobileInfo, out);
			} else {
				html(mobileInfo, out);
			}
		}

		//���̑�Attribute�̒ǉ�
		Set<Entry<String, String>> set = mAttributes.entrySet();
		for (Entry<String, String> entry : set) {
			out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}

		//���^�O
		if (isXHTML) {
			out.write(" />");
		} else {
			out.write(">");
		}
	}

	private void xhtml(MobileInfo mobileInfo, Writer out) throws IOException {
		if (mobileInfo.isDocomo()) {
			xhtmlDoCoMo(out);
		} else if (mobileInfo.isAU()) {
			xhtmlAU(out);
		} else if (mobileInfo.isSoftBank()) {
			xhtmlSoftBank(out);
		} else if (Checker.isNotEmpty(style)){
			out.write(" style=\"" + style + "\"");
		}
	}

	private void xhtmlDoCoMo(Writer out) throws IOException {
		String style = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			style = "-wap-input-format:&quot;*&lt;ja:h&gt;&quot;";
			break;
		case CODE_HANKAKU_KANA:
			style = "-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;";
			break;
		case CODE_ALPHABET:
			style = "-wap-input-format:&quot;*&lt;ja:en&gt;&quot;";
			break;
		case CODE_NUMBER:
			style = "-wap-input-format:&quot;*&lt;ja:n&gt;&quot;";
			break;
		}

		//�X�^�C����ǉ�
		if (Checker.isNotEmpty(this.style)) {
			if (style == null) {
				style = this.style;
			} else {
				style += " " + this.style;
			}
		}

		if (Checker.isNotEmpty(style)) {
			out.write(" style=\"" + style + "\"");
		}
	}

	private void xhtmlSoftBank(Writer out) throws IOException {
		String style = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			style = "-wap-input-format:&quot;*&lt;ja:h&gt;&quot;";
			break;
		case CODE_HANKAKU_KANA:
			style = "-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;";
			break;
		case CODE_ALPHABET:
			style = "-wap-input-format:&quot;*&lt;ja:en&gt;&quot;";
			break;
		case CODE_NUMBER:
			style = "-wap-input-format:&quot;*&lt;ja:n&gt;&quot;";
			break;
		}

		//�X�^�C����ǉ�
		if (Checker.isNotEmpty(this.style)) {
			if (style == null) {
				style = this.style;
			} else {
				style += " " + this.style;
			}
		}

		if (Checker.isNotEmpty(style)) {
			out.write(" style=\"" + style + "\"");
		}
	}

	private void xhtmlAU(Writer out) throws IOException {
		String style = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			style = "-wap-input-format:*M;";
			break;
		case CODE_HANKAKU_KANA:
			style = "-wap-input-format:*M;";
			break;
		case CODE_ALPHABET:
			style = "-wap-input-format:*m;";
			break;
		case CODE_NUMBER:
			style = "-wap-input-format:*N;";
			break;
		}

		//�X�^�C����ǉ�
		if (Checker.isNotEmpty(this.style)) {
			if (style == null) {
				style = this.style;
			} else {
				style += " " + this.style;
			}
		}

		if (Checker.isNotEmpty(style)) {
			out.write(" style=\"" + style + "\"");
		}
	}

	private void html5(Writer out) throws IOException {
		String type = "text";

		//���̓��[�h������
		if (formatCode == CODE_NUMBER) {
			type = "number";
		}

		out.write("type=\"" + type + "\"");

		if (Checker.isNotEmpty(style)) {
			out.write(" style=\"" + this.style + "\"");
		}
	}

	private void html(MobileInfo mobileInfo, Writer out) throws IOException {

		if (mobileInfo.isDocomo()) {
			htmlDoCoMo(out);
		} else if (mobileInfo.isAU()) {
			htmlAU(out);
		} else if (mobileInfo.isSoftBank()) {
			htmlSoftBank(out);
		}

		if (Checker.isNotEmpty(style)){
			out.write(" style=\"" + style + "\"");
		}
	}

	private void htmlDoCoMo(Writer out) throws IOException {
		String istyle = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			istyle = "1";
			break;
		case CODE_HANKAKU_KANA:
			istyle = "2";
			break;
		case CODE_ALPHABET:
			istyle = "3";
			break;
		case CODE_NUMBER:
			istyle = "4";
			break;
		}

		if (istyle != null) {
			out.write(" istyle=\"" + istyle + "\"");
		}
	}

	private void htmlSoftBank(Writer out) throws IOException {
		String mode = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			mode = "hiragana";
			break;
		case CODE_HANKAKU_KANA:
			mode = "katakana";
			break;
		case CODE_ALPHABET:
			mode = "alphabet";
			break;
		case CODE_NUMBER:
			mode = "numeric";
			break;
		}

		if (mode != null) {
			out.write(" mode=\"" + mode + "\"");
		}
	}

	private void htmlAU(Writer out) throws IOException {
		String istyle = null;
		String format = null;

		//���̓��[�h������
		switch (formatCode) {
		case CODE_ZENKAKU_KANA:
			istyle = "1";
			format = "*M";
			break;
		case CODE_HANKAKU_KANA:
			istyle = "2";
			format = "*M";
			break;
		case CODE_ALPHABET:
			istyle = "3";
			format = "*a";
			break;
		case CODE_NUMBER:
			istyle = "4";
			format = "*N";
			break;
		}

		if (istyle != null) {
			out.write(" istyle=\"" + istyle + "\"");
		}

		if (format != null) {
			out.write(" format=\"" + format + "\"");
		}
	}

	private void setFormatCode() throws JspTagException {
		if (Checker.isNotEmpty(format)) {
			if (ALPHABET.equalsIgnoreCase(format)) {
				formatCode = CODE_ALPHABET;
			} else if (NUMBER.equalsIgnoreCase(format)) {
				formatCode = CODE_NUMBER;
			} else if (ZENKAKU_KANA.equalsIgnoreCase(format)) {
				formatCode = CODE_ZENKAKU_KANA;
			} else if (HANKAKU_KANA.equalsIgnoreCase(format)) {
				formatCode = CODE_HANKAKU_KANA;
			} else {
				throw new JspTagException("format=\"" + format + "\" �͕s���ł��Bformat�ɂ� " + ALPHABET + "," + NUMBER + "," + ZENKAKU_KANA + "," + HANKAKU_KANA + " �̂����ꂩ�̂ݎw��ł��܂��B");
			}
		}
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

	@Override
	public void release() {
		name = null;
		scopeName = null;
		style = null;
		format = null;
		formatCode = CODE_UNDEFINED;
		mAttributes = new HashMap<String, String>();
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		mAttributes.put(name, value.toString());
	}

}
