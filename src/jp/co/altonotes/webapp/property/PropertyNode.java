package jp.co.altonotes.webapp.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;



/**
 * �v���p�e�B�̃m�[�h�B���ۊ��N���X�B
 *  "."�i�h�b�g�j�ŋ�؂�ꂽ�v���p�e�B���̒��́A�����\���B
 * 
 * @author Yamamoto Keita
 */
abstract class PropertyNode {
	
	protected static final Object[] ARGS_NULL = {};

	/** �m�[�h�̊K�w */
	protected int depth;

	/** �v���p�e�B�L�[�̔z�� */
	protected String[] keys;

	/** ���̃m�[�h�̐e�m�[�h���\���I�u�W�F�N�g */
	protected Object parentObj;
	
	/** ���̃m�[�h���\���I�u�W�F�N�g */
	protected Object obj = Undefined.VALUE;

	private static final Class<?>[] PRIMITIVE_WRAPPER_LIST = {
		Integer.class,
		Boolean.class,
		Long.class,
		Double.class,
		Float.class,
		Character.class,
		Byte.class,
		Short.class
	};
	
	/**
	 * ���[�m�[�h�̃I�u�W�F�N�g���擾����
	 * @param result 
	 * @return ���[�m�[�h�̃I�u�W�F�N�g
	 */
	public Object getLastNodeObject(Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return null;
		}
		return lastNode.getObject(result);
	}

	/**
	 * ���[�m�[�h�� boolean �l���擾����
	 * @param result 
	 * @return ���[�m�[�h�� boolean �l
	 */
	public boolean getLastNodeBool(Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return false;
		}
		return lastNode.getBool(result);
	}

	/**
	 * ���[�̃m�[�h�ɒl���Z�b�g����
	 * @param value
	 * @param result
	 */
	public void setValueToLastNode(String value, Result result) {
		PropertyNode lastNode = getLastNode(result);
		if (result.isFailed) {
			return;
		}
		lastNode.setValue(value, result);
	}

	/**
	 * ���[�̃m�[�h���擾����
	 * @param result
	 * @return ���[�̃m�[�h
	 */
	public PropertyNode getLastNode(Result result) {
		return getDescendentNode(keys.length - 1, result);
	}

	/**
	 * ���̃m�[�h���\���I�u�W�F�N�g���擾����
	 */
	final protected Object getObject(Result result) {
		if (obj == Undefined.VALUE) {
			obj = extractObject(result);
		}
		return obj;		
	}

	/**
	 * ���̃m�[�h���\���I�u�W�F�N�g���A�e�m�[�h�̃I�u�W�F�N�g���璊�o����
	 * @param result
	 * @return
	 */
	protected abstract Object extractObject(Result result);

	/**
	 * �v���p�e�ɒl���Z�b�g����
	 * @param value
	 * @param result
	 */
	protected abstract void setValue(String value, Result result);

	/**
	 * �v���p�e�ɔz��l���Z�b�g����
	 * @param value
	 * @param result
	 */
	protected abstract void setValueArray(String[] value, Result result);

	/**
	 * @param result
	 * @return
	 */
	protected abstract boolean getBool(Result result);

	/**
	 * �K�w���w�肵�Ďq���m�[�h���擾����
	 * @param depth
	 * @param result
	 * @return �w�肵���K�w�̎q���m�[�h
	 */
	protected PropertyNode getDescendentNode(int depth, Result result) {
		// ���̃m�[�h���擾����ꍇ
		if (this.depth == depth) {
			return this;
		} 
		// �q���m�[�h���擾����ꍇ
		else {
			PropertyNode childNode = childNode(result);
			if (result.isFailed) {
				return null;
			}
			return childNode.getDescendentNode(depth, result);
		}
	}

	/**
	 * �����̎q�m�[�h���擾����
	 * @param result
	 * @return �����̎q�m�[�h
	 */
	protected PropertyNode childNode(Result result) {
		obj = getObject(result);
		if (result.isFailed) {
			return null;
		}
		return PropertyNodeFactory.createFromParent(obj, keys, depth + 1, result);
	}

	/**
	 * @return ���̃m�[�h��\���L�[
	 */
	protected String getKey() {
		return keys[depth];
	}

	/**
	 * �e�I�u�W�F�N�g�̃Q�b�^�[�����s���ăI�u�W�F�N�g���擾����
	 * 
	 * @param index
	 * @param result
	 * @return
	 */
	protected Object doGetter(String name, Result result) {
		Method getter = ObjectAccessor.getGetter(parentObj, name, false);
		if (getter == null) {
			result.fail(null);
			return Undefined.VALUE;
		}
		
		Object obj = null;
		try {
			obj = getter.invoke(parentObj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // �����`�F�b�N�����Ă���̂ł��蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) { // public �����擾���Ă��Ȃ��̂ł��蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(methodToString(getter) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		return obj;
	}

	/**
	 * �e�I�u�W�F�N�g�̃����o�[�ϐ����擾����
	 * @param result
	 * @return
	 */
	protected Object getField(String name, Result result) {
		Field field = null;

		try {
			field = parentObj.getClass().getField(name);
		} catch (SecurityException e) {
			result.fail(null);
			return Undefined.VALUE;
		} catch (NoSuchFieldException e) {
			result.fail(null);
			return Undefined.VALUE;
		}

		try {
			return field.get(parentObj);
		} catch (IllegalArgumentException e) { // ���蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			result.fail(null);
			return Undefined.VALUE;
		}
	}

	// ���[�e�B���e�B�[ -----------------------------------------
	/**
	 * ��������e��{�f�[�^�^�ɕϊ�����
	 *
	 * @param str
	 * @param type
	 * @return
	 */
	protected static Object toPrimitive(String str, Class<?> type, Result result) {

		try {
			if (type.equals(int.class) || Integer.class.isAssignableFrom(type)) {
				return Integer.parseInt(str);
			} else if (type.equals(boolean.class) || Boolean.class.isAssignableFrom(type)) {
				Object value = Boolean.parseBoolean(str);
				if (!(Boolean)value && !str.equalsIgnoreCase("false") && 0 < str.trim().length()) {
					result.fail("�啶���������� \"true\" \"false\" �܂��͋󕶎��A���p�X�y�[�X�̂ݕϊ��ł��܂��B");
					return null;
				}
				return value;
			} else if (type.equals(long.class) || Long.class.isAssignableFrom(type)) {
				return Long.parseLong(str);
			} else if (type.equals(double.class) || Double.class.isAssignableFrom(type)) {
				return Double.parseDouble(str);
			} else if (type.equals(float.class) || Float.class.isAssignableFrom(type)) {
				return Float.parseFloat(str);
			} else if (type.equals(char.class) || Character.class.isAssignableFrom(type)) {
				if (str.length() == 1) {
					return str.charAt(0);
				} else {
					result.fail("���� 1 �̕�����̂ݕϊ��ł��܂��B");
					return null;
				}
			} else if (type.equals(byte.class) || Byte.class.isAssignableFrom(type)) {
				return Byte.parseByte(str);
			} else if (type.equals(short.class) || Short.class.isAssignableFrom(type)) {
				return Short.parseShort(str);
			} else {
				throw new FrameworkBugException("Primitive �ȊO�̌^���w�肳��܂����B");
			}
		} catch (NumberFormatException e) {
			result.fail(null);
			return null;
		}
	}
	
	/**
	 * ���\�b�h���𕶎���ɂ���
	 * @param method
	 * @return
	 */
	protected static String methodToString(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName() + "(");

		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(types[i].getSimpleName());
		}
		sb.append(")");
		return sb.toString();
	}

	protected static String methodToStringWithoutArgs(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName());
		return sb.toString();
	}
	
	/**
	 * �����̌^�� Primitive ���b�p�[�^�����肷��
	 * @param type
	 * @return
	 */
	protected boolean isPrimitiveWrapper(Class<?> type) {
		for (Class<?> target : PRIMITIVE_WRAPPER_LIST) {
			if (target.equals(type)) {
				return true;
			}
		}
		return false;
	}

}
