package jp.co.altonotes.webapp.property;


/**
 * �I�u�W�F�N�g����v���p�e�B���ɑΉ�����l���擾������A�l���Z�b�g�����肷��B
 * @author Yamamoto Keita
 *
 */
public class PropertyAccessor {
	
	// RequestInfo#bind �Ŏg�p -----------------------------------------

	/**
	 * �I�u�W�F�N�g�̎w�肵���v���p�e�B��String�l���Z�b�g����B
	 * RequestInfo#bind �Ŏg�p�B
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
		
		// ���s���͌x�����O���o��
	}
	
	/**
	 * �I�u�W�F�N�g�̎w�肵���v���p�e�B��String[]�l���Z�b�g����B
	 * RequestInfo#bind �Ŏg�p�B
	 * @param rootName 
	 * @param obj
	 * @param propertyName
	 * @param value
	 */
	public static void setProperty(String rootName, Object obj, String propertyName, String[] value) {
		// ���s���͌x�����O���o��
	}

	// ScopeAccessor �Ŏg�p�B �e��JSP�^�O����Ă΂�� ------------------------------------------------------------------------

	/**
	 * 
	 * @param rootName
	 * @param obj
	 * @param propertyName
	 * @return
	 */
	public static Object getProperty(String rootName, Object obj, String propertyName) {
		// ���s���͌x�����O���o��
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
		// ���s���͌x�����O���o��
		return false;
	}
	
	public static boolean doTestMethod(String rootName, Object obj, String propertyName) {
		// ���s���͌x�����O���o��
		return false;
	}
}
