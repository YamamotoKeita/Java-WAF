package jp.co.altonotes.webapp;

/**
 * Viewの表示方法。<br>
 *
 * <pre>
 * Navigator.REDIRECT + "/path"
 * Navigator.CROSS_APP_FORWARD + "context://path/cvc"
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public final class ViewType {

	/** コンテキストパスからの相対パスによるリダイレクト */
	public static final String REDIRECT = "redirect > ";

	/** 絶対パスによるリダイレクト */
	public static final String URL_REDIRECT = "absolute-redirect > ";

	/** 別コンテキストパスへのフォワード */
	public static final String FORWARD_TO_OTHER_CONTEXT = "other-context > ";

	/**
	 * 別コンテキストパスへのフォワードを指示するViewのキー。
	 * 
	 * @param contextPath
	 * @param path
	 * @return 別コンテキストパスへのフォワードを指示する文字列
	 */
	public static String forwardToOtherContext(String contextPath, String path) {
		return FORWARD_TO_OTHER_CONTEXT + contextPath + " > " + path;
	}
}
