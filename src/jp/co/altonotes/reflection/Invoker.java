package jp.co.altonotes.reflection;


/**
 * クラスのメソッドを実行する。
 * privateメソッドのテストに使用できる。
 *
 * <pre>
 * ex.
 *
 * new Invoker(obj).method("getName");
 * </pre
 *
 * @author Yamamoto Keita
 *
 */
public class Invoker {
	private Object obj;

	/**
	 * コンストラクター
	 *
	 * @param obj
	 */
	public Invoker(Object obj) {
		this.obj = obj;
	}

	/**
	 * 引数に指定した名前のメソッドを取得する。
	 *
	 * @param name
	 * @return 引数に指定した名前のメソッドのラッパー
	 */
	public MethodWrapper method(String name) {
		return new MethodWrapper(obj, name);
	}

	/**
	 * staticメソッドのラッパーを取得する。
	 *
	 * @param klass
	 * @param name
	 * @return staticメソッドのラッパー
	 */
	public static MethodWrapper method(Class<?> klass, String name) {
		return new MethodWrapper(klass, name);
	}
}
