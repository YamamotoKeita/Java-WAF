package jp.co.altonotes.webapp.jsptag.m;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.co.altonotes.mobile.MobileAgentType;
import jp.co.altonotes.mobile.MobileInfo;
import jp.co.altonotes.webapp.RequestContext;

/**
 * 携帯機種のメール送信リンクを出力する。
 *
 * @author Yamamoto Keita
 *
 */
public class MobileMailLinkTag extends BodyTagSupport {

	private static final long serialVersionUID = 2884983728860965340L;

	private String subject;
	private String body;
	private String encoding;
	private boolean canUseMailTo;

	/**
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public int doStartTag() throws JspTagException {
		MobileInfo mobileInfo = RequestContext.getMobileInfo();
		canUseMailTo = canUseMailTo(mobileInfo);

		if (!canUseMailTo) {
			return SKIP_BODY;
		}

		String emoji = MobileEmojiTag.get("mail", MobileEmojiTag.getCareerCode(mobileInfo));

		if (emoji == null) {
			emoji = "　";
		}

		JspWriter out = pageContext.getOut();

		String query = mailToQuery(mobileInfo, subject, body, encoding);

		try {
			out.write(emoji + "&nbsp;");
			out.write("<a href=\"" + query + "\">");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return EVAL_BODY_AGAIN;
	}

	@Override
	public int doAfterBody() throws JspTagException {
		if (!canUseMailTo) {
			return SKIP_BODY;
		}

		JspWriter out = bodyContent.getEnclosingWriter();

		try {
			out.write(bodyContent.getString());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException{
		if (!canUseMailTo) {
			return EVAL_PAGE;
		}

		JspWriter out = pageContext.getOut();
		try {
			out.write("</a>");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		release();

		return EVAL_PAGE;
	}

	@Override
	public void release() {
		subject = null;
		body = null;
		encoding = null;
		canUseMailTo = false;
	}

	/**
	 * mailtoのクエリを作成する
	 *
	 * @param mobileInfo
	 * @param subject
	 * @param body
	 * @param encoding
	 * @return mailto のクエリ
	 */
	public static String mailToQuery(MobileInfo mobileInfo, String subject, String body, String encoding) {
		MobileAgentType agentType = mobileInfo.getAgentType();
		if (agentType == MobileAgentType.SOFTBANK || agentType == MobileAgentType.VODAFONE || mobileInfo.isIPhone()) {
			encoding = "UTF-8";
		}

		try {
			if (agentType != MobileAgentType.J_PHONE_C) {
				subject = URLEncoder.encode(subject, encoding);
				body = URLEncoder.encode(body, encoding);
			}
		} catch (UnsupportedEncodingException ignored) {}

		String query = "mailto:?";
		if (agentType != MobileAgentType.J_PHONE_C) {
			query += "subject=" + subject + "&";
		}
		if (agentType == MobileAgentType.J_PHONE_C) {
			query += "mailbody=" + body;
		} else {
			query += "body=" + body;
		}
		return query;
	}

	/**
	 * mailto が使えるか判定する。
	 * @param mobileInfo 
	 *
	 * @return mailto が使える機種の場合 true
	 */
	public boolean canUseMailTo(MobileInfo mobileInfo) {
		return mobileInfo.isDocomo() ||
			   mobileInfo.isAU() ||
			   mobileInfo.isSoftBank() ||
			   mobileInfo.isIPhone() ||
			   mobileInfo.isWillcom();
	}
}
