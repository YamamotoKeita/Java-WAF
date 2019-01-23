package jp.co.altonotes.webapp.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;



/**
 * プロパティのノード。抽象基底クラス。
 *  "."（ドット）で区切られたプロパティ名の中の、一区画を表す。
 * 
 * @author Yamamoto Keita
 */
abstract class PropertyNode {
	
	protected static final Object[] ARGS_NULL = {};

	/** ノードの階層 */
	protected int depth;

	/** プロパティキーの配列 */
	protected String[] keys;

	/** このノードの親ノードが表すオブジェクト */
	protected Object parentObj;
	
	/** このノードが表すオブジェクト */
	protected Object obj = Undefined.VALUE;

	private static final Class<?>[] PRIMITIVE_WRAPPER_LIST = {
		Integer.class,
		Boolean.class,
		Long.class,
		Double.class,
		Float.class,
		Character.class,
		Byte.class,
		Short.class
	};
	
	/**
	 * 末端ノードのオブジェクトを取得する
	 * @param result 
	 * @return 末端ノードのオブジェクト
	 */
	public Object getLastNodeObject(Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return null;
		}
		return lastNode.getObject(result);
	}

	/**
	 * 末端ノードの boolean 値を取得する
	 * @param result 
	 * @return 末端ノードの boolean 値
	 */
	public boolean getLastNodeBool(Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return false;
		}
		return lastNode.getBool(result);
	}

	/**
	 * 末端のノードに値をセットする
	 * @param value
	 * @param result
	 */
	public void setValueToLastNode(String value, Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return;
		}
		lastNode.setValue(value, result);
	}

	/**
	 * 末端のノードを取得する
	 * @param result
	 * @return 末端のノード
	 */
	public PropertyNode getLastNode(Result result) {
		return getDescendentNode(keys.length - 1, result);
	}

	/**
	 * このノードが表すオブジェクトを取得する
	 */
	final protected Object getObject(Result result) {
		if (obj == Undefined.VALUE) {
			obj = extractObject(result);
		}
		return obj;		
	}

	/**
	 * このノードが表すオブジェクトを、親ノードのオブジェクトから抽出する
	 * @param result
	 * @return
	 */
	protected abstract Object extractObject(Result result);

	/**
	 * プロパテに値をセットする
	 * @param value
	 * @param result
	 */
	protected abstract void setValue(String value, Result result);

	/**
	 * プロパテに配列値をセットする
	 * @param value
	 * @param result
	 */
	protected abstract void setValueArray(String[] value, Result result);

	/**
	 * @param result
	 * @return
	 */
	protected abstract boolean getBool(Result result);

	/**
	 * 階層を指定して子孫ノードを取得する
	 * @param depth
	 * @param result
	 * @return 指定した階層の子孫ノード
	 */
	protected PropertyNode getDescendentNode(int depth, Result result) {
		// このノードを取得する場合
		if (this.depth == depth) {
			return this;
		} 
		// 子孫ノードを取得する場合
		else {
			PropertyNode childNode = childNode(result);
			if (result.isFailed) {
				return null;
			}
			return childNode.getDescendentNode(depth, result);
		}
	}

	/**
	 * 直下の子ノードを取得する
	 * @param result
	 * @return 直下の子ノード
	 */
	protected PropertyNode childNode(Result result) {
		obj = getObject(result);
		if (result.isFailed) {
			return null;
		}
		return PropertyNodeFactory.createFromParent(obj, keys, depth + 1, result);
	}

	/**
	 * @return このノードを表すキー
	 */
	protected String getKey() {
		return keys[depth];
	}

	/**
	 * 親オブジェクトのゲッターを実行してオブジェクトを取得する
	 * 
	 * @param index
	 * @param result
	 * @return
	 */
	protected Object doGetter(String name, Result result) {
		Method getter = ObjectAccessor.getGetter(parentObj, name, false);
		if (getter == null) {
			result.fail(null);
			return Undefined.VALUE;
		}
		
		Object obj = null;
		try {
			obj = getter.invoke(parentObj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // 引数チェックをしているのであり得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) { // public しか取得していないのであり得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(methodToString(getter) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
		}

		return obj;
	}

	/**
	 * 親オブジェクトのメンバー変数を取得する
	 * @param result
	 * @return
	 */
	protected Object getField(String name, Result result) {
		Field field = null;

		try {
			field = parentObj.getClass().getField(name);
		} catch (SecurityException e) {
			result.fail(null);
			return Undefined.VALUE;
		} catch (NoSuchFieldException e) {
			result.fail(null);
			return Undefined.VALUE;
		}

		try {
			return field.get(parentObj);
		} catch (IllegalArgumentException e) { // あり得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			result.fail(null);
			return Undefined.VALUE;
		}
	}

	// ユーティリティー -----------------------------------------
	/**
	 * 文字列を各基本データ型に変換する
	 *
	 * @param str
	 * @param type
	 * @return
	 */
	protected static Object toPrimitive(String str, Class<?> type, Result result) {

		try {
			if (type.equals(int.class) || Integer.class.isAssignableFrom(type)) {
				return Integer.parseInt(str);
			} else if (type.equals(boolean.class) || Boolean.class.isAssignableFrom(type)) {
				Object value = Boolean.parseBoolean(str);
				if (!(Boolean)value && !str.equalsIgnoreCase("false") && 0 < str.trim().length()) {
					result.fail("大文字小文字の \"true\" \"false\" または空文字、半角スペースのみ変換できます。");
					return null;
				}
				return value;
			} else if (type.equals(long.class) || Long.class.isAssignableFrom(type)) {
				return Long.parseLong(str);
			} else if (type.equals(double.class) || Double.class.isAssignableFrom(type)) {
				return Double.parseDouble(str);
			} else if (type.equals(float.class) || Float.class.isAssignableFrom(type)) {
				return Float.parseFloat(str);
			} else if (type.equals(char.class) || Character.class.isAssignableFrom(type)) {
				if (str.length() == 1) {
					return str.charAt(0);
				} else {
					result.fail("長さ 1 の文字列のみ変換できます。");
					return null;
				}
			} else if (type.equals(byte.class) || Byte.class.isAssignableFrom(type)) {
				return Byte.parseByte(str);
			} else if (type.equals(short.class) || Short.class.isAssignableFrom(type)) {
				return Short.parseShort(str);
			} else {
				throw new FrameworkBugException("Primitive 以外の型が指定されました。");
			}
		} catch (NumberFormatException e) {
			result.fail(null);
			return null;
		}
	}
	
	/**
	 * メソッド名を文字列にする
	 * @param method
	 * @return
	 */
	protected static String methodToString(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName() + "(");

		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(types[i].getSimpleName());
		}
		sb.append(")");
		return sb.toString();
	}

	protected static String methodToStringWithoutArgs(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName());
		return sb.toString();
	}
	
	/**
	 * 引数の型が Primitive ラッパー型か判定する
	 * @param type
	 * @return
	 */
	protected boolean isPrimitiveWrapper(Class<?> type) {
		for (Class<?> target : PRIMITIVE_WRAPPER_LIST) {
			if (target.equals(type)) {
				return true;
			}
		}
		return false;
	}

}
