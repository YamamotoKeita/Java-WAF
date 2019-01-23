package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.util.Checker;

import org.junit.Test;

/**
 * Checkerのテスト
 *
 * @author Yamamoto Keita
 *
 */
public class CheckerTest {

	/**
	 * 
	 */
	@Test
	public void containsTest() {
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("a");
		list.add("abc");
		list.add("111");

		String[] array = list.toArray(new String[list.size()]);

		assertEquals(true, Checker.contains(list, ""));
		assertEquals(true, Checker.contains(list, "abc"));
		assertEquals(true, Checker.contains(list, "111"));
		assertEquals(false, Checker.contains(list, "def"));
		assertEquals(false, Checker.contains(list, "ABC"));
		assertEquals(false, Checker.contains(list, null));

		assertEquals(true, Checker.contains(array, ""));
		assertEquals(true, Checker.contains(array, "abc"));
		assertEquals(true, Checker.contains(array, "111"));
		assertEquals(false, Checker.contains(array, "def"));
		assertEquals(false, Checker.contains(array, "ABC"));
		assertEquals(false, Checker.contains(list, null));

		assertEquals(true, Checker.containsIgnoreCase(list, ""));
		assertEquals(true, Checker.containsIgnoreCase(list, "abc"));
		assertEquals(true, Checker.containsIgnoreCase(list, "111"));
		assertEquals(false, Checker.containsIgnoreCase(list, "def"));
		assertEquals(true, Checker.containsIgnoreCase(list, "ABC"));
		assertEquals(false, Checker.containsIgnoreCase(list, null));

		assertEquals(true, Checker.containsIgnoreCase(array, ""));
		assertEquals(true, Checker.containsIgnoreCase(array, "abc"));
		assertEquals(true, Checker.containsIgnoreCase(array, "111"));
		assertEquals(false, Checker.containsIgnoreCase(array, "def"));
		assertEquals(true, Checker.containsIgnoreCase(array, "ABC"));
		assertEquals(false, Checker.containsIgnoreCase(array, null));
	}

	/**
	 * 
	 */
	@Test
	public void blank() {
		String str = null;

		assertEquals(true, Checker.isEmpty(str));
		assertEquals(true, Checker.isEmpty(""));
		assertEquals(false, Checker.isEmpty(" "));

		assertEquals(false, Checker.isNotEmpty(str));
		assertEquals(false, Checker.isNotEmpty(""));
		assertEquals(true, Checker.isNotEmpty(" "));

		assertEquals(true, Checker.isBlank(str));
		assertEquals(true, Checker.isBlank(""));
		assertEquals(true, Checker.isBlank(" "));
		assertEquals(true, Checker.isBlank("　"));
		assertEquals(false, Checker.isBlank("a"));
	}
}
