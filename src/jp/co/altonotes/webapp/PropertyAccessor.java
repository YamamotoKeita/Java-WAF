package jp.co.altonotes.webapp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.util.exception.AmbiguousSetterException;
import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.util.exception.SetterException;

/**
 * オブジェクトのプロパティを取得または設定する。
 * プロパティとみなすものは、getter、setter、publicメンバー。
 *
 * 配列の場合、getXXX(int), setXXX(int, Object)形式のgetter、setterを使用するか、
 * publicメンバーの配列への直接アクセスを行う。
 *
 * @author Yamamoto Keita
 *
 */
public class PropertyAccessor {

	/** プロパティの区切り記号 */
	public static final String ATTRIBUTE_SEPARATOR = ".";

	private static final Class<?>[] TYPES_NULL = {};
	private static final Class<?>[] TYPES_INT = {int.class};
	private static final Object[] ARGS_NULL = {};

	private static final Class<?>[][] BASIC_TYPES_LIST = {
															{int.class},
															{boolean.class},
															{long.class},
															{double.class},
															{float.class},
															{char.class},
															{byte.class},
															{short.class},
															{Integer.class},
															{Boolean.class},
															{Long.class},
															{Double.class},
															{Float.class},
															{Character.class},
															{Byte.class},
															{Short.class}
														 };

	private static final Class<?>[][] BASIC_TYPES_WITH_INDEX_LIST = {
															{int.class, int.class},
															{int.class, boolean.class},
															{int.class, long.class},
															{int.class, double.class},
															{int.class, float.class},
															{int.class, char.class},
															{int.class, byte.class},
															{int.class, short.class},
															{int.class, Integer.class},
															{int.class, Boolean.class},
															{int.class, Long.class},
															{int.class, Double.class},
															{int.class, Float.class},
															{int.class, Character.class},
															{int.class, Byte.class},
															{int.class, Short.class}
														};

	private static final Class<?>[] FORBIDDEN_CLASSES = {ClassLoader.class, Class.class};

	/**
	 * オブジェクトからネストしたプロパティ値を取得する
	 * @param obj
	 * @param nestedProperty
	 * @return 実行結果
	 */
	public static AccessResult getNestedProperty(Object obj, String nestedProperty) {
		return getNestedProperty(null, obj, nestedProperty);
	}

