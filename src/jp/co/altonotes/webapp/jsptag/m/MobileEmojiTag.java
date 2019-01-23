package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.util.Checker;
import jp.co.altonotes.webapp.RequestContext;

/**
 * ågë—ópÇÃäGï∂éöÇèoóÕÇ∑ÇÈÉ^ÉO
 *
 * @author Yamamoto Keita
 *
 */
public class MobileEmojiTag extends TagSupport {

	private static final long serialVersionUID = 2033631332840987599L;

	private static final String UNKNOWN = "0";
	private static final String DOCOMO = "1";
	private static final String AU = "2";
	private static final String SOFTBANK = "3";

	private static Map<String, String> emojiMap = new TreeMap<String, String>();

	private String type;
	private String arg;

	static {
		put("mail", DOCOMO, "&#63858;");
		put("mail", AU, "<img localsrc=\"108\" />");
		put("mail", SOFTBANK, "$E#");

		put("number", DOCOMO, "1", "&#63879;");
		put("number", DOCOMO, "2", "&#63880;");
		put("number", DOCOMO, "3", "&#63881;");
		put("number", DOCOMO, "4", "&#63882;");
		put("number", DOCOMO, "5", "&#63883;");
		put("number", DOCOMO, "6", "&#63884;");
		put("number", DOCOMO, "7", "&#63885;");
		put("number", DOCOMO, "8", "&#63886;");
		put("number", DOCOMO, "9", "&#63887;");
		put("number", DOCOMO, "0", "&#63888;");

		put("number", AU, "1", "<img localsrc=\"180\" />");
		put("number", AU, "2", "<img localsrc=\"181\" />");
		put("number", AU, "3", "<img localsrc=\"182\" />");
		put("number", AU, "4", "<img localsrc=\"183\" />");
		put("number", AU, "5", "<img localsrc=\"184\" />");
		put("number", AU, "6", "<img localsrc=\"185\" />");
		put("number", AU, "7", "<img localsrc=\"186\" />");
		put("number", AU, "8", "<img localsrc=\"187\" />");
		put("number", AU, "9", "<img localsrc=\"188\" />");
		put("number", AU, "0", "0");

		put("number", SOFTBANK, "1", "$F<");
		put("number", SOFTBANK, "2", "$F=");
		put("number", SOFTBANK, "3", "$F>");
		put("number", SOFTBANK, "4", "$F?");
		put("number", SOFTBANK, "5", "$F@");
		put("number", SOFTBANK, "6", "$FA");
		put("number", SOFTBANK, "7", "$FB");
		put("number", SOFTBANK, "8", "$FC");
		put("number", SOFTBANK, "9", "$FD");
		put("number", SOFTBANK, "0", "$FE");

		put("number", UNKNOWN, "1", "[1]");
		put("number", UNKNOWN, "2", "[2]");
		put("number", UNKNOWN, "3", "[3]");
		put("number", UNKNOWN, "4", "[4]");
		put("number", UNKNOWN, "5", "[5]");
		put("number", UNKNOWN, "6", "[6]");
		put("number", UNKNOWN, "7", "[7]");
		put("number", UNKNOWN, "8", "[8]");
		put("number", UNKNOWN, "9", "[9]");
		put("number", UNKNOWN, "0", "[0]");
	}

	private static void put(String type, String career, String value) {
		String key = type + ":" + career;
		emojiMap.put(key, value);
	}

	private static void put(String type, String career, String arg, String value) {
		String key = type + ":" + career + ":" + arg;
		emojiMap.put(key, value);
	}

	/**
	 * @param type
	 * @param career
	 * @return ëŒè€ÇÃäGï∂éöÉRÅ[Éh
	 */
	public static String get(String type, String career) {
		String key = type + ":" + career;
		return emojiMap.get(key);
	}

	/**
	 * @param type
	 * @param career
	 * @param arg
	 * @returnÅ@ëŒè€ÇÃäGï∂éöÉRÅ[Éh
	 */
	public static String get(String type, String career, String arg) {
		String key = type + ":" + career + ":" + arg;
		return emojiMap.get(key);
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param arg
	 */
	public void setArg(String arg) {
		this.arg = arg;
	}

	@Override
	public int doStartTag() throws JspTagException {

		MobileInfo mobileInfo = RequestContext.getMobileInfo();
		String carrerCode = getCareerCode(mobileInfo);

		JspWriter out = pageContext.getOut();

		String html = null;
		if (Checker.isEmpty(arg)) {
			html = get(type, carrerCode);
		} else {
			html = get(type, carrerCode, arg);
		}

		if (html == null) {
			html = "Å@";
		}

		try {
			out.write(html);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	/**
	 * @param mobileInfo
	 * @return ÉLÉÉÉäÉAÉRÅ[Éh
	 */
	public static String getCareerCode(MobileInfo mobileInfo) {
		if (mobileInfo.isDocomo()) {
			return DOCOMO;
		} else if (mobileInfo.isAU()) {
			return AU;
		} else if (mobileInfo.isSoftBank()) {
			return SOFTBANK;
		}
		return UNKNOWN;
	}

	/*
	 * (îÒ Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		type = null;
		arg = null;
	}

	@Override
	public int doEndTag() {
		release();
		return EVAL_PAGE;
	}
}
