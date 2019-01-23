package jp.co.altonotes.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jp.co.altonotes.util.Checker;

/**
 * �N���X�̃��\�b�h�����s����B
 * private���\�b�h�̃e�X�g�Ɏg�p����B
 *
 * @author Yamamoto Keita
 *
 */
public class MethodWrapper {
	private Class<?> klass;
	private Object obj;
	private Method method;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param obj
	 * @param name
	 */
	public MethodWrapper(Object obj, String name) {
		if (obj == null) {
			throw new IllegalArgumentException("�����̃I�u�W�F�N�g��null�ł�");
		}

		if (Checker.isEmpty(name)) {
			throw new IllegalArgumentException("���\�b�h����null�܂��͋󕶎��ł�");
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
		throw new IllegalArgumentException(klass.getName() + "#" + name + " ���\�b�h��������܂���B");
	}

	/**
	 * �R���X�g���N�^�[�B
	 * @param klass 
	 *
	 * @param name
	 */
	public MethodWrapper(Class<?> klass, String name) {
		if (klass == null) {
			throw new IllegalArgumentException("�N���X��null�ł�");
		}

		if (Checker.isEmpty(name)) {
			throw new IllegalArgumentException("���\�b�h����null�܂��͋󕶎��ł�");
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
		throw new IllegalArgumentException(klass.getName() + "#" + name + " static���\�b�h��������܂���B");

	}

	/**
	 * �w�肵�������Ń��\�b�h�����s����B
	 *
	 * @param args
	 * @return ���s�������\�b�h�̖߂�l
	 */
	public Object args(Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(obj, args);
		} catch (IllegalArgumentException e) {
			//TODO �G���[�������l����
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//TODO �G���[�������l����
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			//TODO �G���[�������l����
			e.printStackTrace();
		}

		return null;
	}

}
