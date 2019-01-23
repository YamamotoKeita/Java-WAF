package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jp.co.altonotes.util.IDTable;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Yamamoto Keita
 *
 */
public class IDTableTest {
	
	/**
	 * 
	 */
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
	public void createTest() {
		IDTable table = new IDTable(1000);
		
		for (int i = 0; i <= 1000; i++) {
			assertFalse(table.contains(i));
		}
	}

	/**
	 * 
	 */
	@Test
	public void insertTest() {
		IDTable table = new IDTable(1000);
		
		for (int i = 0; i <= 1000; i++) {
			table.insert(i);
			assertTrue(table.contains(i));
		}
	}

	/**
	 * 
	 */
	@Test
	public void containsTest() {
		
		IDTable table = new IDTable(1000);
		long[] ids = {1, 4, 123, 328, 627, 1000};
		
		for (int i = 0; i < ids.length; i++) {
			table.insert(ids[i]);
		}
		
		for (int i = 0; i <= 1000; i++) {
			if (isInList(ids, i)) {
				assertTrue(table.contains(i));
			} else {
				assertFalse(table.contains(i));
			}
		}
	}
	
	private static boolean isInList(long[] list, long id) {
		for (long l : list) {
			if (id == l) {
				return true;
			}
		}
		return false;
	}

}