	/**
	 * オブジェクトからネストしたプロパティ値を取得する
	 * @param rootName
	 *
	 * @param obj
	 * @param nestedProperty
	 * @return 実行結果
	 */
	public static AccessResult getNestedProperty(String rootName, Object obj, String nestedProperty) {

		String[] properties = nestedProperty.split("\\.", -1);
		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				sb.append(ATTRIBUTE_SEPARATOR + properties[i]);
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null && i < properties.length - 1) {
				result.message = combine(rootName, properties, i) + " が null です。";
				result.code = AccessResult.NOT_FOUND;
				return result;
			} else {
				obj = result.value;
			}
		}

		return result;
	}

	/**
	 * オブジェクトからインデックス指定可能キーによりboolean値を取得する。
	 *
	 * @param rootName
	 * @param obj
	 * @param nestedProperty
	 * @return 実行結果
	 * @throws PropertyNotFoundException
	 */
	public static AccessResult getNestedFlag(String rootName, Object obj, String nestedProperty) {
		String[] properties = nestedProperty.split("\\.", -1);

		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length - 1; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				sb.append(ATTRIBUTE_SEPARATOR + properties[i]);
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null) {
				result.message = combine(rootName, properties, i) + " が null です。";
				return result;
			} else {
				obj = result.value;
			}
		}

		String property = properties[properties.length - 1];
		int idx = parseArrayIndex(property);
		if (idx != -1) {
			String name = property.substring(0, property.indexOf("["));
			sb.append(name);
			return getFlagProperty(sb.toString(), obj, name, idx);
		} else {
			return getFlagProperty(obj, property);
		}
	}

	/**
	 * boolean値を返すメソッドを実行する。
	 *
	 * @param rootName
	 * @param obj
	 * @param nestedProperty
	 * @return 実行結果
	 */
	public static AccessResult doNestedCheckMethod(String rootName, Object obj, String nestedProperty) {
		String[] properties = nestedProperty.split("\\.", -1);

		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length - 1; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null) {
				result.message = combine(rootName, properties, i) + " が null です。";
				return result;
			} else {
				obj = result.value;
			}
		}

		return doCheckMethod(obj, properties[properties.length - 1]);
	}

	/**
	 * オブジェクトからプロパティ値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getProperty(Object obj, String name) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doGetter(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result = getField(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result.code = AccessResult.NOT_FOUND;
		result.message = klass.getName() + " のプロパティ \"" + name+ "\" が見つかりません。";

		return result;
	}

	/**
	 * オブジェクトから要素のインデックスを指定してプロパティ値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @param idx
	 * @return
	 */
	private static AccessResult getProperty(String fullName, Object obj, String name, int idx) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doGetter(obj, name, idx);
		if (result.isSuccess()) {
			return result;
		}

		result = doGetter(obj, name);
		if (!result.isSuccess()) {
			result = getField(obj, name);
		}

		if (!result.isSuccess()) {
			result.code = AccessResult.NOT_FOUND;
			result.message = klass.getName() + " のプロパティ \"" + name+ "\" が見つかりません。";
			return result;
		} else if (result.value == null) {
			result.code = AccessResult.NOT_FOUND;
			result.message = fullName + " が null です。";
			return result;
		}

		result = getElementAt(result.value, idx);

		if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
			result.message = fullName + " にインデックス " + idx + " の要素がありません。";
		} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
			result.message = fullName + " はインデックス指定による要素のアクセスに対応していません。";
		}

		return result;
	}

	/**
	 * オブジェクトからbooleanおよびBooleanのプロパティ値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getFlagProperty(Object obj, String name) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doFlagGetter(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result = getField(obj, name);
		if (result.isSuccess() && result.value instanceof Boolean) {
			return result;
		}

		result.code = AccessResult.NOT_FOUND;
		result.message = klass.getName() + " のプロパティ \"" + name+ "\" が見つかりません。";

		return result;
	}

	/**
	 * オブジェクトから要素のインデックスを指定してプロパティ値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @param idx
	 * @return
	 */
	private static AccessResult getFlagProperty(String fullName, Object obj, String name, int idx) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doFlagGetter(obj, name, idx);
		if (result.isSuccess()) {
			return result;
		}

		result = doGetter(obj, name);
		if (!result.isSuccess()) {
			result = getField(obj, name);
		}

		if (!result.isSuccess()) {
			result.code = AccessResult.NOT_FOUND;
			result.message = klass.getName() + " のプロパティ \"" + name+ "\" が見つかりません。";
			return result;
		}

		result = getElementAt(result.value, idx);

		if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
			result.message = fullName + " にインデックス " + idx + " の要素がありません。";
		} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
			result.message = fullName + " はインデックス指定による要素のアクセスに対応していません。";
		}

		return result;
	}

	/**
	 * オブジェクトからゲッターにより値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doGetter(Object obj, String name) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		name = toGetter(name);

		try {
			method = klass.getMethod(name, TYPES_NULL);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // getMethodでチェックされるので有り得ないはず
			throw e;
		} catch (IllegalAccessException e) { //getMethodでチェックされるのでおそらく有り得ない
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * オブジェクトからインデックス指定ありのゲッターにより値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doGetter(Object obj, String name, int idx) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		name = toGetter(name);

		try {
			method = klass.getMethod(name, TYPES_INT);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, idx);
		} catch (IllegalArgumentException e) { // getMethodでチェックされるので有り得ないはず
			throw e;
		} catch (IllegalAccessException e) { //getMethodでチェックされるのでおそらく有り得ない
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * オブジェクトからゲッターにより値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doFlagGetter(Object obj, String name) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		String isMethodName = toFlagGetter(name);

		try {
			method = klass.getMethod(isMethodName, TYPES_NULL);
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		if (method == null) {
			String getterName = toGetter(name);
			try {
				method = klass.getMethod(getterName, TYPES_NULL);
			} catch (SecurityException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} catch (NoSuchMethodException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		}

		try {
			result.value = method.invoke(obj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // getMethodでチェックされるので有り得ないはず
			throw e;
		} catch (IllegalAccessException e) { //getMethodでチェックされるのでおそらく有り得ない
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}

		if (!(result.value instanceof Boolean)) {
			result.value = null;
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * オブジェクトからインデックス指定ありのゲッターにより値を取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doFlagGetter(Object obj, String name, int idx) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		String isMethodName = toFlagGetter(name);

		try {
			method = klass.getMethod(isMethodName, TYPES_INT);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		if (method == null) {
			String getterName = toGetter(name);
			try {
				method = klass.getMethod(getterName, TYPES_INT);
			} catch (SecurityException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} catch (NoSuchMethodException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		}

		try {
			result.value = method.invoke(obj, idx);
		} catch (IllegalArgumentException e) { // getMethodでチェックされるので有り得ないはず
			throw e;
		} catch (IllegalAccessException e) { //getMethodでチェックされるのでおそらく有り得ない
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}

		if (!(result.value instanceof Boolean)) {
			result.value = null;
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}
	/**
	 * オブジェクトのフィールド値を直接取得する。
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getField(Object obj, String name) {
		AccessResult result = new AccessResult();
		Field field = null;

		try {
			field = obj.getClass().getField(name);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchFieldException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = field.get(obj);
		} catch (IllegalArgumentException e) {
			throw e; // 有り得ないはず
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * 配列またはコレクションから、指定インデックスの要素を取り出す。
	 *
	 * @param obj
	 * @param idx
	 * @return
	 */
	private static AccessResult getElementAt(Object obj, int idx) {
		AccessResult result = new AccessResult();

		Object[] array = null;

		if (obj.getClass().isArray()) {
			// 配列の場合
			array = (Object[]) obj;
		} else if (obj instanceof Collection<?>) {
			// Collectionの場合
			array = ((Collection<?>) obj).toArray();
		} else {
			result.code = AccessResult.INDEX_IS_NOT_AVAILABLE;
			return result;
		}

		if (idx < 0 || array.length <= idx) {
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		result.value = array[idx];
		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * @param obj
	 * @param nameChain
	 * @param value
	 * @return 実行結果
	 */
	public static AccessResult setNestedProperty(Object obj, String nameChain, Object value) {
		return setNestedProperty(null, obj, nameChain, value);
	}

	/**
	 * オブジェクトにインデックス指定可能キーにより値をセットする。
	 *
	 * @param rootName
	 * @param obj
	 * @param nameChain
	 * @param value
	 * @return 実行結果
	 */
	public static AccessResult setNestedProperty(String rootName, Object obj, String nameChain, Object value) {
		String[] keyArray = nameChain.split("\\.", -1);
		Object target = obj;
		AccessResult result;

		if (keyArray.length > 1) {
			String key = nameChain.substring(0, nameChain.lastIndexOf('.'));
			result = getNestedProperty(rootName, obj, key);
			if (!result.isSuccess()) {
				return result;
			} else {
				target = result.value;
			}
		}

		String name = keyArray[keyArray.length - 1];
		int idx = TextUtils.parseArrayIndex(name);

		if (idx != -1) {
			String fieldName = name.substring(0, name.indexOf("["));
			result = setProperty(target, fieldName, value, idx);

			if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " にインデックス " + idx + " の要素がありません。";
			} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " はインデックス指定による要素のアクセスに対応していません。";
			} else if (result.code == AccessResult.ARRAY_IS_NULL) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " が null です。";
			}
		} else {
			result = setProperty(target, name, value);
		}

		return result;
	}

	/**
	 * オブジェクトの指定したプロパティにString値をセットする。
	 * 最初にキーを元にセッターを呼びだし、セッターで値が取得できない場合、直接キー名のフィールドを参照する。
	 *
	 * @param obj
	 * @param chainedKey
	 * @param value
	 * @throws AmbiguousSetterException
	 */
	private static AccessResult setProperty(Object obj, String name, Object value) throws AmbiguousSetterException {
		AccessResult result = doSetter(obj, name, value);
		if (result.isSuccess() || result.code != AccessResult.NOT_FOUND) {
			return result;
		}

		result = setField(obj, name, value);

		if (result.code == AccessResult.NOT_FOUND) {
			result.message = obj.getClass().getName() + " のプロパティ \"" + name+ "\" が見つかりません。";
		}

		return result;
	}

	private static AccessResult setProperty(Object obj, String name, Object value, int idx) throws AmbiguousSetterException {
		AccessResult result = doSetter(obj, name, idx, value);
		if (result.isSuccess() || result.code != AccessResult.NOT_FOUND) {
			return result;
		}

		result = getProperty(obj, name);
		if (!result.isSuccess()) {
			return result;
		} else if (result.value == null) {
			result.code = AccessResult.ARRAY_IS_NULL;
			return result;
		}

		result = setElementAt(result.value, idx, value);

		if (result.code == AccessResult.NOT_FOUND) {
			result.message = obj.getClass().getName() + " のプロパティ \"" + name+ "\" が見つかりません。";
		}

		return result;
	}

	/**
	 * 配列またはコレクションの指定インデックス要素に値をセットする。
	 *
	 * @param obj
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static AccessResult setElementAt(Object obj, int index, Object value) throws NumberFormatException {

		if (index < 0) {
			AccessResult result = new AccessResult();
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		if (obj.getClass().isArray()) {
			// 配列の場合
			return setElementToArray(obj, index, value);
		} else if (obj instanceof Collection<?>) {
			// Collectionの場合
			Collection<Object> collection = (Collection<Object>) obj;
			return setElementToCollection(collection, index, value);
		} else {
			// その他
			AccessResult result = new AccessResult();
			result.code = AccessResult.INDEX_IS_NOT_AVAILABLE;
			return result;
		}
	}

	/**
	 * Collectionの指定要素に値をセットする。
	 *
	 * @param collection
	 * @param index
	 * @param value
	 * @return
	 */
	private static AccessResult setElementToCollection(Collection<Object> collection, int index, Object value) {
		AccessResult result = new AccessResult();

		if (collection.size() <= index) {
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		Object[] temp = collection.toArray();
		Class<?> componentType = temp[0].getClass(); // sizeチェックをしてるので[0]は必ずある

		if (componentType.isInstance(value)) {
			collection.clear();

			for (int i = 0; i < temp.length; i++) {
				if (i == index) {
					collection.add(value);
				} else {
					collection.add(temp[i]);
				}
			}

			result.code = AccessResult.SUCCESS;
			return result;
		} else {
			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, componentType);

			if (!convertResult.isSuccess()) {
				if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
					result.code = AccessResult.NOT_FOUND;
					return result;
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + componentType.getName() + "型 に変換できません。";
					if (convertResult.hasMessage()) {
						result.message += convertResult.message;
					}
					return result;
				}
			}

			collection.clear();

			for (int i = 0; i < temp.length; i++) {
				if (i == index) {
					collection.add(convertResult.value);
				} else {
					collection.add(temp[i]);
				}
			}

			result.code = AccessResult.SUCCESS;
			return result;
		}
	}

	/**
	 * 配列の指定要素に値をセットする。
	 *
	 * @param obj
	 * @param index
	 * @param value
	 * @return
	 */
	private static AccessResult setElementToArray(Object obj, int index, Object value) {
		AccessResult result = new AccessResult();

		// 配列の場合
		Class<?> componentType = obj.getClass().getComponentType();


		if (!componentType.isPrimitive()) { // 非プリミティブ型
			Object[] array = (Object[]) obj;

			if (array.length <= index) {
				result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
				return result;
			}

			if (componentType.isInstance(value)) { // セットする値の型が配列と合っている場合
				array[index] = value;
			} else if (value instanceof String) {
				String valueStr = (String) value;
				AccessResult convertResult = convert(valueStr, componentType);

				if (convertResult.isSuccess()) {
					array[index] = convertResult.value;
				} else if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
					result.code = AccessResult.NOT_FOUND;
					return result;
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + componentType.getName() + "型 に変換できません。";
					if (convertResult.hasMessage()) {
						result.message += convertResult.message;
					}
					return result;
				}

			} else {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		} else if (value instanceof String) { // プリミティブ型かつsetする値がString
			if (primitiveArrayLength(obj, componentType) <= index) {
				result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
				return result;
			}

			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, componentType);

			if (convertResult.isSuccess()) {
				setElementToPrimitiveTypeArray(index, obj, convertResult.value, componentType);
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + componentType.getName() + "型 に変換できません。";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		} else {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}
		result.code = AccessResult.SUCCESS;
		return result ;
	}

	/**
	 * 指定名のセッターを実行する。
	 * valueの型を判定し、その型を引数に取るセッターを実行する。
	 * null値をセットすることはできない。
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 * @throws AmbiguousSetterException
	 */
	private static AccessResult doSetter(Object obj, String name, Object value) throws AmbiguousSetterException {
		//TODO nullだとsetterの引数の型がわかんねーから無理。なんとかなんねーかな。
		if (value == null) {
			throw new NullPointerException("nullをセットすることはできません");
		}

		AccessResult result = new AccessResult();
		Method method = null;
		Class<?> klass = obj.getClass();
		Object arg = value;

		name = toSetter(name);

		try {
			method = klass.getMethod(name, new Class<?>[]{value.getClass()});
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		// 基本データ型引数のsetter
		if (method == null && value instanceof String) {
			String valueStr = (String) value;

			Class<?> type = null;
			int hitCount = 0;

			// TODO ループでExceptionを発生させるのはパフォーマンスが良くない。全メソッドからサーチに変更するべき。
			// 各基本データ型のsetterを探す
			for (Class<?>[] types : BASIC_TYPES_LIST) {
				Method setterAsType = null;

				try {
					setterAsType = klass.getMethod(name, types);
				} catch (SecurityException e1) {
					setterAsType = null;
				} catch (NoSuchMethodException e1) {
					setterAsType = null;
				}

				if (setterAsType != null) {
					method = setterAsType;
					type = types[0];
					hitCount++;
				}

				if (1 < hitCount) {
					throw new AmbiguousSetterException("基本データ型を引数にとる " + klass.getName() + "#" + name + " が複数存在します。セッターを一意に特定できません");
				}
			}

			if (method == null) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}

			AccessResult convertResult = convert(valueStr, type);
			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + type.getName() + "型 に変換できません。";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		if (method == null) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			method.invoke(obj, arg);
		} catch (IllegalArgumentException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(toMessage(method) + " の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * 指定名のインデックス指定ありセッターを実行する。
	 * valueの型を判定し、その型を引数に取るセッターを実行する。
	 * null値をセットすることはできない。
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 * @throws PropertyNotFoundException
	 * @throws AmbiguousSetterException
	 * @throws PropertyFormatException
	 */
	private static AccessResult doSetter(Object obj, String name, int idx, Object value) {
		if (value == null) {
			throw new NullPointerException("nullをセットすることはできません");
		}

		AccessResult result = new AccessResult();
		Method method = null;
		Class<?> klass = obj.getClass();
		Object arg = value;

		name = toSetter(name);

		try {
			method = klass.getMethod(name, new Class<?>[]{int.class, value.getClass()});
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		// 基本データ型引数のsetter
		if (method == null && value instanceof String) {
			String valueStr = (String) value;

			Class<?> type = null;
			int hitCount = 0;

			// TODO ループでExceptionを発生させるのはパフォーマンスが良くない。全メソッドからサーチに変更するべき。
			// 各基本データ型のsetterを探す
			for (Class<?>[] types : BASIC_TYPES_WITH_INDEX_LIST) {
				Method setterAsType = null;
				try {
					setterAsType = klass.getMethod(name, types);
				} catch (SecurityException e1) {
					setterAsType = null;
				} catch (NoSuchMethodException e1) {
					setterAsType = null;
				}

				if (setterAsType != null) {
					method = setterAsType;
					type = types[1];
					hitCount++;
				}

				if (1 < hitCount) {
					throw new AmbiguousSetterException("基本データ型を引数にとる " + klass.getName() + "#" + name + " が複数存在します。セッターを一意に特定できません");
				}
			}

			if (method == null) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}

			AccessResult convertResult = convert(valueStr, type);
			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + type.getName() + "型 に変換できません。";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		if (method == null) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			method.invoke(obj, idx, arg);
		} catch (IllegalArgumentException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(toMessage(method) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * オブジェクトの指定名のフィールドに値をセットする。
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 */
	private static AccessResult setField(Object obj, String name, Object value) {
		Field member = null;
		Object arg = value;

		AccessResult result = new AccessResult();
		try {
			member = obj.getClass().getField(name);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchFieldException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		Class<?> type = member.getType();

		if (!type.isInstance(value) && value instanceof String) {
			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, type);

			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "型変換に失敗しました。" + "\"" + valueStr + "\" を " + type.getName() + "型 に変換できません。";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		try {
			member.set(obj, arg);
		} catch (IllegalArgumentException e) {// 有り得ないはず
			throw e;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * オブジェクトのbooleanを返すメソッドを実行する。
	 *
	 * @param obj
	 * @param methodName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private static AccessResult doCheckMethod(Object obj, String methodName) {
		AccessResult result = new AccessResult();
		Method method = null;

		try {
			method = obj.getClass().getMethod(methodName, new Class<?>[]{});
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, new Object[0]);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "\" の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
		} catch (IllegalArgumentException e) {
			throw e; // 有り得ないはず
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * プリミティブ型の配列の長さを取得する
	 *
	 * @param obj
	 * @param componentType
	 * @return
	 */
	private static int primitiveArrayLength(Object obj, Class<?> type) {
		if (type.equals(int.class)) {
			int[] array = (int[]) obj;
			return array.length;
		} else if (type.equals(boolean.class)) {
			boolean[] array = (boolean[]) obj;
			return array.length;
		} else if (type.equals(long.class)) {
			long[] array = (long[]) obj;
			return array.length;
		} else if (type.equals(double.class)) {
			double[] array = (double[]) obj;
			return array.length;
		} else if (type.equals(float.class)) {
			float[] array = (float[]) obj;
			return array.length;
		} else if (type.equals(char.class)) {
			char[] array = (char[]) obj;
			return array.length;
		} else if (type.equals(byte.class)) {
			byte[] array = (byte[]) obj;
			return array.length;
		} else if (type.equals(short.class)) {
			short[] array = (short[]) obj;
			return array.length;
		} else {
			throw new IllegalArgumentException(type + " はプリミティブ型ではありません。");
		}
	}

	/**
	 * プリミティブ型の配列要素にデータをセットする
	 *
	 * @param index
	 * @param obj
	 * @param arg
	 * @param componentType
	 */
	private static void setElementToPrimitiveTypeArray(int index, Object obj, Object arg, Class<?> type) {
		if (type.equals(int.class)) {
			int[] array = (int[]) obj;
			array[index] = (Integer) arg;
		} else if (type.equals(boolean.class)) {
			boolean[] array = (boolean[]) obj;
			array[index] = (Boolean) arg;
		} else if (type.equals(long.class)) {
			long[] array = (long[]) obj;
			array[index] = (Long) arg;
		} else if (type.equals(double.class)) {
			double[] array = (double[]) obj;
			array[index] = (Double) arg;
		} else if (type.equals(float.class)) {
			float[] array = (float[]) obj;
			array[index] = (Float) arg;
		} else if (type.equals(char.class)) {
			char[] array = (char[]) obj;
			array[index] = (Character) arg;
		} else if (type.equals(byte.class)) {
			byte[] array = (byte[]) obj;
			array[index] = (Byte) arg;
		} else if (type.equals(short.class)) {
			short[] array = (short[]) obj;
			array[index] = (Short) arg;
		} else {
			throw new IllegalStateException(type + " はプリミティブ型ではありません。");
		}
	}

	/**
	 * プロパティ名をゲッター名に変換する。
	 *
	 * @param name
	 * @return
	 */
	private static String toFlagGetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "is" + name;
		return name;
	}

	/**
	 * プロパティ名をゲッター名に変換する。
	 *
	 * @param name
	 * @return
	 */
	private static String toGetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "get" + name;
		return name;
	}

	/**
	 * プロパティ名をセッター名に変換する。
	 *
	 * @param name
	 * @return
	 */
	private static String toSetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "set" + name;
		return name;
	}

	/**
	 * MethodをExceptionメッセージに表記するための文字列にする。
	 *
	 * @param method
	 * @return
	 */
	private static String toMessage(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName() + "(");

		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(types[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 文字列を各基本データ型に変換する
	 *
	 * @param str
	 * @param type
	 * @return
	 */
	private static AccessResult convert(String str, Class<?> type) {
		AccessResult result = new AccessResult();
		result.code = AccessResult.SUCCESS;

		try {
			if (type.equals(int.class) || Integer.class.isAssignableFrom(type)) {
				result.value = Integer.parseInt(str);
			} else if (type.equals(boolean.class) || Boolean.class.isAssignableFrom(type)) {
				result.value = Boolean.parseBoolean(str);
				if (!(Boolean)result.value && !str.equalsIgnoreCase("false") && str.trim().length() > 0) {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "大文字小文字の \"true\" \"false\" または空文字、半角スペースのみ変換できます。";
				}
			} else if (type.equals(long.class) || Long.class.isAssignableFrom(type)) {
				result.value = Long.parseLong(str);
			} else if (type.equals(double.class) || Double.class.isAssignableFrom(type)) {
				result.value = Double.parseDouble(str);
			} else if (type.equals(float.class) || Float.class.isAssignableFrom(type)) {
				result.value = Float.parseFloat(str);
			} else if (type.equals(char.class) || Character.class.isAssignableFrom(type)) {
				if (str.length() == 1) {
					result.value = str.charAt(0);
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "長さ 1 の文字列のみ変換できます。";
				}
			} else if (type.equals(byte.class) || Byte.class.isAssignableFrom(type)) {
				result.value = Byte.parseByte(str);
			} else if (type.equals(short.class) || Short.class.isAssignableFrom(type)) {
				result.value = Short.parseShort(str);
			} else {
				result.code = AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE;
				return result;
			}
		} catch (NumberFormatException e) {
			result.code = AccessResult.INVALID_FORMAT;
		}

		return result;
	}

	/**
	 * 文字列の配列を指定したインデックスまでドット繋ぎで連結する。
	 *
	 * @param properties
	 * @param i
	 * @return
	 */
	private static String combine(String rootName, String[] properties, int i) {
		StringBuilder sb = new StringBuilder();

		if (rootName != null) {
			sb.append(rootName + ".");
		}

		for (int j = 0; j <= i; j++) {
			if (j != 0) {
				sb.append(".");
			}
			sb.append(properties[j]);
		}
		return sb.toString();
	}

	/**
	 * 文字列の配列を指定したインデックスまでドット繋ぎで連結する。
	 *
	 * @param properties
	 * @param i
	 * @return
	 */
	private static String combine(String rootName, String[] properties, int i, String lastName) {
		StringBuilder sb = new StringBuilder();

		if (rootName != null) {
			sb.append(rootName + ".");
		}

		for (int j = 0; j <= i; j++) {
			if (j != 0) {
				sb.append(".");
			}

			if (j == i) {
				sb.append(lastName);
			} else {
				sb.append(properties[j]);
			}
		}
		return sb.toString();
	}

	/**
	 * 文字列末尾にある、[]に囲まれた数値をintとして取得する。<br>
	 * 例）array[1]の場合、1が返る。<br>
	 * <br>
	 * 以下の場合は-1を返す。
	 * <ul>
	 * <li>末尾に[]が存在しない</li>
	 * <li>[]内を数値として認識できない</li>
	 * <li>[]内が負の数</li>
	 * </ul>
	 *
	 * @param str
	 * @return
	 */
	private static int parseArrayIndex(String str) {
		int aryIdx = -1;
		String idxStr = null;
		int head = 0;
		int tail = 0;

		head = str.indexOf("[");
		if (head == -1) {
			return -1;
		}

		tail = str.indexOf("]", head + 2);
		if (tail != str.length() - 1) {
			return -1;
		}

		idxStr = str.substring(head + 1, tail);
		if (Checker.isNumber(idxStr)) {
			aryIdx = Integer.parseInt(idxStr);
		} else {
			return -1;
		}

		if (aryIdx >= 0) {
			return aryIdx;
		} else {
			return -1;
		}
	}

	/**
	 * Propertyアクセスの結果を保持する。
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static class AccessResult {
		/** アクセス成功	*/
		public static final int SUCCESS = 1;
		/** 対象のプロパティが見つからない	*/
		public static final int NOT_FOUND = 2;
		/** 不正なフォーマット	*/
		public static final int INVALID_FORMAT = 3;
		/** インデックスが使用できないフィールドにインデックスが指定された	*/
		public static final int INDEX_IS_NOT_AVAILABLE = 4;
		/** 範囲外のインデックスが指定された	*/
		public static final int INDEX_OUT_OF_BOUNDS = 5;
		/** 型変換ができなかった	*/
		public static final int TYPE_CONVERSION_IS_NOT_AVAILABLE = 6;
		/** 配列要素がnull	*/
		public static final int ARRAY_IS_NULL = 7;

		/**
		 * アクセス結果オブジェクト
		 */
		public Object value;

		/**
		 * 結果コード
		 */
		public int code;

		/**
		 * メッセージ
		 */
		public String message;

		/**
		 * @return アクセス成功の場合<code>true</code>
		 */
		public boolean isSuccess() {
			if (code != SUCCESS) return false;

			if (value != null) {
				for (Class<?> klass : FORBIDDEN_CLASSES) {
					if (klass.isAssignableFrom(value.getClass())) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * @return メッセージがある場合<code>true</code>
		 */
		public boolean hasMessage() {
			return message != null;
		}

		@Override
		public String toString() {
			return "AccessResult [code=" + code + ", message=" + message
					+ ", value=" + value + "]";
		}
	}

}
