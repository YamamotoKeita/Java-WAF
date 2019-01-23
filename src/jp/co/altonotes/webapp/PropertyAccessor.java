package jp.co.altonotes.webapp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.util.exception.AmbiguousSetterException;
import jp.co.altonotes.util.exception.GetterException;
import jp.co.altonotes.util.exception.SetterException;

/**
 * �I�u�W�F�N�g�̃v���p�e�B���擾�܂��͐ݒ肷��B
 * �v���p�e�B�Ƃ݂Ȃ����̂́Agetter�Asetter�Apublic�����o�[�B
 *
 * �z��̏ꍇ�AgetXXX(int), setXXX(int, Object)�`����getter�Asetter���g�p���邩�A
 * public�����o�[�̔z��ւ̒��ڃA�N�Z�X���s���B
 *
 * @author Yamamoto Keita
 *
 */
public class PropertyAccessor {

	/** �v���p�e�B�̋�؂�L�� */
	public static final String ATTRIBUTE_SEPARATOR = ".";

	private static final Class<?>[] TYPES_NULL = {};
	private static final Class<?>[] TYPES_INT = {int.class};
	private static final Object[] ARGS_NULL = {};

	private static final Class<?>[][] BASIC_TYPES_LIST = {
															{int.class},
															{boolean.class},
															{long.class},
															{double.class},
															{float.class},
															{char.class},
															{byte.class},
															{short.class},
															{Integer.class},
															{Boolean.class},
															{Long.class},
															{Double.class},
															{Float.class},
															{Character.class},
															{Byte.class},
															{Short.class}
														 };

	private static final Class<?>[][] BASIC_TYPES_WITH_INDEX_LIST = {
															{int.class, int.class},
															{int.class, boolean.class},
															{int.class, long.class},
															{int.class, double.class},
															{int.class, float.class},
															{int.class, char.class},
															{int.class, byte.class},
															{int.class, short.class},
															{int.class, Integer.class},
															{int.class, Boolean.class},
															{int.class, Long.class},
															{int.class, Double.class},
															{int.class, Float.class},
															{int.class, Character.class},
															{int.class, Byte.class},
															{int.class, Short.class}
														};

	private static final Class<?>[] FORBIDDEN_CLASSES = {ClassLoader.class, Class.class};

	/**
	 * �I�u�W�F�N�g����l�X�g�����v���p�e�B�l���擾����
	 * @param obj
	 * @param nestedProperty
	 * @return ���s����
	 */
	public static AccessResult getNestedProperty(Object obj, String nestedProperty) {
		return getNestedProperty(null, obj, nestedProperty);
	}

