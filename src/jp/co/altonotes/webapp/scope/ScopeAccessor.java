package jp.co.altonotes.webapp.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.webapp.PropertyAccessor;
import jp.co.altonotes.webapp.PropertyAccessor.AccessResult;

/**
 * Request, Session, Page, ApplicationとAttributeの受け渡しを行う
 *
 * @author Yamamoto Keita
 *
 */
public class ScopeAccessor {

	/**
	 * Scope名に対応したScopeを作成する。
	 *
	 * @param page
	 * @param scopeName
	 * @return 対応するScope
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

		throw new IllegalArgumentException("scope名に不正な値 "+ scopeName + " が指定されました。scope名には application, session, request, page または null値のみ指定できます");
	}

	/**
	 * Scopeから指定したキーにひもづく文字列を取得する。
	 * 対応するオブジェクトが存在しない場合空文字を返す。
	 *
	 * @param scope
	 * @param key
	 * @return Scopeに存在する引数のキーに紐づく文字列
	 */
	public static String extractString(IScope scope, String key) {
		Object obj = extract(scope, key);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * Scopeから指定したキーにひもづくboolean値を取得する。
	 *
	 * @param scope
	 * @param key
	 * @return Scopeに存在する引数のキーに紐づくboolean値
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
	 * Scopeから指定したキーのメソッドを実行し、boolean値を取得する。
	 *
	 * @param scope
	 * @param key
	 * @return Scopeに存在する引数のキーに紐づくメソッドの実行結果のboolean値
	 */
	public static boolean doCheckMethod(IScope scope, String key) {
		String attributeName = null;
		String propertyName = null;
		int idx = key.indexOf(PropertyAccessor.ATTRIBUTE_SEPARATOR);
		if (idx == -1) {
			String message = "[警告] method 属性の記述が不完全です：" + key;
			System.out.println(message);
			return false;
		}

		attributeName = key.substring(0, idx);
		propertyName = key.substring(idx + 1);

		//TODO attributeNameにインデックス指定がある場合、配列アクセスできるようにする
		Object obj = scope.getAttribute(attributeName);
		if (obj == null) {
			String message = "[警告] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " が実行できませんでした："
				+ scope + " に \"" + attributeName + "\" がありません。";
			System.out.println(message);
			return false;
		}

		AccessResult result = PropertyAccessor.doNestedCheckMethod(attributeName, obj, propertyName);

		if (!result.isSuccess()) {
			String message = "[警告] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " が実行できませんでした";
			if (result.hasMessage()) {
				System.out.println(message + "：" + result.message);
			} else {
				System.out.println(message);
			}
		}

		return (Boolean) result.value;
	}

	/**
	 * Scopeから指定したキーにひもづくオブジェクトを取得する。
	 *
	 * キーのフォーマットは「アトリビュート名.プロパティA.プロパティB.プロパティC」
	 * プロパティが配列やCollectionの場合、要素番号を指定することができる。
	 *
	 * 例）customer.Address[1].Postcode.FirstCode
	 *
	 * @param scope
	 * @param key
	 * @return Scopeに存在する引数のキーに紐づくオブジェクト
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
	 * Attribute名とプロパティ名を指定して、Scopeからオブジェクトを取得する。
	 *
	 * @param scope
	 * @param attributeName
	 * @param propertyName
	 * @return
	 */
	private static Object extract(IScope scope, String attributeName, String propertyName) {
		//TODO attributeNameにインデックス指定がある場合、配列アクセスできるようにする

		Object obj = scope.getAttribute(attributeName);
		if (obj == null) {
			return null;
		}
		if (propertyName == null || propertyName.length() == 0) {
			return obj;
		}

		AccessResult result = PropertyAccessor.getNestedProperty(attributeName, obj, propertyName);

		if (!result.isSuccess()) {
			String message = "[警告] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " を取得できませんでした";
			if (result.hasMessage()) {
				System.out.println(message + "：" + result.message);
			} else {
				System.out.println(message);
			}
		}

		return result.value;
	}

	/**
	 * Attribute名とプロパティ名を指定して、Scopeからboolean値を取得する。
	 *
	 * @param scope
	 * @param attributeName
	 * @param propertyName
	 * @return
	 */
	private static boolean extractFlag(IScope scope, String attributeName, String propertyName) {
		//TODO attributeNameにインデックス指定がある場合、配列アクセスできるようにする
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
			String message = "[警告] " + attributeName + PropertyAccessor.ATTRIBUTE_SEPARATOR + propertyName + " を取得できませんでした";
			if (result.hasMessage()) {
				System.out.println(message + "：" + result.message);
			} else {
				System.out.println(message);
			}
			return false;
		}

		return (Boolean) result.value;
	}

}
