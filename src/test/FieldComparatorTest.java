package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.co.altonotes.reflection.ObjectComparator;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Yamamoto Keita
 *
 */
public class FieldComparatorTest {

	@BeforeClass
	public static void init() {
		assertEquals(1, 1);
		assertTrue(true);
		assertFalse(false);
	}

	/**
	 * 
	 */
	@Test
	public void basicTest() {
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		ObjectComparator.printDiff(map1, map2);
		
		map1.put("key1", "valu1");
		map1.put("key2", "valu2");
		map1.put("key3", "valu3");

		map2.put("key3", "valu3");
		map2.put("key2", "valu2");
		map2.put("key1", "valu1");
		ObjectComparator.printDiff(map1, map2);

		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list1.add("a");
		list1.add("b");
		list2.add("a");
		list2.add("b");
		ObjectComparator.printDiff(list1, list2);

		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		set1.add("a");
		set1.add("b");
		set2.add("b");
		set2.add("a");
		ObjectComparator.printDiff(set1, set2);
	}

	/**
	 * 
	 */
	@Test
	public void beanTest() {
		SampleClass obj1 = new SampleClass();
		SampleClass obj2 = new SampleClass();
		ObjectComparator.printDiff(obj1, obj2);
		
		obj1.enum1 = SampleEnum.BBB;
		obj1.enum2 = SampleEnum2.BBB;
		obj1.str = "bbb";
		obj1.child1.enum1 = SampleEnum.BBB;
		obj1.child1.str = "bbb";
		obj1.child2.str = "ccc";
		obj1.superStr = "aaa";
		ObjectComparator.printDiff(obj1, obj2);
	}
	
	private static class SampleSuper {
		protected String superStr = "super";
	}
	
	private static class SampleClass extends SampleSuper {
		private SampleEnum enum1 = SampleEnum.AAA;
		private SampleEnum2 enum2 = SampleEnum2.AAA;
		private String str = "aaa";
		private List<String> list = new ArrayList<String>();
		private SortedSet<String> sortedSet = new TreeSet<String>();
		private Map<String, String> map = new HashMap<String, String>();
		private Set<String> set = new HashSet<String>();
		private SampleClass2 child1 = new SampleClass2();
		private SampleClass self = this;
		private SampleClass3 child2 = new SampleClass3();
	}

	private static class SampleClass2 {
		private SampleEnum enum1 = SampleEnum.AAA;
		private String str = "aaa";

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((enum1 == null) ? 0 : enum1.hashCode());
			result = prime * result + ((str == null) ? 0 : str.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SampleClass2 other = (SampleClass2) obj;
			if (enum1 != other.enum1)
				return false;
			if (str == null) {
				if (other.str != null)
					return false;
			} else if (!str.equals(other.str))
				return false;
			return true;
		}
	}

	private static class SampleClass3 {
		private String str = "aaa";

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((str == null) ? 0 : str.hashCode());
			return result;
		}

		/**
		 * @param obj
		 * @return boolean
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj.getClass() != this.getClass()) {
				return false;
			}
			
			SampleClass3 cobj = (SampleClass3)obj;
			
			if (str == null) {
				return cobj.str == null;
			} else {
				return str.equals(cobj.str);
			}
		}
	}

	private static enum SampleEnum {
		AAA,
		BBB,
		CCC,
		;
	}
	
	private static enum SampleEnum2 {
		AAA{
			@Override
			public String getCode() {
				return "1";
			}
		},
		BBB{
			@Override
			public String getCode() {
				return "2";
			}
		},
		CCC{
			@Override
			public String getCode() {
				return "3";
			}
		},
		;
		public String getCode() {
			return "";
		}
	}

}
