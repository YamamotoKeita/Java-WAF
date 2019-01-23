package jp.co.altonotes.webapp.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.webapp.PropertyAccessor;
import jp.co.altonotes.webapp.PropertyAccessor.AccessResult;

/**
 * Request, Session, Page, Application��Attribute�̎󂯓n�����s��
 *
 * @author Yamamoto Keita
 *
 */
public class ScopeAccessor {

	/**
	 * Scope���ɑΉ�����Scope���쐬����B
	 *
	 * @param page
	 * @param scopeName
	 * @return �Ή�����Scope
	 */
	public static IScope create(PageContext page, String scopeName) {

		if (scopeName == null) {
			HttpServletRequest req = (HttpServletRequest)page.getRequest();
			HttpSession ses = req.getSession(false);
			return new FullScope(page.getServletContext(), ses, req, page);
		} else if (scopeName.equalsIgnoreCase("request")) {
			return new RequestScope(page.getRequest());
		} else if (scopeName.equalsIgnoreCase("session")) {
			HttpServletRequest req = (HttpServletRequest)page.getRequest();
			return new SessionScope(req.getSession(false));
		} else if (scopeName.equalsIgnoreCase("page")) {
			return new PageScope(page);
		} else if (scopeName.equalsIgnoreCase("application")) {
			return new ApplicationScope(page.getServletContext());
		}

		throw new IllegalArgumentException("scope���ɕs���Ȓl "+ scopeName + " ���w�肳��܂����Bscope���ɂ� application, session, request, page �܂��� null�l�̂ݎw��ł��܂�");
	}

	/**
	 * Scope����w�肵���L�[�ɂЂ��Â���������擾����B
	 * �Ή�����I�u�W�F�N�g�����݂��Ȃ��ꍇ�󕶎���Ԃ��B
	 *
	 * @param scope
	 * @param key
	 * @return Scope�ɑ��݂�������̃L�[�ɕR�Â�������
	 */
	public static String extractString(IScope scope, String key) {
		Object obj = extract(scope, key);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * Scope����w�肵���L�[�ɂЂ��Â�boolean�l���擾����B
	 *
	 * @param scope
	 * @param key
	 * @return Scope�ɑ��݂�������̃L�[�ɕR�Â�boolean�l
	 */
	public static boolean extractFlag(IScope scope, String key) {
		String attributeName = null;
		String propertyName = null;
		int idx = key.indexOf(PropertyAccessor.ATTRIBUTE_SEPARATOR);
		if (idx == -1) {
			attributeName = key;
		} else {
			attributeName = key.substring(0, idx);
			propertyName = key.substring(idx + 1);
		}

		return extractFlag(scope, attributeName, propertyName);
	}

	/**
	 * Scope����w�肵���L�[�̃��\�b�h�����s���Aboolean�l���擾����B
	 *
	 * @param scope
	 * @param key
	 * @return Scope�ɑ��݂�������̃L�[�ɕR�Â����\�b�h�̎��s���ʂ�boolean�l
	 */
	public static boolean doCheckMethod(IScope scope, String key) {
		String attributeName = null;
		String propertyName = null;
		int idx = key.indexOf(PropertyAccessor.ATTRIBUTE_SEPARATOR);
		if (idx == -1) {
			String message = "[�x��] method �����̋L�q���s���S�ł��F" + key;
			System.out.println(message);
			return false;
		}

		attributeName = key.substring(0, idx);
		propertyName = key.substring(idx + 1);

		//TODO attributeName�ɃC���f�b�N�X�w�肪����ꍇ�A�z��A�N�Z�X�ł���悤�ɂ���
		Object obj = scope.getAttribute(attributeName);
		if (obj == null) {
			String message = "[�x��] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " �����s�ł��܂���ł����F"
				+ scope + " �� \"" + attributeName + "\" ������܂���B";
			System.out.println(message);
			return false;
		}

		AccessResult result = PropertyAccessor.doNestedCheckMethod(attributeName, obj, propertyName);

		if (!result.isSuccess()) {
			String message = "[�x��] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " �����s�ł��܂���ł���";
			if (result.hasMessage()) {
				System.out.println(message + "�F" + result.message);
			} else {
				System.out.println(message);
			}
		}

		return (Boolean) result.value;
	}

	/**
	 * Scope����w�肵���L�[�ɂЂ��Â��I�u�W�F�N�g���擾����B
	 *
	 * �L�[�̃t�H�[�}�b�g�́u�A�g���r���[�g��.�v���p�e�BA.�v���p�e�BB.�v���p�e�BC�v
	 * �v���p�e�B���z���Collection�̏ꍇ�A�v�f�ԍ����w�肷�邱�Ƃ��ł���B
	 *
	 * ��jcustomer.Address[1].Postcode.FirstCode
	 *
	 * @param scope
	 * @param key
	 * @return Scope�ɑ��݂�������̃L�[�ɕR�Â��I�u�W�F�N�g
	 */
	public static Object extract(IScope scope, String key) {
		String attributeName = null;
		String propertyName = null;
		int idx = key.indexOf(PropertyAccessor.ATTRIBUTE_SEPARATOR);
		if (idx == -1) {
			attributeName = key;
		} else {
			attributeName = key.substring(0, idx);
			propertyName = key.substring(idx + 1);
		}

		return extract(scope, attributeName, propertyName);
	}

	/**
	 * Attribute���ƃv���p�e�B�����w�肵�āAScope����I�u�W�F�N�g���擾����B
	 *
	 * @param scope
	 * @param attributeName
	 * @param propertyName
	 * @return
	 */
	private static Object extract(IScope scope, String attributeName, String propertyName) {
		//TODO attributeName�ɃC���f�b�N�X�w�肪����ꍇ�A�z��A�N�Z�X�ł���悤�ɂ���

		Object obj = scope.getAttribute(attributeName);
		if (obj == null) {
			return null;
		}
		if (propertyName == null || propertyName.length() == 0) {
			return obj;
		}

		AccessResult result = PropertyAccessor.getNestedProperty(attributeName, obj, propertyName);

		if (!result.isSuccess()) {
			String message = "[�x��] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " ���擾�ł��܂���ł���";
			if (result.hasMessage()) {
				System.out.println(message + "�F" + result.message);
			} else {
				System.out.println(message);
			}
		}

		return result.value;
	}

	/**
	 * Attribute���ƃv���p�e�B�����w�肵�āAScope����boolean�l���擾����B
	 *
	 * @param scope
	 * @param attributeName
	 * @param propertyName
	 * @return
	 */
	private static boolean extractFlag(IScope scope, String attributeName, String propertyName) {
		//TODO attributeName�ɃC���f�b�N�X�w�肪����ꍇ�A�z��A�N�Z�X�ł���悤�ɂ���
		Object obj = scope.getAttribute(attributeName);
		if (obj == null) {
			return false;
		}

		if (Checker.isEmpty(propertyName)) {
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			} else if (obj instanceof String) {
				return Boolean.valueOf((String)obj);
			}
		}

		AccessResult result = PropertyAccessor.getNestedFlag(attributeName, obj, propertyName);

		if (!result.isSuccess()) {
			String message = "[�x��] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " ���擾�ł��܂���ł���";
			if (result.hasMessage()) {
				System.out.println(message + "�F" + result.message);
			} else {
				System.out.println(message);
			}
			return false;
		}

		return (Boolean) result.value;
	}

}
