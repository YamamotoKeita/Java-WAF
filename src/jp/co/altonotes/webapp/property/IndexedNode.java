package jp.co.altonotes.webapp.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import jp.co.altonotes.util.exception.AmbiguousSetterException;
import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.util.exception.SetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;

/**
 * インデックス添字付きのノード
 * @author Yamamoto Keita
 *
 */
public class IndexedNode extends PropertyNode {
	
	private int index;
	private String name;

	/**
	 * コンストラクター
	 * @param index
	 */
	protected IndexedNode(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#extractObject(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected Object extractObject(Result result) {
		Class<?> klass = parentObj.getClass();
		Object resultObj = Undefined.VALUE;
		
		resultObj = doGetter(name, index, result);
		if (!result.isFailed) {
			return resultObj;
		}

		resultObj = doGetter(name, result);
		if (result.isFailed) {
			resultObj = getField(name, result);
		}

		String fullName = fullName();
		if (result.isFailed) {
			result.fail(klass.getName() + " のプロパティ \"" + name + "\" が見つかりません。");
			return Undefined.VALUE;
		} else if (resultObj == null) {
			result.fail(fullName + " が null です。");
			return Undefined.VALUE;
		}

		resultObj = getElementAt(resultObj, index, result);

		return null;
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#set(java.lang.String, jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValue(String value, Result result) {
		// TODO Auto-generated method stub
		
//		doSetter(obj, name, index, value);
//		// セッターがある場合はここで終了
//		if (!result.isFailed() || result.errorCode != Result.NOT_FOUND) {
//			return;
//		}
//
//		result = getProperty(obj, name);
//		if (!result.isSuccess()) {
//			return result;
//		} else if (result.value == null) {
//			result.code = AccessResult.ARRAY_IS_NULL;
//			return result;
//		}
//
//		result = setElementAt(result.value, idx, value);
//
//		if (result.code == AccessResult.NOT_FOUND) {
//			result.message = obj.getClass().getName() + " のプロパティ \"" + name+ "\" が見つかりません。";
//		}
//
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValueArray(java.lang.String[], jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValueArray(String[] value, Result result) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#getBool(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected boolean getBool(Result result) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 配列またはコレクションから、指定インデックスの要素を取り出す。
	 *
	 * @param obj
	 * @param idx
	 * @return
	 */
	private Object getElementAt(Object obj, int idx, Result result) {

		Object[] array = null;

		// 配列の場合
		if (obj.getClass().isArray()) {
			array = (Object[]) obj;
		}
		// Collectionの場合
		else if (obj instanceof Collection<?>) {
			array = ((Collection<?>) obj).toArray();
		}
		// その他
		else {
			result.message = fullName() + " にインデックス " + idx + " の要素がありません。";
			return Undefined.VALUE;
		}

		if (idx < 0 || array.length <= idx) {
			result.message = fullName() + " はインデックス指定による要素のアクセスに対応していません。";
			return Undefined.VALUE;
		}

		return array[idx];
	}
	
	/**
	 * インデックス付きのゲッターを実行してオブジェクトを取得する
	 * 
	 * @param index
	 * @param result
	 * @return
	 */
	protected Object doGetter(String name, int index, Result result) {
		Method getter = ObjectAccessor.getGetter(parentObj, name, true);
		if (getter == null) {
			result.fail(null);
			return Undefined.VALUE;
		}
		
		Object obj = null;
		try {
			obj = getter.invoke(parentObj, index);
		} catch (IllegalArgumentException e) { // 引数チェックをしているのであり得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) { // public しか取得していないのであり得ないはず
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(methodToString(getter) + " の実行時にエラーが発生しました：" + e.getCause()).initCause(e.getCause());
		}

		return obj;
	}

	private void doSetter(String name, String value, Result result) throws AmbiguousSetterException {

		Method method = null;
		Class<?> klass = parentObj.getClass();
		Object arg = value;

		List<Method> setters = ObjectAccessor.getSetters(parentObj, name, true);

		// 基本データ型引数のsetter
//		throw new AmbiguousSetterException("基本データ型を引数にとる " + klass.getName() + "#" + name + " が複数存在します。セッターを一意に特定できません");

		try {
			method.invoke(obj, arg);
		} catch (IllegalArgumentException e) {
			result.fail(null);
		} catch (IllegalAccessException e) {
			result.fail(null);
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(methodToString(method) + " の実行時に例外が発生しました：" + e.getCause()).initCause(e.getCause());
		}
	}

	/**
	 * @return 添字手前までのプロパティ名
	 */
	private String fullName() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			if (i != 0) {
				sb.append(".");
			}
			sb.append(keys[i]);
		}
		sb.append(name);
		return sb.toString();
	}

}
