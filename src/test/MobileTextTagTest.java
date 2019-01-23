package test;

import static org.junit.Assert.assertEquals;

import java.io.CharArrayWriter;
import java.io.Writer;

import javax.servlet.jsp.JspTagException;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.jsptag.m.MobileTextTag;

import org.junit.BeforeClass;
import org.junit.Test;

public class MobileTextTagTest {
	//DoCoMo, SoftBank, au, iPhone, iPhone3.1, その他	6
	//ひらがな、かたかな、アルファベット、数字	4
	//html, xhtml	2
	//計48パターン

	//format あり、なし	2
	//styleあり、なし	2
	//その他attributeあり、なし	2
	//計6パターン

	private static final String style = "style1 style2";
	private static final String value = "value1";
	private static final String name = "name1";
	private static final String attribute = "attribute1";

	@BeforeClass
	public static void init() {
	}

	@Test
	public void DoCoMoのXHTMLでひらがな() throws Exception {
		String userAgent = "DoCoMo/1.0/F503iS/c10";
		boolean isXHTML = true;
		String format = "hiragana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:h&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void DoCoMoのXHTMLでカタカナ() throws Exception {
		String userAgent = "DoCoMo/1.0/F503iS/c10";
		boolean isXHTML = true;
		String format = "katakana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:hk&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void DoCoMoのXHTMLでアルファベット() throws Exception {
		String userAgent = "DoCoMo/1.0/F503iS/c10";
		boolean isXHTML = true;
		String format = "alphabet";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:en&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void DoCoMoのXHTMLで数字() throws Exception {
		String userAgent = "DoCoMo/1.0/F503iS/c10";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:n&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void SoftBankのXHTMLでひらがな() throws Exception {
		String userAgent = "SoftBank/1.0/941SC/SCJ001[/Serial] Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		boolean isXHTML = true;
		String format = "hiragana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:h&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void SoftBankのXHTMLでカタカナ() throws Exception {
		String userAgent = "SoftBank/1.0/941SC/SCJ001[/Serial] Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		boolean isXHTML = true;
		String format = "katakana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:hk&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void SoftBankのXHTMLでアルファベット() throws Exception {
		String userAgent = "SoftBank/1.0/941SC/SCJ001[/Serial] Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		boolean isXHTML = true;
		String format = "alphabet";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:en&gt;&quot; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setFormat(format);
		tag.setStyle(style);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void SoftBankのXHTMLで数字() throws Exception {
		String userAgent = "SoftBank/1.0/941SC/SCJ001[/Serial] Browser/NetFront/3.5 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:&quot;*&lt;ja:n&gt;&quot;\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void AUのXHTMLでひらがな() throws Exception {
		String userAgent = "KDDI-SA31 UP.Browser/6.2.0.7.3.129 (GUI) MMP/2.0";
		boolean isXHTML = true;
		String format = "hiragana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:*M; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void AUのXHTMLでカタカナ() throws Exception {
		String userAgent = "KDDI-SA31 UP.Browser/6.2.0.7.3.129 (GUI) MMP/2.0";
		boolean isXHTML = true;
		String format = "katakana";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:*M; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void AUのXHTMLでアルファベット() throws Exception {
		String userAgent = "KDDI-SA31 UP.Browser/6.2.0.7.3.129 (GUI) MMP/2.0";
		boolean isXHTML = true;
		String format = "alphabet";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:*m; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void AUのXHTMLで数字() throws Exception {
		String userAgent = "KDDI-SA31 UP.Browser/6.2.0.7.3.129 (GUI) MMP/2.0";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"-wap-input-format:*N; style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void iPhone3_1のXHTMLでひらがな() throws Exception {
		String userAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1 like Mac OS X; ja-jp) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7C144 Safari/528.16";
		boolean isXHTML = true;
		String format = "hiragana";

		String expected = "<input name=\"name1\" value=\"value1\" autocapitalize=\"off\" type=\"text\" style=\"style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void iPhone3_1のXHTMLで数字() throws Exception {
		String userAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1 like Mac OS X; ja-jp) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7C144 Safari/528.16";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" autocapitalize=\"off\" type=\"number\" style=\"style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void iPhone2_1のXHTMLで数字() throws Exception {
		String userAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 2_1 like Mac OS X; ja-jp) AppleWebKit/525.18.1 (KHTML, like Gecko) Version/3.1.1 Mobile/5F136 Safari/525.20";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" autocapitalize=\"off\" type=\"text\" style=\"style1 style2\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test
	public void AndroidのXHTMLで数字() throws Exception {
		String userAgent = "Mozilla/5.0 (Linux; U; Android 1.0; en-us; dream) AppleWebKit/525.10+ (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2";
		boolean isXHTML = true;
		String format = "number";

		String expected = "<input name=\"name1\" value=\"value1\" type=\"text\" style=\"style1 style2\" attribute1=\"attribute1\" />";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);
		tag.setDynamicAttribute(null, attribute, attribute);

		tag.out(mobileInfo, out, value, isXHTML);
		assertEquals(expected, out.toString());
	}

	@Test(expected = JspTagException.class)
	public void 不正なフォーマット() throws Exception {
		String userAgent = "Mozilla/5.0 (Linux; U; Android 1.0; en-us; dream) AppleWebKit/525.10+ (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2";
		boolean isXHTML = true;
		String format = "???";

		MobileInfo mobileInfo = new MobileInfo(userAgent);

		Writer out = new CharArrayWriter();

		MobileTextTag tag = new MobileTextTag();
		tag.setName(name);
		tag.setStyle(style);
		tag.setFormat(format);
		tag.setDynamicAttribute(null, attribute, attribute);

		tag.out(mobileInfo, out, value, isXHTML);
	}

}
