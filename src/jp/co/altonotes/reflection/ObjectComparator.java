package jp.co.altonotes.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

/**
 * ��̃I�u�W�F�N�g���r���āA����������B
 * 
 * @author Yamamoto Keita
 *
 */
public class ObjectComparator {
	
	private List<String> messageList = new ArrayList<String>();
	
	/**
	 * ��r���ʂ��o�͂���
	 * @param obj1
	 * @param obj2
	 */
	public static void printDiff(Object obj1, Object obj2) {
		System.out.println("----------- [" + obj1 + "] �� [" + obj2 + "] ���r���܂�");
		List<String> resultList = compare(obj1, obj2);
		for (String string : resultList) {
			System.out.println(string);
		}
	}
	
	/**
	 * 
	 * @param obj1
	 * @param obj2
	 * @return ���b�Z�[�W���X�g
	 */
	public static List<String> compare(Object obj1, Object obj2) {
		ObjectComparator comparator = new ObjectComparator();
		boolean equals = comparator.equals(obj1, obj2);
		List<String> list = comparator.getMessageList();
		if (equals) {
			list.add("��̃I�u�W�F�N�g�͓��l�ł�");
		}
		return list;
	}
	
	/**
	 * ���Bean���r����
	 * @param obj1
	 * @param obj2
	 * @return ���Bean����������� <code>true</code>
	 */
	public boolean equals(Object obj1, Object obj2) {
		Property property = new Property(obj1.getClass().getSimpleName(), obj1);
		return equals(property, obj1, obj2);
	}
	
	private boolean equals(Property property, Object obj1, Object obj2) {
		//System.out.println("-" + property.getName());
		
		// ���� & null �`�F�b�N
		if (obj1 == obj2) {
			return true;
		} else if (obj1 == null || obj2 == null) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		// �N���X�`�F�b�N
		if (!obj1.getClass().equals(obj2.getClass())) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		// ���Ƀ`�F�b�N�ς݂̃C���X�^���X�Ȃ�X�L�b�v
		if (property.overlappingContains(obj1)) {
			messageList.add(property.getName() + " - ����I�u�W�F�N�g���ċA�I�ɕ\�ꂽ���߃X�L�b�v���܂�");
			return true;
		}
		
		Class<?> klass = obj1.getClass();

		// �I�[�o�[���[�h���ꂽ equals ���\�b�h
		Method[] equalsMethods = getOverLoadedEqualsMethods(klass);
		for (Method method : equalsMethods) {
			if (method.getParameterTypes()[0].isAssignableFrom(obj2.getClass())) {
				return invokeEquals(property, method, obj1, obj2);
			}
		}

		// ���ʂ� equals ���\�b�h
		if (isAvailableSimpleEqualsMethod(klass)) {
			return simpleEquals(property, obj1, obj2);
		}
		
		// �z��
		if (klass.isArray()) {
			return arrayEquals(property, obj1, obj2);
		}
		// List
		else if (List.class.isAssignableFrom(klass)) {
			return listEquals(property, obj1, obj2);
		}
		// SortedSet
		else if (SortedSet.class.isAssignableFrom(klass)) {
			return orderedCollectionEquals(property, obj1, obj2);
		}
		// Map
		else if (Map.class.isAssignableFrom(klass)) {
			return mapEquals(property, obj1, obj2);
		}
		// HashSet
		else if (HashSet.class.isAssignableFrom(klass)) {
			return nonOrderedSetEquals(property, obj1, obj2);
		}

		return fieldEquals(property, obj1, obj2);
	}

