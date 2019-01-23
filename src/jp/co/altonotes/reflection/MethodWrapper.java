package jp.co.altonotes.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jp.co.altonotes.util.Checker;

/**
 * クラスのメソッドを実行する。
 * privateメソッドのテストに使用する。
 *
 * @author Yamamoto Keita
 *
 */
public class MethodWrapper {
	private Class<?> klass;
	private Object obj;
	private Method method;

	/**
	 * コンストラクター
	 *
	 * @param obj
	 * @param name
	 */
	public MethodWrapper(Object obj, String name) {
		if (obj == null) {
			throw new IllegalArgumentException("引数のオブジェクトがnullです");
		}

		if (Checker.isEmpty(name)) {
			throw new IllegalArgumentException("メソッド名がnullまたは空文字です");
		}

		this.obj = obj;
		this.klass = obj.getClass();

		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(name)) {
				this.method = method;
				return;
			}
		}

		methods = klass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(name)) {
				this.method = method;
				return;
			}
		}
		throw new IllegalArgumentException(klass.getName() + "#" + name + " メソッドが見つかりません。");
	}

	/**
	 * コンストラクター。
	 * @param klass 
	 *
	 * @param name
	 */
	public MethodWrapper(Class<?> klass, String name) {
		if (klass == null) {
			throw new IllegalArgumentException("クラスがnullです");
		}

		if (Checker.isEmpty(name)) {
			throw new IllegalArgumentException("メソッド名がnullまたは空文字です");
		}
		this.klass = klass;
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
				this.method = method;
				return;
			}
		}

		methods = klass.getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
				this.method = method;
				return;
			}
		}
		throw new IllegalArgumentException(klass.getName() + "#" + name + " staticメソッドが見つかりません。");

	}

	/**
	 * 指定した引数でメソッドを実行する。
	 *
	 * @param args
	 * @return 実行したメソッドの戻り値
	 */
	public Object args(Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(obj, args);
		} catch (IllegalArgumentException e) {
			//TODO エラー処理を考える
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//TODO エラー処理を考える
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			//TODO エラー処理を考える
			e.printStackTrace();
		}

		return null;
	}

}
