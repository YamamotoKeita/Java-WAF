package jp.co.altonotes.webapp.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jp.co.altonotes.util.exception.SetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;
import jp.co.altonotes.webapp.form.ISelectOption;

/**
 * 配列添え字を持たないプロパティのノード
 * @author Yamamoto Keita
 *
 */
class SingleNode extends PropertyNode {

	/**
	 * コンストラクター
	 */
	protected SingleNode() {
	}
	
	/**
	 * 単体テスト用コンストラクター
	 */
	protected SingleNode(Object parent, String name) {
		this.parentObj = parent;
		depth = 1;
		keys = new String[2];
		keys[0] = "parent";
		keys[1] = name;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#extractObject(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected Object extractObject(Result result) {
		Object obj = doGetter(getKey(), result);
		if (!result.isFailed) {
			return obj;
		}
		
		result.clear();
		String name = getKey();
		obj = getField(name, result);
		
		if (result.isFailed) {
			result.message = parentObj.getClass().getName() + " のプロパティ \"" + name + "\" が見つかりません。";
		}

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValue(java.lang.String, jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValue(String value, Result result) {
		// セッターによるセット
		doSetter(value, result);
		if (!result.isFailed) {
			return;
		}
		
		String setterErrorMsg = null;
		if (result.hasMessage()) {
			setterErrorMsg = result.message;
		}
		
		result.clear();
		
		// フィールドアクセスによるセット
		setField(value, result);
		if (result.isFailed) {
			if (setterErrorMsg != null ) {
				result.message = setterErrorMsg;
			} else if (result.message == null) {
				result.message = parentObj.getClass().getName() + " のプロパティ \"" + getKey()+ "\" が見つかりません。";
			}
		}
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValueArray(java.lang.String[], jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValueArray(String[] values, Result result) {
		// false 以外の値があればセットして終了
		for (String string : values) {
			if (!"false".equalsIgnoreCase(string)) {
				setValue(string, result);
				return;
			}
		}
		
		// false しかなければ false
		if (1 <= values.length) {
			setValue("false", result);
		}
	}
	
	/**
	 * オブジェクトの指定名のフィールドに値をセットする。
	 * @param value
	 * @param result
	 */
	private void setField(String value, Result result) {
		Field member = null;
		Object setObj = value;

		try {
			member = parentObj.getClass().getField(getKey());
		} catch (SecurityException e) {
			result.fail(null);
			return;
		} catch (NoSuchFieldException e) {
			result.fail(null);
			return;
		}

		Class<?> type = member.getType();

		if (!type.equals(String.class)) {
			// セットする値を型変換
			if (type.isPrimitive() || isPrimitiveWrapper(type)) {
				setObj = toPrimitive(value, type, result);
				if (result.isFailed) {
					String errorMessage = result.hasMessage() ? result.message : "";
					result.message = "型変換に失敗しました。" + "\"" + value + "\" を " + type.getName() + "型 に変換できません。" + errorMessage;
					return;
				}
			} else if (type.isEnum() && ISelectOption.class.isAssignableFrom(type)) {
				setObj = ObjectAccessor.getSelectOptionEnum(type, value);
			} else {
				result.fail(parentObj.getClass().getName() + " のプロパティ \"" + getKey() + "\" に値をセットできません。" + type.getName() + " は対応していない型です。");
				return;
			}
		}

		// フィールドに値をセット
		try {
			member.set(parentObj, setObj);
		} catch (IllegalArgumentException e) {// 有り得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			result.fail(null);
			return;
		}

		return;
	}

	/**
	 * セッターを実行する
	 * @param value 
	 * @param result
	 */
	private void doSetter(String value, Result result) {
		// セッターを取得する
		List<Method> setters = ObjectAccessor.getSetters(parentObj, getKey(), false);
		if (setters.size() == 0) {
			result.fail(null);
			return;
		}
		
		Method stringSetter = null;
		Method enumSetter = null;
		Method primitiveSetter = null;
		Method stringArraySetter = null;
		
		// String, enum, primitive
		for (Method method : setters) {
			Class<?> parameterType = method.getParameterTypes()[0];
			// String セッターの場合
			if (parameterType.equals(String.class)) {
				stringSetter = method;
			}
			// ISelectOption セッターの場合
			else if (parameterType.isEnum() && ISelectOption.class.isAssignableFrom(parameterType)) {
				if (enumSetter == null) {
					enumSetter = method;
				} else {
					throw new IllegalStateException("enum implements " + ISelectOption.class.getSimpleName() + " を引数にとる " +
							methodToStringWithoutArgs(method) + " が複数存在します。セッターを一意に特定できません。");
				}
			}
			// 基本データ型 セッターの場合
			else if (parameterType.isPrimitive() || isPrimitiveWrapper(parameterType)) {
				if (primitiveSetter == null) {
					primitiveSetter = method;
				} else {
					throw new IllegalStateException("基本データ型を引数にとる " + methodToStringWithoutArgs(method) +
							" が複数存在します。セッターを一意に特定できません。");
				}
			}
			// String 配列セッターの場合
			else if (parameterType.isArray() || parameterType.getComponentType().equals(String.class)) {
				stringArraySetter = method;
			}
		}
		
		// String セッターを実行
		if (stringSetter != null) {
			try {
				stringSetter.invoke(parentObj, value);
			} catch (IllegalArgumentException e) { // 引数チェックをしているのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public しか取得していないのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(stringSetter) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
			}
		}
		// プリミティブ セッターを実行
		else if (primitiveSetter != null) {
			try {
				Class<?> type = primitiveSetter.getParameterTypes()[0];
				Object primitiveValue = toPrimitive(value, type, result);
				if (result.isFailed) {
					String errorMessage = result.hasMessage() ? result.message : "";
					result.message = "型変換に失敗しました。" + "\"" + value + "\" を " + type.getName() + "型 に変換できません。" + errorMessage;
					return;
				}
				
				primitiveSetter.invoke(parentObj, primitiveValue);
			} catch (IllegalArgumentException e) { // 引数チェックをしているのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public しか取得していないのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(primitiveSetter) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
			}
			return;
		}
		// enum セッターを実行
		else if (enumSetter != null) {
			Class<?> enumType = enumSetter.getParameterTypes()[0];
			ISelectOption selectOptionEnum = ObjectAccessor.getSelectOptionEnum(enumType, value);
			if (selectOptionEnum == null) {
				result.fail(enumType.getName() + " に value=\"" + value + "\" の要素が存在しないため、値をセットできません。");
			}
			try {
				enumSetter.invoke(parentObj, selectOptionEnum);
			} catch (IllegalArgumentException e) { // 引数チェックをしているのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public しか取得していないのであり得ないはず
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(enumSetter) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
			}
			return;
		}
		else {
			result.fail(parentObj.getClass().getName() + " のプロパティ \"" + getKey() + "\" に値をセットできません。 setter の引数が対応していない型です。");
		}
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#getBool(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected boolean getBool(Result result) {
		// TODO Auto-generated method stub
		return false;
	}

}
