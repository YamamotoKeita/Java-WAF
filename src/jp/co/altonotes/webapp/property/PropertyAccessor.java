package jp.co.altonotes.webapp.property;


/**
 * オブジェクトからプロパティ名に対応する値を取得したり、値をセットしたりする。
 * @author Yamamoto Keita
 *
 */
public class PropertyAccessor {
	
	// RequestInfo#bind で使用 -----------------------------------------

	/**
	 * オブジェクトの指定したプロパティにString値をセットする。
	 * RequestInfo#bind で使用。
	 * 
	 * @param rootName 
	 * @param obj
	 * @param propertyName
	 * @param value
	 */
	public static void setProperty(String rootName, Object obj, String propertyName, String value) {
		Result result = new Result();
		
		PropertyNode rootObj = PropertyNodeFactory.createRoot(obj, propertyName, result);
		if (result.isFailed) {
			
		}
		
		rootObj.setValueToLastNode(value, result);
		
		// 失敗時は警告ログを出す
	}
	
	/**
	 * オブジェクトの指定したプロパティにString[]値をセットする。
	 * RequestInfo#bind で使用。
	 * @param rootName 
	 * @param obj
	 * @param propertyName
	 * @param value
	 */
	public static void setProperty(String rootName, Object obj, String propertyName, String[] value) {
		// 失敗時は警告ログを出す
	}

	// ScopeAccessor で使用。 各種JSPタグから呼ばれる ------------------------------------------------------------------------

	/**
	 * 
	 * @param rootName
	 * @param obj
	 * @param propertyName
	 * @return
	 */
	public static Object getProperty(String rootName, Object obj, String propertyName) {
		// 失敗時は警告ログを出す
		return null;
	}
	
	/**
	 * 
	 * @param rootName
	 * @param obj
	 * @param propertyName
	 * @return
	 */
	public static boolean getPropertyBool(String rootName, Object obj, String propertyName) {
		// 失敗時は警告ログを出す
		return false;
	}
	
	public static boolean doTestMethod(String rootName, Object obj, String propertyName) {
		// 失敗時は警告ログを出す
		return false;
	}
}
