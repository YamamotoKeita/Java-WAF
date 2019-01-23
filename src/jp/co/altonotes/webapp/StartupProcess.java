package jp.co.altonotes.webapp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.altonotes.webapp.exception.StartupException;

/**
 * 起動時の処理
 *
 * @author Yamamoto Keita
 *
 */
public final class StartupProcess {

	private Object obj;
	private Method method;
	private Method destroyMethod;

	/**
	 * コンストラクター
	 *
	 * @param obj
	 * @param method
	 * @param destroyMethod
	 */
	protected StartupProcess(Object obj, Method method, Method destroyMethod) {
		this.obj = obj;
		this.method = method;
		this.destroyMethod = destroyMethod;
	}

	/**
	 * 処理を実行する。
	 *
	 * @throws StartupException
	 */
	protected void run() throws Throwable {
		try {
			method.invoke(obj);
		} catch (IllegalArgumentException e) {
			//TODO エラー処理を考える
			System.out.println(e);
		} catch (IllegalAccessException e) {
			//TODO エラー処理を考える
			System.out.println(e);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	/**
	 * 終了処理を実行する。
	 *
	 */
	protected void destroy() {
		if (destroyMethod != null) {
			try {
				destroyMethod.invoke(obj);
			} catch (IllegalArgumentException e) {//引数が異なる場合何もしない
				System.out.println(e);
			} catch (IllegalAccessException e) {//privateなら何もしない
				System.out.println(e);
			} catch (InvocationTargetException e) {
				throw (IllegalStateException) new IllegalStateException(obj.getClass() + "#destroy メソッドでエラーが発生しました。").initCause(e);
			}
		}
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[StartupProcess]" + obj.getClass() + "#" + method.getName();
	}
}
