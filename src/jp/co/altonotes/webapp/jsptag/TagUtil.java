package jp.co.altonotes.webapp.jsptag;

import javax.servlet.jsp.PageContext;

import jp.co.altonotes.webapp.RequestContext;
import jp.co.altonotes.webapp.RequestInfo;

/**
 * �J�X�^���^�O�N���X�Ŏg�p���郆�[�e�B���e�B�[
 * @author Yamamoto Keita
 *
 */
public class TagUtil {

	/**
	 * config.xml �Ŏw�肵�� HTML�^�C�v�� XHTML �����肷��
	 * @param pageContext
	 * @return �y�[�W�� XHTML �̏ꍇ true
	 */
	public static boolean isXHTML(PageContext pageContext) {
		RequestInfo context = RequestContext.getCurrentContext(pageContext.getRequest());
		return context.isXHTMLPage();
	}

	/**
	 * config.xml ��URL�Z�b�V�������L���ɐݒ肳��Ă��邩���肷��
	 * @param pageContext
	 * @return URL�Z�b�V�������L���ɂȏꍇ true
	 */
	public static boolean isAvailableURLSession(PageContext pageContext) {
		RequestInfo context = RequestContext.getCurrentContext(pageContext.getRequest());
		return context.isAvailableURLSession();
	}
}