	/**
	 * �I�u�W�F�N�g����l�X�g�����v���p�e�B�l���擾����
	 * @param rootName
	 *
	 * @param obj
	 * @param nestedProperty
	 * @return ���s����
	 */
	public static AccessResult getNestedProperty(String rootName, Object obj, String nestedProperty) {

		String[] properties = nestedProperty.split("\\.", -1);
		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				sb.append(ATTRIBUTE_SEPARATOR + properties[i]);
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null && i < properties.length - 1) {
				result.message = combine(rootName, properties, i) + " �� null �ł��B";
				result.code = AccessResult.NOT_FOUND;
				return result;
			} else {
				obj = result.value;
			}
		}

		return result;
	}

	/**
	 * �I�u�W�F�N�g����C���f�b�N�X�w��\�L�[�ɂ��boolean�l���擾����B
	 *
	 * @param rootName
	 * @param obj
	 * @param nestedProperty
	 * @return ���s����
	 * @throws PropertyNotFoundException
	 */
	public static AccessResult getNestedFlag(String rootName, Object obj, String nestedProperty) {
		String[] properties = nestedProperty.split("\\.", -1);

		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length - 1; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				sb.append(ATTRIBUTE_SEPARATOR + properties[i]);
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null) {
				result.message = combine(rootName, properties, i) + " �� null �ł��B";
				return result;
			} else {
				obj = result.value;
			}
		}

		String property = properties[properties.length - 1];
		int idx = parseArrayIndex(property);
		if (idx != -1) {
			String name = property.substring(0, property.indexOf("["));
			sb.append(name);
			return getFlagProperty(sb.toString(), obj, name, idx);
		} else {
			return getFlagProperty(obj, property);
		}
	}

	/**
	 * boolean�l��Ԃ����\�b�h�����s����B
	 *
	 * @param rootName
	 * @param obj
	 * @param nestedProperty
	 * @return ���s����
	 */
	public static AccessResult doNestedCheckMethod(String rootName, Object obj, String nestedProperty) {
		String[] properties = nestedProperty.split("\\.", -1);

		AccessResult result = null;
		StringBuilder sb = new StringBuilder();
		sb.append(rootName);

		for (int i = 0; i < properties.length - 1; i++) {

			int idx = parseArrayIndex(properties[i]);

			if (idx != -1) {
				String name = properties[i].substring(0, properties[i].indexOf("["));
				sb.append(ATTRIBUTE_SEPARATOR + name);
				result = getProperty(sb.toString(), obj, name, idx);
			} else {
				result = getProperty(obj, properties[i]);
			}

			if (!result.isSuccess()) {
				return result;
			} else if (result.value == null) {
				result.message = combine(rootName, properties, i) + " �� null �ł��B";
				return result;
			} else {
				obj = result.value;
			}
		}

		return doCheckMethod(obj, properties[properties.length - 1]);
	}

	/**
	 * �I�u�W�F�N�g����v���p�e�B�l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getProperty(Object obj, String name) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doGetter(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result = getField(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result.code = AccessResult.NOT_FOUND;
		result.message = klass.getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";

		return result;
	}

	/**
	 * �I�u�W�F�N�g����v�f�̃C���f�b�N�X���w�肵�ăv���p�e�B�l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @param idx
	 * @return
	 */
	private static AccessResult getProperty(String fullName, Object obj, String name, int idx) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doGetter(obj, name, idx);
		if (result.isSuccess()) {
			return result;
		}

		result = doGetter(obj, name);
		if (!result.isSuccess()) {
			result = getField(obj, name);
		}

		if (!result.isSuccess()) {
			result.code = AccessResult.NOT_FOUND;
			result.message = klass.getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";
			return result;
		} else if (result.value == null) {
			result.code = AccessResult.NOT_FOUND;
			result.message = fullName + " �� null �ł��B";
			return result;
		}

		result = getElementAt(result.value, idx);

		if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
			result.message = fullName + " �ɃC���f�b�N�X " + idx + " �̗v�f������܂���B";
		} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
			result.message = fullName + " �̓C���f�b�N�X�w��ɂ��v�f�̃A�N�Z�X�ɑΉ����Ă��܂���B";
		}

		return result;
	}

	/**
	 * �I�u�W�F�N�g����boolean�����Boolean�̃v���p�e�B�l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getFlagProperty(Object obj, String name) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doFlagGetter(obj, name);
		if (result.isSuccess()) {
			return result;
		}

		result = getField(obj, name);
		if (result.isSuccess() && result.value instanceof Boolean) {
			return result;
		}

		result.code = AccessResult.NOT_FOUND;
		result.message = klass.getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";

		return result;
	}

	/**
	 * �I�u�W�F�N�g����v�f�̃C���f�b�N�X���w�肵�ăv���p�e�B�l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @param idx
	 * @return
	 */
	private static AccessResult getFlagProperty(String fullName, Object obj, String name, int idx) {
		AccessResult result;
		Class<?> klass = obj.getClass();

		result = doFlagGetter(obj, name, idx);
		if (result.isSuccess()) {
			return result;
		}

		result = doGetter(obj, name);
		if (!result.isSuccess()) {
			result = getField(obj, name);
		}

		if (!result.isSuccess()) {
			result.code = AccessResult.NOT_FOUND;
			result.message = klass.getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";
			return result;
		}

		result = getElementAt(result.value, idx);

		if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
			result.message = fullName + " �ɃC���f�b�N�X " + idx + " �̗v�f������܂���B";
		} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
			result.message = fullName + " �̓C���f�b�N�X�w��ɂ��v�f�̃A�N�Z�X�ɑΉ����Ă��܂���B";
		}

		return result;
	}

	/**
	 * �I�u�W�F�N�g����Q�b�^�[�ɂ��l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doGetter(Object obj, String name) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		name = toGetter(name);

		try {
			method = klass.getMethod(name, TYPES_NULL);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // getMethod�Ń`�F�b�N�����̂ŗL�蓾�Ȃ��͂�
			throw e;
		} catch (IllegalAccessException e) { //getMethod�Ń`�F�b�N�����̂ł����炭�L�蓾�Ȃ�
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * �I�u�W�F�N�g����C���f�b�N�X�w�肠��̃Q�b�^�[�ɂ��l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doGetter(Object obj, String name, int idx) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		name = toGetter(name);

		try {
			method = klass.getMethod(name, TYPES_INT);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, idx);
		} catch (IllegalArgumentException e) { // getMethod�Ń`�F�b�N�����̂ŗL�蓾�Ȃ��͂�
			throw e;
		} catch (IllegalAccessException e) { //getMethod�Ń`�F�b�N�����̂ł����炭�L�蓾�Ȃ�
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * �I�u�W�F�N�g����Q�b�^�[�ɂ��l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doFlagGetter(Object obj, String name) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		String isMethodName = toFlagGetter(name);

		try {
			method = klass.getMethod(isMethodName, TYPES_NULL);
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		if (method == null) {
			String getterName = toGetter(name);
			try {
				method = klass.getMethod(getterName, TYPES_NULL);
			} catch (SecurityException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} catch (NoSuchMethodException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		}

		try {
			result.value = method.invoke(obj, ARGS_NULL);
		} catch (IllegalArgumentException e) { // getMethod�Ń`�F�b�N�����̂ŗL�蓾�Ȃ��͂�
			throw e;
		} catch (IllegalAccessException e) { //getMethod�Ń`�F�b�N�����̂ł����炭�L�蓾�Ȃ�
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		if (!(result.value instanceof Boolean)) {
			result.value = null;
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * �I�u�W�F�N�g����C���f�b�N�X�w�肠��̃Q�b�^�[�ɂ��l���擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult doFlagGetter(Object obj, String name, int idx) {
		AccessResult result = new AccessResult();

		Method method = null;
		Class<?> klass = obj.getClass();

		String isMethodName = toFlagGetter(name);

		try {
			method = klass.getMethod(isMethodName, TYPES_INT);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		if (method == null) {
			String getterName = toGetter(name);
			try {
				method = klass.getMethod(getterName, TYPES_INT);
			} catch (SecurityException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} catch (NoSuchMethodException e) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		}

		try {
			result.value = method.invoke(obj, idx);
		} catch (IllegalArgumentException e) { // getMethod�Ń`�F�b�N�����̂ŗL�蓾�Ȃ��͂�
			throw e;
		} catch (IllegalAccessException e) { //getMethod�Ń`�F�b�N�����̂ł����炭�L�蓾�Ȃ�
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "  �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		if (!(result.value instanceof Boolean)) {
			result.value = null;
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}
	/**
	 * �I�u�W�F�N�g�̃t�B�[���h�l�𒼐ڎ擾����B
	 *
	 * @param obj
	 * @param name
	 * @return
	 */
	private static AccessResult getField(Object obj, String name) {
		AccessResult result = new AccessResult();
		Field field = null;

		try {
			field = obj.getClass().getField(name);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchFieldException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = field.get(obj);
		} catch (IllegalArgumentException e) {
			throw e; // �L�蓾�Ȃ��͂�
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * �z��܂��̓R���N�V��������A�w��C���f�b�N�X�̗v�f�����o���B
	 *
	 * @param obj
	 * @param idx
	 * @return
	 */
	private static AccessResult getElementAt(Object obj, int idx) {
		AccessResult result = new AccessResult();

		Object[] array = null;

		if (obj.getClass().isArray()) {
			// �z��̏ꍇ
			array = (Object[]) obj;
		} else if (obj instanceof Collection<?>) {
			// Collection�̏ꍇ
			array = ((Collection<?>) obj).toArray();
		} else {
			result.code = AccessResult.INDEX_IS_NOT_AVAILABLE;
			return result;
		}

		if (idx < 0 || array.length <= idx) {
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		result.value = array[idx];
		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * @param obj
	 * @param nameChain
	 * @param value
	 * @return ���s����
	 */
	public static AccessResult setNestedProperty(Object obj, String nameChain, Object value) {
		return setNestedProperty(null, obj, nameChain, value);
	}

	/**
	 * �I�u�W�F�N�g�ɃC���f�b�N�X�w��\�L�[�ɂ��l���Z�b�g����B
	 *
	 * @param rootName
	 * @param obj
	 * @param nameChain
	 * @param value
	 * @return ���s����
	 */
	public static AccessResult setNestedProperty(String rootName, Object obj, String nameChain, Object value) {
		String[] keyArray = nameChain.split("\\.", -1);
		Object target = obj;
		AccessResult result;

		if (keyArray.length > 1) {
			String key = nameChain.substring(0, nameChain.lastIndexOf('.'));
			result = getNestedProperty(rootName, obj, key);
			if (!result.isSuccess()) {
				return result;
			} else {
				target = result.value;
			}
		}

		String name = keyArray[keyArray.length - 1];
		int idx = TextUtils.parseArrayIndex(name);

		if (idx != -1) {
			String fieldName = name.substring(0, name.indexOf("["));
			result = setProperty(target, fieldName, value, idx);

			if (result.code == AccessResult.INDEX_OUT_OF_BOUNDS) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " �ɃC���f�b�N�X " + idx + " �̗v�f������܂���B";
			} else if (result.code == AccessResult.INDEX_IS_NOT_AVAILABLE) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " �̓C���f�b�N�X�w��ɂ��v�f�̃A�N�Z�X�ɑΉ����Ă��܂���B";
			} else if (result.code == AccessResult.ARRAY_IS_NULL) {
				result.message = combine(rootName, keyArray, keyArray.length - 1, fieldName) + " �� null �ł��B";
			}
		} else {
			result = setProperty(target, name, value);
		}

		return result;
	}

	/**
	 * �I�u�W�F�N�g�̎w�肵���v���p�e�B��String�l���Z�b�g����B
	 * �ŏ��ɃL�[�����ɃZ�b�^�[���Ăт����A�Z�b�^�[�Œl���擾�ł��Ȃ��ꍇ�A���ڃL�[���̃t�B�[���h���Q�Ƃ���B
	 *
	 * @param obj
	 * @param chainedKey
	 * @param value
	 * @throws AmbiguousSetterException
	 */
	private static AccessResult setProperty(Object obj, String name, Object value) throws AmbiguousSetterException {
		AccessResult result = doSetter(obj, name, value);
		if (result.isSuccess() || result.code != AccessResult.NOT_FOUND) {
			return result;
		}

		result = setField(obj, name, value);

		if (result.code == AccessResult.NOT_FOUND) {
			result.message = obj.getClass().getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";
		}

		return result;
	}

	private static AccessResult setProperty(Object obj, String name, Object value, int idx) throws AmbiguousSetterException {
		AccessResult result = doSetter(obj, name, idx, value);
		if (result.isSuccess() || result.code != AccessResult.NOT_FOUND) {
			return result;
		}

		result = getProperty(obj, name);
		if (!result.isSuccess()) {
			return result;
		} else if (result.value == null) {
			result.code = AccessResult.ARRAY_IS_NULL;
			return result;
		}

		result = setElementAt(result.value, idx, value);

		if (result.code == AccessResult.NOT_FOUND) {
			result.message = obj.getClass().getName() + " �̃v���p�e�B \"" + name+ "\" ��������܂���B";
		}

		return result;
	}

	/**
	 * �z��܂��̓R���N�V�����̎w��C���f�b�N�X�v�f�ɒl���Z�b�g����B
	 *
	 * @param obj
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static AccessResult setElementAt(Object obj, int index, Object value) throws NumberFormatException {

		if (index < 0) {
			AccessResult result = new AccessResult();
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		if (obj.getClass().isArray()) {
			// �z��̏ꍇ
			return setElementToArray(obj, index, value);
		} else if (obj instanceof Collection<?>) {
			// Collection�̏ꍇ
			Collection<Object> collection = (Collection<Object>) obj;
			return setElementToCollection(collection, index, value);
		} else {
			// ���̑�
			AccessResult result = new AccessResult();
			result.code = AccessResult.INDEX_IS_NOT_AVAILABLE;
			return result;
		}
	}

	/**
	 * Collection�̎w��v�f�ɒl���Z�b�g����B
	 *
	 * @param collection
	 * @param index
	 * @param value
	 * @return
	 */
	private static AccessResult setElementToCollection(Collection<Object> collection, int index, Object value) {
		AccessResult result = new AccessResult();

		if (collection.size() <= index) {
			result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
			return result;
		}

		Object[] temp = collection.toArray();
		Class<?> componentType = temp[0].getClass(); // size�`�F�b�N�����Ă�̂�[0]�͕K������

		if (componentType.isInstance(value)) {
			collection.clear();

			for (int i = 0; i < temp.length; i++) {
				if (i == index) {
					collection.add(value);
				} else {
					collection.add(temp[i]);
				}
			}

			result.code = AccessResult.SUCCESS;
			return result;
		} else {
			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, componentType);

			if (!convertResult.isSuccess()) {
				if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
					result.code = AccessResult.NOT_FOUND;
					return result;
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + componentType.getName() + "�^ �ɕϊ��ł��܂���B";
					if (convertResult.hasMessage()) {
						result.message += convertResult.message;
					}
					return result;
				}
			}

			collection.clear();

			for (int i = 0; i < temp.length; i++) {
				if (i == index) {
					collection.add(convertResult.value);
				} else {
					collection.add(temp[i]);
				}
			}

			result.code = AccessResult.SUCCESS;
			return result;
		}
	}

	/**
	 * �z��̎w��v�f�ɒl���Z�b�g����B
	 *
	 * @param obj
	 * @param index
	 * @param value
	 * @return
	 */
	private static AccessResult setElementToArray(Object obj, int index, Object value) {
		AccessResult result = new AccessResult();

		// �z��̏ꍇ
		Class<?> componentType = obj.getClass().getComponentType();


		if (!componentType.isPrimitive()) { // ��v���~�e�B�u�^
			Object[] array = (Object[]) obj;

			if (array.length <= index) {
				result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
				return result;
			}

			if (componentType.isInstance(value)) { // �Z�b�g����l�̌^���z��ƍ����Ă���ꍇ
				array[index] = value;
			} else if (value instanceof String) {
				String valueStr = (String) value;
				AccessResult convertResult = convert(valueStr, componentType);

				if (convertResult.isSuccess()) {
					array[index] = convertResult.value;
				} else if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
					result.code = AccessResult.NOT_FOUND;
					return result;
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + componentType.getName() + "�^ �ɕϊ��ł��܂���B";
					if (convertResult.hasMessage()) {
						result.message += convertResult.message;
					}
					return result;
				}

			} else {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}
		} else if (value instanceof String) { // �v���~�e�B�u�^����set����l��String
			if (primitiveArrayLength(obj, componentType) <= index) {
				result.code = AccessResult.INDEX_OUT_OF_BOUNDS;
				return result;
			}

			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, componentType);

			if (convertResult.isSuccess()) {
				setElementToPrimitiveTypeArray(index, obj, convertResult.value, componentType);
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + componentType.getName() + "�^ �ɕϊ��ł��܂���B";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		} else {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}
		result.code = AccessResult.SUCCESS;
		return result ;
	}

	/**
	 * �w�薼�̃Z�b�^�[�����s����B
	 * value�̌^�𔻒肵�A���̌^�������Ɏ��Z�b�^�[�����s����B
	 * null�l���Z�b�g���邱�Ƃ͂ł��Ȃ��B
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 * @throws AmbiguousSetterException
	 */
	private static AccessResult doSetter(Object obj, String name, Object value) throws AmbiguousSetterException {
		//TODO null����setter�̈����̌^���킩��ˁ[���疳���B�Ȃ�Ƃ��Ȃ�ˁ[���ȁB
		if (value == null) {
			throw new NullPointerException("null���Z�b�g���邱�Ƃ͂ł��܂���");
		}

		AccessResult result = new AccessResult();
		Method method = null;
		Class<?> klass = obj.getClass();
		Object arg = value;

		name = toSetter(name);

		try {
			method = klass.getMethod(name, new Class<?>[]{value.getClass()});
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		// ��{�f�[�^�^������setter
		if (method == null && value instanceof String) {
			String valueStr = (String) value;

			Class<?> type = null;
			int hitCount = 0;

			// TODO ���[�v��Exception�𔭐�������̂̓p�t�H�[�}���X���ǂ��Ȃ��B�S���\�b�h����T�[�`�ɕύX����ׂ��B
			// �e��{�f�[�^�^��setter��T��
			for (Class<?>[] types : BASIC_TYPES_LIST) {
				Method setterAsType = null;

				try {
					setterAsType = klass.getMethod(name, types);
				} catch (SecurityException e1) {
					setterAsType = null;
				} catch (NoSuchMethodException e1) {
					setterAsType = null;
				}

				if (setterAsType != null) {
					method = setterAsType;
					type = types[0];
					hitCount++;
				}

				if (1 < hitCount) {
					throw new AmbiguousSetterException("��{�f�[�^�^�������ɂƂ� " + klass.getName() + "#" + name + " ���������݂��܂��B�Z�b�^�[����ӂɓ���ł��܂���");
				}
			}

			if (method == null) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}

			AccessResult convertResult = convert(valueStr, type);
			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + type.getName() + "�^ �ɕϊ��ł��܂���B";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		if (method == null) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			method.invoke(obj, arg);
		} catch (IllegalArgumentException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(toMessage(method) + " �̎��s���ɗ�O���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * �w�薼�̃C���f�b�N�X�w�肠��Z�b�^�[�����s����B
	 * value�̌^�𔻒肵�A���̌^�������Ɏ��Z�b�^�[�����s����B
	 * null�l���Z�b�g���邱�Ƃ͂ł��Ȃ��B
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 * @throws PropertyNotFoundException
	 * @throws AmbiguousSetterException
	 * @throws PropertyFormatException
	 */
	private static AccessResult doSetter(Object obj, String name, int idx, Object value) {
		if (value == null) {
			throw new NullPointerException("null���Z�b�g���邱�Ƃ͂ł��܂���");
		}

		AccessResult result = new AccessResult();
		Method method = null;
		Class<?> klass = obj.getClass();
		Object arg = value;

		name = toSetter(name);

		try {
			method = klass.getMethod(name, new Class<?>[]{int.class, value.getClass()});
		} catch (SecurityException e) {
			method = null;
		} catch (NoSuchMethodException e) {
			method = null;
		}

		// ��{�f�[�^�^������setter
		if (method == null && value instanceof String) {
			String valueStr = (String) value;

			Class<?> type = null;
			int hitCount = 0;

			// TODO ���[�v��Exception�𔭐�������̂̓p�t�H�[�}���X���ǂ��Ȃ��B�S���\�b�h����T�[�`�ɕύX����ׂ��B
			// �e��{�f�[�^�^��setter��T��
			for (Class<?>[] types : BASIC_TYPES_WITH_INDEX_LIST) {
				Method setterAsType = null;
				try {
					setterAsType = klass.getMethod(name, types);
				} catch (SecurityException e1) {
					setterAsType = null;
				} catch (NoSuchMethodException e1) {
					setterAsType = null;
				}

				if (setterAsType != null) {
					method = setterAsType;
					type = types[1];
					hitCount++;
				}

				if (1 < hitCount) {
					throw new AmbiguousSetterException("��{�f�[�^�^�������ɂƂ� " + klass.getName() + "#" + name + " ���������݂��܂��B�Z�b�^�[����ӂɓ���ł��܂���");
				}
			}

			if (method == null) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			}

			AccessResult convertResult = convert(valueStr, type);
			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + type.getName() + "�^ �ɕϊ��ł��܂���B";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		if (method == null) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			method.invoke(obj, idx, arg);
		} catch (IllegalArgumentException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (InvocationTargetException e) {
			throw (SetterException) new SetterException(toMessage(method) + " �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * �I�u�W�F�N�g�̎w�薼�̃t�B�[���h�ɒl���Z�b�g����B
	 *
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 */
	private static AccessResult setField(Object obj, String name, Object value) {
		Field member = null;
		Object arg = value;

		AccessResult result = new AccessResult();
		try {
			member = obj.getClass().getField(name);
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchFieldException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		Class<?> type = member.getType();

		if (!type.isInstance(value) && value instanceof String) {
			String valueStr = (String) value;
			AccessResult convertResult = convert(valueStr, type);

			if (convertResult.isSuccess()) {
				arg = convertResult.value;
			} else if (convertResult.code == AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE) {
				result.code = AccessResult.NOT_FOUND;
				return result;
			} else {
				result.code = AccessResult.INVALID_FORMAT;
				result.message = "�^�ϊ��Ɏ��s���܂����B" + "\"" + valueStr + "\" �� " + type.getName() + "�^ �ɕϊ��ł��܂���B";
				if (convertResult.hasMessage()) {
					result.message += convertResult.message;
				}
				return result;
			}
		}

		try {
			member.set(obj, arg);
		} catch (IllegalArgumentException e) {// �L�蓾�Ȃ��͂�
			throw e;
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;
		return result;
	}

	/**
	 * �I�u�W�F�N�g��boolean��Ԃ����\�b�h�����s����B
	 *
	 * @param obj
	 * @param methodName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private static AccessResult doCheckMethod(Object obj, String methodName) {
		AccessResult result = new AccessResult();
		Method method = null;

		try {
			method = obj.getClass().getMethod(methodName, new Class<?>[]{});
		} catch (SecurityException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		} catch (NoSuchMethodException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		try {
			result.value = method.invoke(obj, new Object[0]);
		} catch (InvocationTargetException e) {
			throw (GetterException) new GetterException(toMessage(method) + "\" �̎��s���ɃG���[���������܂����F" + e.getCause()).initCause(e.getCause());
		} catch (IllegalArgumentException e) {
			throw e; // �L�蓾�Ȃ��͂�
		} catch (IllegalAccessException e) {
			result.code = AccessResult.NOT_FOUND;
			return result;
		}

		result.code = AccessResult.SUCCESS;

		return result;
	}

	/**
	 * �v���~�e�B�u�^�̔z��̒������擾����
	 *
	 * @param obj
	 * @param componentType
	 * @return
	 */
	private static int primitiveArrayLength(Object obj, Class<?> type) {
		if (type.equals(int.class)) {
			int[] array = (int[]) obj;
			return array.length;
		} else if (type.equals(boolean.class)) {
			boolean[] array = (boolean[]) obj;
			return array.length;
		} else if (type.equals(long.class)) {
			long[] array = (long[]) obj;
			return array.length;
		} else if (type.equals(double.class)) {
			double[] array = (double[]) obj;
			return array.length;
		} else if (type.equals(float.class)) {
			float[] array = (float[]) obj;
			return array.length;
		} else if (type.equals(char.class)) {
			char[] array = (char[]) obj;
			return array.length;
		} else if (type.equals(byte.class)) {
			byte[] array = (byte[]) obj;
			return array.length;
		} else if (type.equals(short.class)) {
			short[] array = (short[]) obj;
			return array.length;
		} else {
			throw new IllegalArgumentException(type + " �̓v���~�e�B�u�^�ł͂���܂���B");
		}
	}

	/**
	 * �v���~�e�B�u�^�̔z��v�f�Ƀf�[�^���Z�b�g����
	 *
	 * @param index
	 * @param obj
	 * @param arg
	 * @param componentType
	 */
	private static void setElementToPrimitiveTypeArray(int index, Object obj, Object arg, Class<?> type) {
		if (type.equals(int.class)) {
			int[] array = (int[]) obj;
			array[index] = (Integer) arg;
		} else if (type.equals(boolean.class)) {
			boolean[] array = (boolean[]) obj;
			array[index] = (Boolean) arg;
		} else if (type.equals(long.class)) {
			long[] array = (long[]) obj;
			array[index] = (Long) arg;
		} else if (type.equals(double.class)) {
			double[] array = (double[]) obj;
			array[index] = (Double) arg;
		} else if (type.equals(float.class)) {
			float[] array = (float[]) obj;
			array[index] = (Float) arg;
		} else if (type.equals(char.class)) {
			char[] array = (char[]) obj;
			array[index] = (Character) arg;
		} else if (type.equals(byte.class)) {
			byte[] array = (byte[]) obj;
			array[index] = (Byte) arg;
		} else if (type.equals(short.class)) {
			short[] array = (short[]) obj;
			array[index] = (Short) arg;
		} else {
			throw new IllegalStateException(type + " �̓v���~�e�B�u�^�ł͂���܂���B");
		}
	}

	/**
	 * �v���p�e�B�����Q�b�^�[���ɕϊ�����B
	 *
	 * @param name
	 * @return
	 */
	private static String toFlagGetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "is" + name;
		return name;
	}

	/**
	 * �v���p�e�B�����Q�b�^�[���ɕϊ�����B
	 *
	 * @param name
	 * @return
	 */
	private static String toGetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "get" + name;
		return name;
	}

	/**
	 * �v���p�e�B�����Z�b�^�[���ɕϊ�����B
	 *
	 * @param name
	 * @return
	 */
	private static String toSetter(String name) {
		if (name.length() > 0) {
			name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		}
		name = "set" + name;
		return name;
	}

	/**
	 * Method��Exception���b�Z�[�W�ɕ\�L���邽�߂̕�����ɂ���B
	 *
	 * @param method
	 * @return
	 */
	private static String toMessage(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName() + "(");

		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(types[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * ��������e��{�f�[�^�^�ɕϊ�����
	 *
	 * @param str
	 * @param type
	 * @return
	 */
	private static AccessResult convert(String str, Class<?> type) {
		AccessResult result = new AccessResult();
		result.code = AccessResult.SUCCESS;

		try {
			if (type.equals(int.class) || Integer.class.isAssignableFrom(type)) {
				result.value = Integer.parseInt(str);
			} else if (type.equals(boolean.class) || Boolean.class.isAssignableFrom(type)) {
				result.value = Boolean.parseBoolean(str);
				if (!(Boolean)result.value && !str.equalsIgnoreCase("false") && str.trim().length() > 0) {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "�啶���������� \"true\" \"false\" �܂��͋󕶎��A���p�X�y�[�X�̂ݕϊ��ł��܂��B";
				}
			} else if (type.equals(long.class) || Long.class.isAssignableFrom(type)) {
				result.value = Long.parseLong(str);
			} else if (type.equals(double.class) || Double.class.isAssignableFrom(type)) {
				result.value = Double.parseDouble(str);
			} else if (type.equals(float.class) || Float.class.isAssignableFrom(type)) {
				result.value = Float.parseFloat(str);
			} else if (type.equals(char.class) || Character.class.isAssignableFrom(type)) {
				if (str.length() == 1) {
					result.value = str.charAt(0);
				} else {
					result.code = AccessResult.INVALID_FORMAT;
					result.message = "���� 1 �̕�����̂ݕϊ��ł��܂��B";
				}
			} else if (type.equals(byte.class) || Byte.class.isAssignableFrom(type)) {
				result.value = Byte.parseByte(str);
			} else if (type.equals(short.class) || Short.class.isAssignableFrom(type)) {
				result.value = Short.parseShort(str);
			} else {
				result.code = AccessResult.TYPE_CONVERSION_IS_NOT_AVAILABLE;
				return result;
			}
		} catch (NumberFormatException e) {
			result.code = AccessResult.INVALID_FORMAT;
		}

		return result;
	}

	/**
	 * ������̔z����w�肵���C���f�b�N�X�܂Ńh�b�g�q���ŘA������B
	 *
	 * @param properties
	 * @param i
	 * @return
	 */
	private static String combine(String rootName, String[] properties, int i) {
		StringBuilder sb = new StringBuilder();

		if (rootName != null) {
			sb.append(rootName + ".");
		}

		for (int j = 0; j <= i; j++) {
			if (j != 0) {
				sb.append(".");
			}
			sb.append(properties[j]);
		}
		return sb.toString();
	}

	/**
	 * ������̔z����w�肵���C���f�b�N�X�܂Ńh�b�g�q���ŘA������B
	 *
	 * @param properties
	 * @param i
	 * @return
	 */
	private static String combine(String rootName, String[] properties, int i, String lastName) {
		StringBuilder sb = new StringBuilder();

		if (rootName != null) {
			sb.append(rootName + ".");
		}

		for (int j = 0; j <= i; j++) {
			if (j != 0) {
				sb.append(".");
			}

			if (j == i) {
				sb.append(lastName);
			} else {
				sb.append(properties[j]);
			}
		}
		return sb.toString();
	}

	/**
	 * �����񖖔��ɂ���A[]�Ɉ͂܂ꂽ���l��int�Ƃ��Ď擾����B<br>
	 * ��jarray[1]�̏ꍇ�A1���Ԃ�B<br>
	 * <br>
	 * �ȉ��̏ꍇ��-1��Ԃ��B
	 * <ul>
	 * <li>������[]�����݂��Ȃ�</li>
	 * <li>[]���𐔒l�Ƃ��ĔF���ł��Ȃ�</li>
	 * <li>[]�������̐�</li>
	 * </ul>
	 *
	 * @param str
	 * @return
	 */
	private static int parseArrayIndex(String str) {
		int aryIdx = -1;
		String idxStr = null;
		int head = 0;
		int tail = 0;

		head = str.indexOf("[");
		if (head == -1) {
			return -1;
		}

		tail = str.indexOf("]", head + 2);
		if (tail != str.length() - 1) {
			return -1;
		}

		idxStr = str.substring(head + 1, tail);
		if (Checker.isNumber(idxStr)) {
			aryIdx = Integer.parseInt(idxStr);
		} else {
			return -1;
		}

		if (aryIdx >= 0) {
			return aryIdx;
		} else {
			return -1;
		}
	}

	/**
	 * Property�A�N�Z�X�̌��ʂ�ێ�����B
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static class AccessResult {
		/** �A�N�Z�X����	*/
		public static final int SUCCESS = 1;
		/** �Ώۂ̃v���p�e�B��������Ȃ�	*/
		public static final int NOT_FOUND = 2;
		/** �s���ȃt�H�[�}�b�g	*/
		public static final int INVALID_FORMAT = 3;
		/** �C���f�b�N�X���g�p�ł��Ȃ��t�B�[���h�ɃC���f�b�N�X���w�肳�ꂽ	*/
		public static final int INDEX_IS_NOT_AVAILABLE = 4;
		/** �͈͊O�̃C���f�b�N�X���w�肳�ꂽ	*/
		public static final int INDEX_OUT_OF_BOUNDS = 5;
		/** �^�ϊ����ł��Ȃ�����	*/
		public static final int TYPE_CONVERSION_IS_NOT_AVAILABLE = 6;
		/** �z��v�f��null	*/
		public static final int ARRAY_IS_NULL = 7;

		/**
		 * �A�N�Z�X���ʃI�u�W�F�N�g
		 */
		public Object value;

		/**
		 * ���ʃR�[�h
		 */
		public int code;

		/**
		 * ���b�Z�[�W
		 */
		public String message;

		/**
		 * @return �A�N�Z�X�����̏ꍇ<code>true</code>
		 */
		public boolean isSuccess() {
			if (code != SUCCESS) return false;

			if (value != null) {
				for (Class<?> klass : FORBIDDEN_CLASSES) {
					if (klass.isAssignableFrom(value.getClass())) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * @return ���b�Z�[�W������ꍇ<code>true</code>
		 */
		public boolean hasMessage() {
			return message != null;
		}

		@Override
		public String toString() {
			return "AccessResult [code=" + code + ", message=" + message
					+ ", value=" + value + "]";
		}
	}

}
