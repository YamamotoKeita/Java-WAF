package jp.co.altonotes.webapp.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jp.co.altonotes.util.exception.SetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;
import jp.co.altonotes.webapp.form.ISelectOption;

/**
 * �z��Y�����������Ȃ��v���p�e�B�̃m�[�h
 * @author Yamamoto Keita
 *
 */
class SingleNode extends PropertyNode {

	/**
	 * �R���X�g���N�^�[
	 */
	protected SingleNode() {
	}
	
	/**
	 * �P�̃e�X�g�p�R���X�g���N�^�[
	 */
	protected SingleNode(Object parent, String name) {
		this.parentObj = parent;
		depth = 1;
		keys = new String[2];
		keys[0] = "parent";
		keys[1] = name;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#extractObject(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected Object extractObject(Result result) {
		Object obj = doGetter(getKey(), result);
		if (!result.isFailed) {
			return obj;
		}
		
		result.clear();
		String name = getKey();
		obj = getField(name, result);
		
		if (result.isFailed) {
			result.message = parentObj.getClass().getName() + " �̃v���p�e�B \"" + name + "\" ��������܂���B";
		}

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValue(java.lang.String, jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValue(String value, Result result) {
		// �Z�b�^�[�ɂ��Z�b�g
		doSetter(value, result);
		if (!result.isFailed) {
			return;
		}
		
		String setterErrorMsg = null;
		if (result.hasMessage()) {
			setterErrorMsg = result.message;
		}
		
		result.clear();
		
		// �t�B�[���h�A�N�Z�X�ɂ��Z�b�g
		setField(value, result);
		if (result.isFailed) {
			if (setterErrorMsg != null ) {
				result.message = setterErrorMsg;
			} else if (result.message == null) {
				result.message = parentObj.getClass().getName() + " �̃v���p�e�B \"" + getKey()+ "\" ��������܂���B";
			}
		}
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValueArray(java.lang.String[], jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValueArray(String[] values, Result result) {
		// false �ȊO�̒l������΃Z�b�g���ďI��
		for (String string : values) {
			if (!"false".equalsIgnoreCase(string)) {
				setValue(string, result);
				return;
			}
		}
		
		// false �����Ȃ���� false
		if (1 <= values.length) {
			setValue("false", result);
		}
	}
	
	/**
	 * �I�u�W�F�N�g�̎w�薼�̃t�B�[���h�ɒl���Z�b�g����B
	 * @param value
	 * @param result
	 */
	private void setField(String value, Result result) {
		Field member = null;
		Object setObj = value;

		try {
			member = parentObj.getClass().getField(getKey());
		} catch (SecurityException e) {
			result.fail(null);
			return;
		} catch (NoSuchFieldException e) {
			result.fail(null);
			return;
		}

		Class<?> type = member.getType();

		if (!type.equals(String.class)) {
			// �Z�b�g����l���^�ϊ�
			if (type.isPrimitive() || isPrimitiveWrapper(type)) {
				setObj = toPrimitive(value, type, result);
				if (result.isFailed) {
					String errorMessage = result.hasMessage() ? result.message : "";
					result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + value + "\" �� " + type.getName() + "�^ �ɕϊ��ł��܂���B" + errorMessage;
					return;
				}
			} else if (type.isEnum() && ISelectOption.class.isAssignableFrom(type)) {
				setObj = ObjectAccessor.getSelectOptionEnum(type, value);
			} else {
				result.fail(parentObj.getClass().getName() + " �̃v���p�e�B \"" + getKey() + "\" �ɒl���Z�b�g�ł��܂���B" + type.getName() + " �͑Ή����Ă��Ȃ��^�ł��B");
				return;
			}
		}

		// �t�B�[���h�ɒl���Z�b�g
		try {
			member.set(parentObj, setObj);
		} catch (IllegalArgumentException e) {// �L�蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			result.fail(null);
			return;
		}

		return;
	}

	/**
	 * �Z�b�^�[�����s����
	 * @param value 
	 * @param result
	 */
	private void doSetter(String value, Result result) {
		// �Z�b�^�[���擾����
		List<Method> setters = ObjectAccessor.getSetters(parentObj, getKey(), false);
		if (setters.size() == 0) {
			result.fail(null);
			return;
		}
		
		Method stringSetter = null;
		Method enumSetter = null;
		Method primitiveSetter = null;
		Method stringArraySetter = null;
		
		// String, enum, primitive
		for (Method method : setters) {
			Class<?> parameterType = method.getParameterTypes()[0];
			// String �Z�b�^�[�̏ꍇ
			if (parameterType.equals(String.class)) {
				stringSetter = method;
			}
			// ISelectOption �Z�b�^�[�̏ꍇ
			else if (parameterType.isEnum() && ISelectOption.class.isAssignableFrom(parameterType)) {
				if (enumSetter == null) {
					enumSetter = method;
				} else {
					throw new IllegalStateException("enum implements " + ISelectOption.class.getSimpleName() + " �������ɂƂ� " +
							methodToStringWithoutArgs(method) + " ���������݂��܂��B�Z�b�^�[����ӂɓ���ł��܂���B");
				}
			}
			// ��{�f�[�^�^ �Z�b�^�[�̏ꍇ
			else if (parameterType.isPrimitive() || isPrimitiveWrapper(parameterType)) {
				if (primitiveSetter == null) {
					primitiveSetter = method;
				} else {
					throw new IllegalStateException("��{�f�[�^�^�������ɂƂ� " + methodToStringWithoutArgs(method) +
							" ���������݂��܂��B�Z�b�^�[����ӂɓ���ł��܂���B");
				}
			}
			// String �z��Z�b�^�[�̏ꍇ
			else if (parameterType.isArray() || parameterType.getComponentType().equals(String.class)) {
				stringArraySetter = method;
			}
		}
		
		// String �Z�b�^�[�����s
		if (stringSetter != null) {
			try {
				stringSetter.invoke(parentObj, value);
			} catch (IllegalArgumentException e) { // �����`�F�b�N�����Ă���̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public �����擾���Ă��Ȃ��̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(stringSetter) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
			}
		}
		// �v���~�e�B�u �Z�b�^�[�����s
		else if (primitiveSetter != null) {
			try {
				Class<?> type = primitiveSetter.getParameterTypes()[0];
				Object primitiveValue = toPrimitive(value, type, result);
				if (result.isFailed) {
					String errorMessage = result.hasMessage() ? result.message : "";
					result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + value + "\" �� " + type.getName() + "�^ �ɕϊ��ł��܂���B" + errorMessage;
					return;
				}
				
				primitiveSetter.invoke(parentObj, primitiveValue);
			} catch (IllegalArgumentException e) { // �����`�F�b�N�����Ă���̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public �����擾���Ă��Ȃ��̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(primitiveSetter) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
			}
			return;
		}
		// enum �Z�b�^�[�����s
		else if (enumSetter != null) {
			Class<?> enumType = enumSetter.getParameterTypes()[0];
			ISelectOption selectOptionEnum = ObjectAccessor.getSelectOptionEnum(enumType, value);
			if (selectOptionEnum == null) {
				result.fail(enumType.getName() + " �� value=\"" + value + "\" �̗v�f�����݂��Ȃ����߁A�l���Z�b�g�ł��܂���B");
			}
			try {
				enumSetter.invoke(parentObj, selectOptionEnum);
			} catch (IllegalArgumentException e) { // �����`�F�b�N�����Ă���̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (IllegalAccessException e) { // public �����擾���Ă��Ȃ��̂ł��蓾�Ȃ��͂�
				throw (FrameworkBugException) new FrameworkBugException().initCause(e);
			} catch (InvocationTargetException e) {
				throw (SetterException) new SetterException(methodToString(enumSetter) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
			}
			return;
		}
		else {
			result.fail(parentObj.getClass().getName() + " �̃v���p�e�B \"" + getKey() + "\" �ɒl���Z�b�g�ł��܂���B setter �̈������Ή����Ă��Ȃ��^�ł��B");
		}
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#getBool(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected boolean getBool(Result result) {
		// TODO Auto-generated method stub
		return false;
	}

}
