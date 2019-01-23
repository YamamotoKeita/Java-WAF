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
 * Gateway���\�b�h�̎��s���s���B
 *�@�G���[�n���h���[�A�t�B���^�[�������܂ށB
 *
 * @author Yamamoto Keita
 *
 */
public class GatewayInvoker implements IRequestHandler {

	/** �����Ȃ����\�b�h�p�̈���	*/
	private static final Object[] VOID_ARGUMENT = new Object[]{};

	/** ���\�b�h�����s����I�u�W�F�N�g	*/
	private Class<?> gatewayClass;

	/** ���s���郁�\�b�h	*/
	private Method method;
	
	/** Gateway�ւ̊��荞�ݏ������s�����W���[��	*/
	private GatewayInterceptor interceptor;
	
	/** GatewayInterceptor �� around���\�b�h�p�Ƀ��\�b�h���s�����b�v�����I�u�W�F�N�g */
	private GatewayInvocation invocation;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param obj
	 * @param method
	 */
	public GatewayInvoker(Class<?> gatewayClass, Method method) {
		this.gatewayClass = gatewayClass;
		this.method = method;
	}

	/**
	 * �R���X�g���N�^�[
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
	 * ���\�b�h�����s����B
	 *
	 * @return ���\�b�h��String�̖߂�l
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
			throw new IllegalStateException("Gateway��invoke�ŗ\�����ʃG���[���������܂����B").initCause(e);//�N����Ȃ��͂�
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Gateway��invoke�ŗ\�����ʃG���[���������܂����B").initCause(e);//�N����Ȃ��͂�
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
	 * StackTraceElement�̔z�񂩂�AGateway���\�b�h���s�ȑO�̃X�^�b�N�g���[�X���폜����B
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

		// �����珇�ԂɃX�^�b�N�g���[�X�����Ă���
		for (int i = stacks.length - 1; 0 <= i ; i--) {
			StackTraceElement stack = stacks[i];

			// ���t���N�V������ivoke�ɓ���O
			if (state == START) {
				if (stack.getClassName().startsWith("sun.reflect.") && stack.getMethodName().startsWith("invoke")) {
					state = INVOKING;
				}

			// ���t���N�V������ivoke��
			} else if (state == INVOKING) {
				if (!stack.getClassName().startsWith("sun.reflect.") || !stack.getMethodName().startsWith("invoke")) {
					state = AFTER_GATEWAY;
					traceList.add(stack);
				}

			// GateWay���\�b�h�ȍ~
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
	 * GatewayInvoker �̃��\�b�h���s�����b�v����B
	 * GatewayInterceptor �� around���\�b�h�p�B
	 * @author Yamamoto Keita
	 *
	 */
	public static class GatewayInvocation {
		
		private GatewayInvoker invoker;
		
		protected GatewayInvocation(GatewayInvoker invoker) {
			this.invoker = invoker;
		}
		
		/**
		 * @return ���s���ʂ̕Ԃ�l�i�J�ڐ�̃p�X�j
		 * @throws Throwable
		 */
		public String proceed() throws Throwable {
			return invoker.invoke();
		}
	}
	
	/**
	 * ���\�b�h���ɑΉ��������\�b�h���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return ���\�b�h���ɑΉ��������\�b�h
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
	 * �w�肵���N���X�̃C���X�^���X���擾����B
	 *
	 * @param element
	 * @return
	 */
	private static <T> T createObject(Class<T> klass) {
		//object�̍쐬
		T obj = null;
		try {
			obj = klass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace(); // �N�����Ɋm�F�ς݂Ȃ̂ŃG���[�ɂ͂Ȃ�Ȃ��͂�
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // �N�����Ɋm�F�ς݂Ȃ̂ŃG���[�ɂ͂Ȃ�Ȃ��͂�
		}

		return obj;
	}
}
