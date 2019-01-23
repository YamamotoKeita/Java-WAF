package jp.co.altonotes.webapp.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.webapp.exception.FrameworkBugException;
import jp.co.altonotes.webapp.form.ISelectOption;

/**
 * @author Yamamoto Keita
 *
 */
public class ObjectAccessor {
	
	/**
	 * �I�u�W�F�N�g�̎��A�w�肵�� Property ��getter�� �擾����B
	 * @param obj
	 * @param name
	 * @param indexed
	 * @return �Q�b�^�[
	 */
	public static Method getGetter(Object obj, String name, boolean indexed) {
		
		Method[] methods = obj.getClass().getMethods();
		String getterName = toGetterName(name, "get");
		String isName = toGetterName(name, "is");
		
		Method isGetter = null;
		
		// �S public ���\�b�h���猟��
		for (Method method : methods) {
			
			// �߂�l�� void �Ȃ�X�L�b�v
			Class<?> returnType = method.getReturnType();
			if (returnType.equals(Void.class)) {
				continue;
			}
			
			// �������Q�b�^�[�p����Ȃ����̂̓X�L�b�v
			Class<?>[] paramTypes = method.getParameterTypes();
			if (!indexed) {
				if (paramTypes.length != 0) continue;
			} else {
				if (paramTypes.length != 1 || !(paramTypes[0].equals(int.class) || paramTypes[0].equals(Integer.class))) continue;
			}
			
			// getXXX() �`���Ƀ}�b�`����Ȃ�I��
			if (getterName.equals(method.getName())) {
				return method;
			} 
			// isXXX() �`���Ƀ}�b�`����Ȃ�A����getXXX() ��������������
			else if (isName.equals(method.getName())) {
				isGetter = method;
			}
		}
		
		return isGetter;
	}

	/**
	 * �I�u�W�F�N�g�̎��A�w�肵�� Property ��setter��S�Ď擾����B
	 * 
	 * @param name
	 * @param indexed 
	 * @return �Z�b�^�[�̔z��
	 */
	static List<Method> getSetters(Object obj, String name, boolean indexed) {

		List<Method> list = new ArrayList<Method>();
		
		Method[] methods = obj.getClass().getMethods();
		name = toSetterName(name);
		for (Method method : methods) {
			if (!method.getName().equals(name)) {
				continue;
			}
			
			Class<?>[] paramTypes = method.getParameterTypes();
			
			// �C���f�b�N�X�t���Z�b�^�[
			if (indexed) {
				if (paramTypes.length == 2 && 
						(paramTypes[0].equals(int.class) || paramTypes[0].equals(Integer.class))) {
					list.add(method);
				}
			}
			// �C���f�b�N�X�Ȃ��Z�b�^�[
			else if (paramTypes.length == 1) {
				list.add(method);
			}
		}
		
		return list;
	}

	/**
	 * �v���p�e�B�����Z�b�^�[���ɕϊ�����B
	 *
	 * @param name
	 * @return
	 */
	static String toSetterName(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "set" + name;
		return name;
	}

	/**
	 * �v���p�e�B�����Q�b�^�[���ɕϊ�����B
	 * @param name
	 * @param prefix
	 * @return �Q�b�^�[��
	 */
	static String toGetterName(String name, String prefix) {
		if (0 < name.length()) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = prefix + name;
		return name;
	}
	
	/**
	 * ������ enum �N���X�ɂ����āAvalue �ɑΉ����� SelectOption �̃C���X�^���X���擾����
	 * @param klass
	 * @param value
	 * @return value �ɑΉ����� SelectOption �̃C���X�^���X
	 */
	static ISelectOption getSelectOptionEnum(Class<?> klass, String value) {
		Method valuesMethod = null;
		
		try {
			valuesMethod = klass.getMethod("values", new Class<?>[]{});
			ISelectOption[] values = (ISelectOption[]) valuesMethod.invoke(null, new Object[]{});
			for (ISelectOption selectOption : values) {
				if (selectOption.getValue().equals(value)) {
					return selectOption;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw (FrameworkBugException) new FrameworkBugException().initCause(e);
		}
		
		return null;
	}

}
