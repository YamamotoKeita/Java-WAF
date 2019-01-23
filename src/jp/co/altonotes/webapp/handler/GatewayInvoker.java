package jp.co.altonotes.webapp.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.webapp.GatewayInterceptor;

/**
 * Gatewayメソッドの実行を行う。
 *　エラーハンドラー、フィルター処理も含む。
 *
 * @author Yamamoto Keita
 *
 */
public class GatewayInvoker implements IRequestHandler {

	/** 引数なしメソッド用の引数	*/
	private static final Object[] VOID_ARGUMENT = new Object[]{};

	/** メソッドを実行するオブジェクト	*/
	private Class<?> gatewayClass;

	/** 実行するメソッド	*/
	private Method method;
	
	/** Gatewayへの割り込み処理を行うモジュール	*/
	private GatewayInterceptor interceptor;
	
	/** GatewayInterceptor の aroundメソッド用にメソッド実行をラップしたオブジェクト */
	private GatewayInvocation invocation;

	/**
	 * コンストラクター
	 *
	 * @param obj
	 * @param method
	 */
	public GatewayInvoker(Class<?> gatewayClass, Method method) {
		this.gatewayClass = gatewayClass;
		this.method = method;
	}

	/**
	 * コンストラクター
	 * @param obj
	 * @param method
	 * @param interceptor
	 */
	public GatewayInvoker(Class<?> gatewayClass, Method method, GatewayInterceptor interceptor) {
		this.gatewayClass = gatewayClass;
		this.method = method;
		this.interceptor = interceptor;
		if (interceptor != null) {
			invocation = new GatewayInvocation(this);
		}
	}
	
	/**
	 * メソッドを実行する。
	 *
	 * @return メソッドのStringの戻り値
	 * @throws Throwable
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public String invoke() throws Throwable {
		Object returnValue = null;
		Object gateway = createObject(gatewayClass);
		try {
			returnValue =  method.invoke(gateway, VOID_ARGUMENT);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Gatewayのinvokeで予期せぬエラーが発生しました。").initCause(e);//起こらないはず
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Gatewayのinvokeで予期せぬエラーが発生しました。").initCause(e);//起こらないはず
		} catch (InvocationTargetException e) {
			Throwable th = e.getCause();
			StackTraceElement[] stacks = th.getStackTrace();
			stacks = cleanStackTrace(stacks);
			th.setStackTrace(stacks);
			throw th;
		}

		if (returnValue != null && returnValue instanceof String) {
			return (String) returnValue;
		}

		return null;
	}

	/**
	 * StackTraceElementの配列から、Gatewayメソッド実行以前のスタックトレースを削除する。
	 *
	 * @param stacks
	 * @return
	 */
	private static StackTraceElement[] cleanStackTrace(StackTraceElement[] stacks) {
		final int START = 0;
		final int INVOKING = 1;
		final int AFTER_GATEWAY = 2;

		List<StackTraceElement> traceList = new ArrayList<StackTraceElement>();

		int state = START;

		// 下から順番にスタックトレースを見ていく
		for (int i = stacks.length - 1; 0 <= i ; i--) {
			StackTraceElement stack = stacks[i];

			// リフレクションのivokeに入る前
			if (state == START) {
				if (stack.getClassName().startsWith("sun.reflect.") && stack.getMethodName().startsWith("invoke")) {
					state = INVOKING;
				}

			// リフレクションのivoke中
			} else if (state == INVOKING) {
				if (!stack.getClassName().startsWith("sun.reflect.") || !stack.getMethodName().startsWith("invoke")) {
					state = AFTER_GATEWAY;
					traceList.add(stack);
				}

			// GateWayメソッド以降
			} else if (state == AFTER_GATEWAY) {
				traceList.add(stack);
			}
		}

		Collections.reverse(traceList);

		return traceList.toArray(new StackTraceElement[traceList.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see jp.co.dcs.prosrv.jugyoin.webfw.handler.RequestHandler#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String process(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		if (interceptor == null) {
			return invoke();
		} else {
			interceptor.before();
			String retVal = interceptor.around(invocation);
			interceptor.after();
			return retVal;
		}
	}

	/**
	 * GatewayInvoker のメソッド実行をラップする。
	 * GatewayInterceptor の aroundメソッド用。
	 * @author Yamamoto Keita
	 *
	 */
	public static class GatewayInvocation {
		
		private GatewayInvoker invoker;
		
		protected GatewayInvocation(GatewayInvoker invoker) {
			this.invoker = invoker;
		}
		
		/**
		 * @return 実行結果の返り値（遷移先のパス）
		 * @throws Throwable
		 */
		public String proceed() throws Throwable {
			return invoker.invoke();
		}
	}
	
	/**
	 * メソッド名に対応したメソッドを取得する。
	 *
	 * @param obj
	 * @param name
	 * @return メソッド名に対応したメソッド
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Object obj, String name) throws NoSuchMethodException {
		if (name == null) {
			return null;
		} else {
			return obj.getClass().getMethod(name, new Class<?>[]{});
		}
	}
	
	/**
	 * 指定したクラスのインスタンスを取得する。
	 *
	 * @param element
	 * @return
	 */
	private static <T> T createObject(Class<T> klass) {
		//objectの作成
		T obj = null;
		try {
			obj = klass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace(); // 起動時に確認済みなのでエラーにはならないはず
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // 起動時に確認済みなのでエラーにはならないはず
		}

		return obj;
	}
}
