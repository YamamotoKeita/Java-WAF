package jp.co.altonotes.reflection;


/**
 * �N���X�̃��\�b�h�����s����B
 * private���\�b�h�̃e�X�g�Ɏg�p�ł���B
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
	 * �R���X�g���N�^�[
	 *
	 * @param obj
	 */
	public Invoker(Object obj) {
		this.obj = obj;
	}

	/**
	 * �����Ɏw�肵�����O�̃��\�b�h���擾����B
	 *
	 * @param name
	 * @return �����Ɏw�肵�����O�̃��\�b�h�̃��b�p�[
	 */
	public MethodWrapper method(String name) {
		return new MethodWrapper(obj, name);
	}

	/**
	 * static���\�b�h�̃��b�p�[���擾����B
	 *
	 * @param klass
	 * @param name
	 * @return static���\�b�h�̃��b�p�[
	 */
	public static MethodWrapper method(Class<?> klass, String name) {
		return new MethodWrapper(klass, name);
	}
}
