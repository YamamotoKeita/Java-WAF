package jp.co.altonotes.webapp.exception;

/**
 * フレームワークのバグによって発生するException。<br>
 * このExceptionが発生することは、本来あってはならない。
 * 
 * @author Yamamoto Keita
 */
public class FrameworkBugException extends RuntimeException {

	private static final long serialVersionUID = -3702627282515135027L;
	
	/**
	 * コンストラクター
	 * @param string
	 */
	public FrameworkBugException(String string) {
		super(string);
	}
	
	/**
	 * コンストラクター
	 */
	public FrameworkBugException() {}


}
