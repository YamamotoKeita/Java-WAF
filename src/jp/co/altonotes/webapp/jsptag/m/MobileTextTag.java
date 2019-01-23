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
 * 携帯サイト用テキストボックスタグ。<br>
 * 機種に応じて属性に指定したformatに準拠したテキストボックスを作成する。
 *
 * @author Yamamoto Keita
 *
 */
public class MobileTextTag extends TagSupport implements DynamicAttributes  {

//	◆HTML
//	DoCoMo, ez-web
//		<input type="text" istyle="1"> →全角かな
//		<input type="text" istyle="2"> →半角カナ
//		<input type="text" istyle="3"> →半角英字
//		<input type="text" istyle="4"> →半角数字
//
//	SoftBank
//		<input type="text" mode="hiragana"> →全角かな
//		<input type="text" mode="katakana"> →半角カナ
//		<input type="text" mode="alphabet"> →半角英字
//		<input type="text" mode="numeric"> →半角数字
//
//	au
//		<input type="text" format="*M">  →全角かな
//		<input type="text" format="*a">  →半角英字（小文字）
//		<input type="text" format="*N">  →半角数字
//		<input type="text" format="*A">  →半角英字（大文字）
//		<input type="text" format="*X">  →半角英数（大文字）
//		<input type="text" format="*x">  →半角英数（小文字）
//		<input type="text" format="*m">  →全角英字
//
//		<input type="text" format="4A">  →半角英字（大文字）
//
//	◆XHTML
//	DoCoMo, SoftBank
//		style="-wap-input-format:&quot;*&lt;ja:h&gt;&quot;" →全角かな
//		style="-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;" →半角カナ
//		style="-wap-input-format:&quot;*&lt;ja:en&gt;&quot;" →英字/半角文字
//		style="-wap-input-format:&quot;*&lt;ja:n&gt;&quot;" →数字/半角文字
//
//	au
//		style="-wap-input-format:*M;" →全角かな
//		style="-wap-input-format:*m;" →英字/半角文字
//		style="-wap-input-format:*N;" →数字/半角文字
//
//	三キャリア
//		style="-wap-input-format:&quot;*&lt;ja:h&gt;&quot;;-wap-input-format:*M;"	全角かな
//		style="-wap-input-format:&quot;*&lt;ja:hk&gt;&quot;;-wap-input-format:*M;"	半角カナ（Auは無いので全角かなで）
//		style="-wap-input-format:&quot;*&lt;ja:en&gt;&quot;;-wap-input-format:*m;"	半角英字
//		style="-wap-input-format:&quot;*&lt;ja:n&gt;&quot;;-wap-input-format:*N;"	半角数字
//
//	◆iPhone
//		自動大文字化off	<input type="text" autocapitalize="off">
//		オートコレクトオフ	<input type="text" autocorrect="off">
//
//		デフォルト入力を数字キーボードにする：inputのname属性に "zip" or "phone" を含める
//
//		※以下の多くはHTML5の実装であり、iPhoneOS 3.1以降でサポートされる。
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
	private String scopeName;//scopeという名前は予約されてるのでscopeNameにする
	private String style;
	private String format;

	private int formatCode = CODE_UNDEFINED;

	private HashMap<String, String> mAttributes = new HashMap<String, String>();

	/**
	 * @param name 対応するプロパティ
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scopeName スコープ。session, requestなど
	 */
	public void setScope(String scopeName) {
		this.scopeName = scopeName;
	}

	/**
	 * @param style CSSスタイル
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @param format 入力する文字のフォーマット。alphabet, number, hiragana, katakanaを指定可能。
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
	 * タグを出力する。
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

		//その他Attributeの追加
		Set<Entry<String, String>> set = mAttributes.entrySet();
		for (Entry<String, String> entry : set) {
			out.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}

		//閉じタグ
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

		//入力モードを決定
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

		//スタイルを追加
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

		//入力モードを決定
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

		//スタイルを追加
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

		//入力モードを決定
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

		//スタイルを追加
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

		//入力モードを決定
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

		//入力モードを決定
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

		//入力モードを決定
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

		//入力モードを決定
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
				throw new JspTagException("format=\"" + format + "\" は不正です。formatには " + ALPHABET + "," + NUMBER + "," + ZENKAKU_KANA + "," + HANKAKU_KANA + " のいずれかのみ指定できます。");
			}
		}
	}

	@Override
	public int doEndTag() {
		//TODO Tomcatはタグインスタンスをプールしている！
		//そのため値を初期化してやる必要があるのだけど、Ja-jakartaのガイドラインには「setXXX() メソッドがセットしたプロパティを変更してはなりません 」とある。
		//でも、めんどくさいので今のところそれに従っていない。cタグも従ってなかったような気がするから。
		//今のところ doEndTag() で初期化しているけれど、どこで初期化するか再考すべき。
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
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
		mAttributes.put(name, value.toString());
	}

}
