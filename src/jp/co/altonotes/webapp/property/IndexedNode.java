package jp.co.altonotes.webapp.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import jp.co.altonotes.util.exception.AmbiguousSetterException;
import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.util.exception.SetterException;
import jp.co.altonotes.webapp.exception.FrameworkBugException;

/**
 * �C���f�b�N�X�Y���t���̃m�[�h
 * @author Yamamoto Keita
 *
 */
public class IndexedNode extends PropertyNode {
	
	private int index;
	private String name;

	/**
	 * �R���X�g���N�^�[
	 * @param index
	 */
	protected IndexedNode(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#extractObject(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected Object extractObject(Result result) {
		Class<?> klass = parentObj.getClass();
		Object resultObj = Undefined.VALUE;
		
		resultObj = doGetter(name, index, result);
		if (!result.isFailed) {
			return resultObj;
		}

		resultObj = doGetter(name, result);
		if (result.isFailed) {
			resultObj = getField(name, result);
		}

		String fullName = fullName();
		if (result.isFailed) {
			result.fail(klass.getName() + " �̃v���p�e�B \"" + name + "\" ��������܂���B");
			return Undefined.VALUE;
		} else if (resultObj == null) {
			result.fail(fullName + " �� null �ł��B");
			return Undefined.VALUE;
		}

		resultObj = getElementAt(resultObj, index, result);

		return null;
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#set(java.lang.String, jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValue(String value, Result result) {
		// TODO Auto-generated method stub
		
//		doSetter(obj, name, index, value);
//		// �Z�b�^�[������ꍇ�͂����ŏI��
//		if (!result.isFailed() || result.errorCode != Result.NOT_FOUND) {
//			return;
//		}
//
//		result = getProperty(obj, name);
//		if (!result.isSuccess()) {
//			return result;
//		} else if (result.value == null) {
//			result.code = AccessResult.ARRAY_IS_NULL;
//			return result;
//		}
//
//		result = setElementAt(result.value, idx, value);
//
//		if (result.code == AccessResult.NOT_FOUND) {
//			result.message = obj.getClass().getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";
//		}
//
	}

	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#setValueArray(java.lang.String[], jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected void setValueArray(String[] value, Result result) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see jp.co.altonotes.webapp.property.PropertyNode#getBool(jp.co.altonotes.webapp.property.Result)
	 */
	@Override
	protected boolean getBool(Result result) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * �z��܂��̓R���N�V��������A�w��C���f�b�N�X�̗v�f�����o���B
	 *
	 * @param obj
	 * @param idx
	 * @return
	 */
	private Object getElementAt(Object obj, int idx, Result result) {

		Object[] array = null;

		// �z��̏ꍇ
		if (obj.getClass().isArray()) {
			array = (Object[]) obj;
		}
		// Collection�̏ꍇ
		else if (obj instanceof Collection<?>) {
			array = ((Collection<?>) obj).toArray();
		}
		// ���̑�
		else {
			result.message = fullName() + " �ɃC���f�b�N�X " + idx + " �̗v�f������܂���B";
			return Undefined.VALUE;
		}

		if (idx < 0 || array.length <= idx) {
			result.message = fullName() + " �̓C���f�b�N�X�w��ɂ��v�f�̃A�N�Z�X�ɑΉ����Ă��܂���B";
			return Undefined.VALUE;
		}

		return array[idx];
	}
	
	/**
	 * �C���f�b�N�X�t���̃Q�b�^�[�����s���ăI�u�W�F�N�g���擾����
	 * 
	 * @param index
	 * @param result
	 * @return
	 */
	protected Object doGetter(String name, int index, Result result) {
		Method getter = ObjectAccessor.getGetter(parentObj, name, true);
		if (getter == null) {
			result.fail(null);
			return Undefined.VALUE;
		}
		
		Object obj = null;
		try {
			obj = getter.invoke(parentObj, index);
		} catch (IllegalArgumentException e) { // �����`�F�b�N�����Ă���̂ł��蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) { // public �����擾���Ă��Ȃ��̂ł��蓾�Ȃ��͂�
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(methodToString(getter) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		return obj;
	}

	private void doSetter(String name, String value, Result result) throws AmbiguousSetterException {

		Method method = null;
		Class<?> klass = parentObj.getClass();
		Object arg = value;

		List<Method> setters = ObjectAccessor.getSetters(parentObj, name, true);

		// ��{�f�[�^�^������setter
//		throw new AmbiguousSetterException("��{�f�[�^�^�������ɂƂ� " + klass.getName() + "#" + name + " ���������݂��܂��B�Z�b�^�[����ӂɓ���ł��܂���");

		try {
			method.invoke(obj, arg);
		} catch (IllegalArgumentException e) {
			result.fail(null);
		} catch (IllegalAccessException e) {
			result.fail(null);
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(methodToString(method) + " �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}
	}

	/**
	 * @return �Y����O�܂ł̃v���p�e�B��
	 */
	private String fullName() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			if (i != 0) {
				sb.append(".");
			}
			sb.append(keys[i]);
		}
		sb.append(name);
		return sb.toString();
	}

}
