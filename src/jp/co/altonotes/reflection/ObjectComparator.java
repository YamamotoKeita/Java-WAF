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
 * 二つのオブジェクトを比較して、差分を見る。
 * 
 * @author Yamamoto Keita
 *
 */
public class ObjectComparator {
	
	private List<String> messageList = new ArrayList<String>();
	
	/**
	 * 比較結果を出力する
	 * @param obj1
	 * @param obj2
	 */
	public static void printDiff(Object obj1, Object obj2) {
		System.out.println("----------- [" + obj1 + "] と [" + obj2 + "] を比較します");
		List<String> resultList = compare(obj1, obj2);
		for (String string : resultList) {
			System.out.println(string);
		}
	}
	
	/**
	 * 
	 * @param obj1
	 * @param obj2
	 * @return メッセージリスト
	 */
	public static List<String> compare(Object obj1, Object obj2) {
		ObjectComparator comparator = new ObjectComparator();
		boolean equals = comparator.equals(obj1, obj2);
		List<String> list = comparator.getMessageList();
		if (equals) {
			list.add("二つのオブジェクトは同値です");
		}
		return list;
	}
	
	/**
	 * 二つのBeanを比較する
	 * @param obj1
	 * @param obj2
	 * @return 二つのBeanが等しければ <code>true</code>
	 */
	public boolean equals(Object obj1, Object obj2) {
		Property property = new Property(obj1.getClass().getSimpleName(), obj1);
		return equals(property, obj1, obj2);
	}
	
	private boolean equals(Property property, Object obj1, Object obj2) {
		//System.out.println("-" + property.getName());
		
		// 同一 & null チェック
		if (obj1 == obj2) {
			return true;
		} else if (obj1 == null || obj2 == null) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		// クラスチェック
		if (!obj1.getClass().equals(obj2.getClass())) {
			addDifferenceMessage(property, obj1, obj2);
			return false;
		}
		
		// 既にチェック済みのインスタンスならスキップ
		if (property.overlappingContains(obj1)) {
			messageList.add(property.getName() + " - 同一オブジェクトが再帰的に表れたためスキップします");
			return true;
		}
		
		Class<?> klass = obj1.getClass();

		// オーバーロードされた equals メソッド
		Method[] equalsMethods = getOverLoadedEqualsMethods(klass);
		for (Method method : equalsMethods) {
			if (method.getParameterTypes()[0].isAssignableFrom(obj2.getClass())) {
				return invokeEquals(property, method, obj1, obj2);
			}
		}

		// 普通の equals メソッド
		if (isAvailableSimpleEqualsMethod(klass)) {
			return simpleEquals(property, obj1, obj2);
		}
		
		// 配列
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
	 * クラスのメンバーフィールドが等しいか判定する
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	private boolean fieldEquals(Property property, Object obj1, Object obj2) {
		boolean equals = true;
		
		Class<?> klass = obj1.getClass();
		while (klass != null && !klass.equals(Object.class)) {
			
			// オーバーロードされた equals メソッド
			Method[] equalsMethods = getOverLoadedEqualsMethods(klass);
			for (Method method : equalsMethods) {
				if (method.getParameterTypes()[0].isAssignableFrom(obj2.getClass())) {
					return equals && invokeEquals(property, method, obj1, obj2);
				}
			}
			
			// 普通の equals メソッド
			Method method = getOverRidedEqualsMethod(klass);
			if (method != null) {
				return equals && invokeEquals(property, method, obj1, obj2);
			}
			
			// フィールド比較
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
	 * @return クラスに宣言されたフィールドが二つのオブジェクトで等しい場合 <code>true</code>
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
			} catch (IllegalArgumentException e) { //事前チェックしているのでないはず
				throw e;
			} catch (IllegalAccessException e) {
				messageList.add(e + " : " + property.getName());
			}
		}
		return equals;
	}

	/**
	 * 配列の同値判定を行う
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return 二つの配列が同じなら <code>true</code>
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
	 * Listの同値判定を行う
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return 二つのListが同じなら <code>true</code>
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
	 * 順序ありCollectionの同値判定を行う
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return 二つのCollectionが等しい場合 <code>true</code>
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
	 * Mapの同値判定を行う
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return 二つのMapが等しい場合 <code>true</code>
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
	 * Setの同値判定を行う
	 * @param property
	 * @param obj1
	 * @param obj2
	 * @return 二つのSetが等しい場合 <code>true</code>
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
		} catch (IllegalArgumentException e) {// 事前チェックするのでありえない
			throw e;
		} catch (IllegalAccessException e) {
			throw (IllegalStateException) new IllegalStateException(method + " にアクセスできません。").initCause(e);
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
	 * @return 引数の型が Object 以外の全ての equals メソッド
	 */
	public Method[] getOverLoadedEqualsMethods(Class<?> klass) {
		List<Method> list = new ArrayList<Method>();
		Method[] methods = klass.getDeclaredMethods();
		
		for (Method method : methods) {
			if (method.getName().equals("equals") && // 名前がequals
				method.getParameterTypes().length == 1 && // 引数ひとつ
				method.getReturnType().equals(Boolean.TYPE) && // booleanを返す
				!method.getParameterTypes()[0].equals(Object.class) && // 引数の型が Object 以外
				Modifier.isPublic(method.getModifiers())) { // public

				list.add(method);
			}
		}
		return list.toArray(new Method[list.size()]);
	}
	
	/**
	 * @param klass
	 * @return オーバーライドされた equals メソッド
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
	 * @return Objectを引数に取るequalsメソッドが使用可能な場合 <code>true</code>
	 */
	public static boolean isAvailableSimpleEqualsMethod(Class<?> klass) {
		return hasOverRidedEqualsMethod(klass) ||
				klass.isPrimitive() ||
				Enum.class.isAssignableFrom(klass) // 無名 enum は isEnum にかからないのでこの判定が必要
				;
	}

	/**
	 * @param klass
	 * @return オーバーライドされた equals メソッドが存在する場合 <code>true</code>
	 */
	public static boolean hasOverRidedEqualsMethod(Class<?> klass) {
		return getOverRidedEqualsMethod(klass) != null;
	}

	private void addDifferenceMessage(Property property, Object obj1, Object obj2) {
		messageList.add(createDifferenceMessage(property, obj1, obj2));
	}

	private static String createDifferenceMessage(Property property, Object obj1, Object obj2) {
		return property.getName() + " に差異があります - [" + obj1 + "] : [" + obj2 + "]";
	}
	
	/**
	 * @return the messageList
	 */
	public List<String> getMessageList() {
		return messageList;
	}

	/**
	 * ビーンのプロパティを表す
	 * @author Yamamoto Keita
	 */
	public static class Property implements Cloneable {
		private String name;
		private List<Object> objectList = new ArrayList<Object>();
		
		/**
		 * コンストラクター
		 * @param name
		 * @param obj
		 */
		public Property(String name, Object obj) {
			this.name = name;
			objectList.add(obj);
		}

		/**
		 * コンストラクター
		 * @param name
		 * @param objectList
		 */
		public Property(String name, List<Object> objectList) {
			this.name = name;
			this.objectList.addAll(objectList);
		}

		/**
		 * @return プロパティ名
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * プロパティ名を追加する
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
		 * インデックスを追加する
		 * @param i
		 */
		public void addIndex(int i) {
			name += "[" + i + "]";
		}

		/**
		 * オブジェクトを追加する
		 * @param obj
		 */
		public void addObject(Object obj) {
			objectList.add(obj);
		}

		/**
		 * @param obj
		 * @return 対象のオブジェクトを重複してプロパティに含む場合 <code>true</code>
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
