package jp.co.altonotes.webapp.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.webapp.exception.FrameworkBugException;
import jp.co.altonotes.webapp.form.ISelectOption;

/**
 * @author Yamamoto Keita
 *
 */
public class ObjectAccessor {
	
	/**
	 * オブジェクトの持つ、指定した Property のgetterを 取得する。
	 * @param obj
	 * @param name
	 * @param indexed
	 * @return ゲッター
	 */
	public static Method getGetter(Object obj, String name, boolean indexed) {
		
		Method[] methods = obj.getClass().getMethods();
		String getterName = toGetterName(name, "get");
		String isName = toGetterName(name, "is");
		
		Method isGetter = null;
		
		// 全 public メソッドから検索
		for (Method method : methods) {
			
			// 戻り値が void ならスキップ
			Class<?> returnType = method.getReturnType();
			if (returnType.equals(Void.class)) {
				continue;
			}
			
			// 引数がゲッター用じゃないものはスキップ
			Class<?>[] paramTypes = method.getParameterTypes();
			if (!indexed) {
				if (paramTypes.length != 0) continue;
			} else {
				if (paramTypes.length != 1 || !(paramTypes[0].equals(int.class) || paramTypes[0].equals(Integer.class))) continue;
			}
			
			// getXXX() 形式にマッチするなら終了
			if (getterName.equals(method.getName())) {
				return method;
			} 
			// isXXX() 形式にマッチするなら、他にgetXXX() が無いかを見る
			else if (isName.equals(method.getName())) {
				isGetter = method;
			}
		}
		
		return isGetter;
	}

	/**
	 * オブジェクトの持つ、指定した Property のsetterを全て取得する。
	 * 
	 * @param name
	 * @param indexed 
	 * @return セッターの配列
	 */
	static List<Method> getSetters(Object obj, String name, boolean indexed) {

		List<Method> list = new ArrayList<Method>();
		
		Method[] methods = obj.getClass().getMethods();
		name = toSetterName(name);
		for (Method method : methods) {
			if (!method.getName().equals(name)) {
				continue;
			}
			
			Class<?>[] paramTypes = method.getParameterTypes();
			
			// インデックス付きセッター
			if (indexed) {
				if (paramTypes.length == 2 && 
						(paramTypes[0].equals(int.class) || paramTypes[0].equals(Integer.class))) {
					list.add(method);
				}
			}
			// インデックスなしセッター
			else if (paramTypes.length == 1) {
				list.add(method);
			}
		}
		
		return list;
	}

	/**
	 * プロパティ名をセッター名に変換する。
	 *
	 * @param name
	 * @return
	 */
	static String toSetterName(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "set" + name;
		return name;
	}

	/**
	 * プロパティ名をゲッター名に変換する。
	 * @param name
	 * @param prefix
	 * @return ゲッター名
	 */
	static String toGetterName(String name, String prefix) {
		if (0 < name.length()) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = prefix + name;
		return name;
	}
	
	/**
	 * 引数の enum クラスにおいて、value に対応する SelectOption のインスタンスを取得する
	 * @param klass
	 * @param value
	 * @return value に対応する SelectOption のインスタンス
	 */
	static ISelectOption getSelectOptionEnum(Class<?> klass, String value) {
		Method valuesMethod = null;
		
		try {
			valuesMethod = klass.getMethod("values", new Class<?>[]{});
			ISelectOption[] values = (ISelectOption[]) valuesMethod.invoke(null, new Object[]{});
			for (ISelectOption selectOption : values) {
				if (selectOption.getValue().equals(value)) {
					return selectOption;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		}
		
		return null;
	}

}