	/**
	 * �N���X�̃����o�[�t�B�[���h�������������肷��
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	private boolean fieldEquals(Property property, Object obj1, Object obj2) {
		boolean equals = true;
		
		Class<?> klass = obj1.getClass();
		while (klass != null && !klass.equals(Object.class)) {
			
			// �I�[�o�[���[�h���ꂽ equals ���\�b�h
			Method[] equalsMethods = getOverLoadedEqualsMethods(klass);
			for (Method method : equalsMethods) {
				if (method.getParameterTypes()[0].isAssignableFrom(obj2.getClass())) {
					return equals && invokeEquals(property, method, obj1, obj2);
				}
			}
			
			// ���ʂ� equals ���\�b�h
			Method method = getOverRidedEqualsMethod(klass);
			if (method != null) {
				return equals && invokeEquals(property, method, obj1, obj2);
			}
			
			// �t�B�[���h��r
			if (!declaredFieldsEquals(property, obj1, obj2, klass)) {
				equals = false;
			}
			
			klass = klass.getSuperclass();
		}
		
		return equals;
	}

	/**
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @param klass1
	 * @return �N���X�ɐ錾���ꂽ�t�B�[���h����̃I�u�W�F�N�g�œ������ꍇ <code>true</code>
	 */
	private boolean declaredFieldsEquals(Property property, Object obj1, Object obj2, Class<?> klass1) {

		boolean equals = true;
		Field[] fields = klass1.getDeclaredFields();
		
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			Property newProperty = property.clone();
			newProperty.addName(field.getName());
			field.setAccessible(true);
			
			try {
				Object fieldItem1 = field.get(obj1);
				Object fieldItem2 = field.get(obj2);
				newProperty.addObject(fieldItem1);
				
				if (!equals(newProperty, fieldItem1, fieldItem2)) {
					equals = false;
				}
			} catch (IllegalArgumentException e) { //���O�`�F�b�N���Ă���̂łȂ��͂�
				throw e;
			} catch (IllegalAccessException e) {
				messageList.add(e + " : " + property.getName());
			}
		}
		return equals;
	}

	/**
	 * �z��̓��l������s��
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return ��̔z�񂪓����Ȃ� <code>true</code>
	 */
	private boolean arrayEquals(Property property, Object obj1, Object obj2) {
		
		int size1 = Array.getLength(obj1);
		int size2 = Array.getLength(obj2);
		
		if (size1 != size2) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		boolean equals = true;
		
		for (int i = 0; i < size1; i++) {
			Object e1 = Array.get(obj1, i);
			Object e2 = Array.get(obj2, i);
			
			Property newProperty = property.clone();
			newProperty.addIndex(i);
			newProperty.addObject(e1);
			
			if (!equals(newProperty, e1, e2)) {
				equals = false;
			}
		}
		return equals;
	}

	/**
	 * List�̓��l������s��
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return ���List�������Ȃ� <code>true</code>
	 */
	private boolean listEquals(Property property, Object obj1, Object obj2) {
		List<?> list1 = (List<?>) obj1;
		List<?> list2 = (List<?>) obj2;
		
		if (list1.size() != list2.size()) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		boolean equals = true;
		
		for (int i = 0; i < list1.size(); i++) {
			Property newProperty = property.clone();
			newProperty.addIndex(i);
			newProperty.addObject(list1.get(i));
			
			if (!equals(newProperty, list1.get(i), list2.get(i))) {
				equals = false;
			}
		}
		return equals;
	}

	/**
	 * ��������Collection�̓��l������s��
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return ���Collection���������ꍇ <code>true</code>
	 */
	private boolean orderedCollectionEquals(Property property, Object obj1, Object obj2) {
		Collection<?> collection1 = (Collection<?>) obj1;
		Collection<?> collection2 = (Collection<?>) obj2;
		
		if (collection1.size() != collection2.size()) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		Iterator<?> iterator1 = collection1.iterator();
		Iterator<?> iterator2 = collection2.iterator();

		boolean equals = true;
		int count = 0;
		while (iterator1.hasNext()) {
			Object next1 = iterator1.next();
			Object next2 = iterator2.next();
			
			Property newProperty = property.clone();
			newProperty.addIndex(count);
			newProperty.addObject(next1);
			
			if (!equals(newProperty, next1, next2)) {
				equals = false;
			}
			count++;
		}
		return equals;
	}

	/**
	 * Map�̓��l������s��
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return ���Map���������ꍇ <code>true</code>
	 */
	private boolean mapEquals(Property property, Object obj1, Object obj2) {
		Map<?,?> map1 = (Map<?,?>) obj1;
		Map<?,?> map2 = (Map<?,?>) obj2;
		
		if (map1.size() != map2.size()) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		boolean equals = true;

		Set<? extends Map.Entry<?,?>> entrySet1 = map1.entrySet();
		for (Entry<?, ?> entry1 : entrySet1) {
			
			Property newProperty = property.clone();
			newProperty.addName(entry1.getKey().toString());
			newProperty.addObject(entry1.getValue());
			
			Object value2 = map2.get(entry1.getKey());
			
			if (!equals(newProperty, entry1.getValue(), value2)) {
				equals = false;
			}
		}
		
		return equals;
	}

	/**
	 * Set�̓��l������s��
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return ���Set���������ꍇ <code>true</code>
	 */
	private boolean nonOrderedSetEquals(Property property, Object obj1, Object obj2) {
		Set<?> set1 = (Set<?>) obj1;
		Set<?> set2 = (Set<?>) obj2;
		
		if (set1.size() != set2.size()) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		for (Object value1 : set1) {
			if (!set2.contains(value1)) {
				addDifferenceMessage(property, obj1, obj2);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	private boolean simpleEquals(Property property, Object obj1, Object obj2) {
		if (obj1.equals(obj2)) {
			return true;
		} else {
			addDifferenceMessage(property, obj1, obj2);
		}
		return false;
	}

	/**
	 * @param property 
	 * @param method
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	private boolean invokeEquals(Property property, Method method, Object obj1, Object obj2) {
		Boolean res = null;
		method.setAccessible(true);
		
		try {
			res = (Boolean) method.invoke(obj1, obj2);
		} catch (IllegalArgumentException e) {// ���O�`�F�b�N����̂ł��肦�Ȃ�
			throw e;
		} catch (IllegalAccessException e) {
			throw (IllegalStateException) new IllegalStateException(method + " �ɃA�N�Z�X�ł��܂���B").initCause(e);
		} catch (InvocationTargetException e) {
			messageList.add(e.getCause() + " : " + property.getName() + ".equals(" + method.getParameterTypes()[0] + ")");
			return false;
		}
		if (res) {
			return true;
		} else {
			messageList.add(createDifferenceMessage(property, obj1, obj2));
			return false;
		}
	}

	/**
	 * @param klass
	 * @return �����̌^�� Object �ȊO�̑S�Ă� equals ���\�b�h
	 */
	public Method[] getOverLoadedEqualsMethods(Class<?> klass) {
		List<Method> list = new ArrayList<Method>();
		Method[] methods = klass.getDeclaredMethods();
		
		for (Method method : methods) {
			if (method.getName().equals("equals") && // ���O��equals
				method.getParameterTypes().length == 1 && // �����ЂƂ�
				method.getReturnType().equals(Boolean.TYPE) && // boolean��Ԃ�
				!method.getParameterTypes()[0].equals(Object.class) && // �����̌^�� Object �ȊO
				Modifier.isPublic(method.getModifiers())) { // public

				list.add(method);
			}
		}
		return list.toArray(new Method[list.size()]);
	}
	
	/**
	 * @param klass
	 * @return �I�[�o�[���C�h���ꂽ equals ���\�b�h
	 */
	public static Method getOverRidedEqualsMethod(Class<?> klass) {
		Method method = null;
		
		try {
			method = klass.getDeclaredMethod("equals", Object.class);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		}
		return method;
	}

	/**
	 * @param klass
	 * @return Object�������Ɏ��equals���\�b�h���g�p�\�ȏꍇ <code>true</code>
	 */
	public static boolean isAvailableSimpleEqualsMethod(Class<?> klass) {
		return hasOverRidedEqualsMethod(klass) ||
				klass.isPrimitive() ||
				Enum.class.isAssignableFrom(klass) // ���� enum �� isEnum �ɂ�����Ȃ��̂ł��̔��肪�K�v
				;
	}

	/**
	 * @param klass
	 * @return �I�[�o�[���C�h���ꂽ equals ���\�b�h�����݂���ꍇ <code>true</code>
	 */
	public static boolean hasOverRidedEqualsMethod(Class<?> klass) {
		return getOverRidedEqualsMethod(klass) != null;
	}

	private void addDifferenceMessage(Property property, Object obj1, Object obj2) {
		messageList.add(createDifferenceMessage(property, obj1, obj2));
	}

	private static String createDifferenceMessage(Property property, Object obj1, Object obj2) {
		return property.getName() + " �ɍ��ق�����܂� - [" + obj1 + "] : [" + obj2 + "]";
	}
	
	/**
	 * @return the messageList
	 */
	public List<String> getMessageList() {
		return messageList;
	}

	/**
	 * �r�[���̃v���p�e�B��\��
	 * @author Yamamoto Keita
	 */
	public static class Property implements Cloneable {
		private String name;
		private List<Object> objectList = new ArrayList<Object>();
		
		/**
		 * �R���X�g���N�^�[
		 * @param name
		 * @param obj
		 */
		public Property(String name, Object obj) {
			this.name = name;
			objectList.add(obj);
		}

		/**
		 * �R���X�g���N�^�[
		 * @param name
		 * @param objectList
		 */
		public Property(String name, List<Object> objectList) {
			this.name = name;
			this.objectList.addAll(objectList);
		}

		/**
		 * @return �v���p�e�B��
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * �v���p�e�B����ǉ�����
		 * @param name
		 */
		public void addName(String name) {
			if (this.name == null) {
				this.name = name;
			} else {
				this.name += "." + name;
			}
		}

		/**
		 * �C���f�b�N�X��ǉ�����
		 * @param i
		 */
		public void addIndex(int i) {
			name += "[" + i + "]";
		}

		/**
		 * �I�u�W�F�N�g��ǉ�����
		 * @param obj
		 */
		public void addObject(Object obj) {
			objectList.add(obj);
		}

		/**
		 * @param obj
		 * @return �Ώۂ̃I�u�W�F�N�g���d�����ăv���p�e�B�Ɋ܂ޏꍇ <code>true</code>
		 */
		public boolean overlappingContains(Object obj) {
			int count = 0;
			for (Object propertyObj : objectList) {
				if (obj == propertyObj) {
					count++;
				}
			}
			return 1 < count;
		}

		@Override
		public Property clone() {
			Property clone = null;
			try {
				clone = (Property) super.clone();
			} catch (CloneNotSupportedException e) {
				throw (IllegalStateException) new IllegalStateException().initCause(e);
			}
			
			clone.objectList = new ArrayList<Object>();
			clone.objectList.addAll(objectList);
			
			return clone;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
