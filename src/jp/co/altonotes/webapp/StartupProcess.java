package jp.co.altonotes.webapp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.altonotes.webapp.exception.StartupException;

/**
 * �N�����̏���
 *
 * @author Yamamoto Keita
 *
 */
public final class StartupProcess {

	private Object obj;
	private Method method;
	private Method destroyMethod;

	/**
	 * �R���X�g���N�^�[
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
	 * ���������s����B
	 *
	 * @throws StartupException
	 */
	protected void run() throws Throwable {
		try {
			method.invoke(obj);
		} catch (IllegalArgumentException e) {
			//TODO �G���[�������l����
			System.out.println(e);
		} catch (IllegalAccessException e) {
			//TODO �G���[�������l����
			System.out.println(e);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	/**
	 * �I�����������s����B
	 *
	 */
	protected void destroy() {
		if (destroyMethod != null) {
			try {
				destroyMethod.invoke(obj);
			} catch (IllegalArgumentException e) {//�������قȂ�ꍇ�������Ȃ�
				System.out.println(e);
			} catch (IllegalAccessException e) {//private�Ȃ牽�����Ȃ�
				System.out.println(e);
			} catch (InvocationTargetException e) {
				throw (IllegalStateException) new IllegalStateException(obj.getClass() + "#destroy ���\�b�h�ŃG���[���������܂����B").initCause(e);
			}
		}
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[StartupProcess]" + obj.getClass() + "#" + method.getName();
	}
}
