package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jp.co.altonotes.util.TextUtils;

import org.junit.Test;

/**
 * TextUtilsのテスト
 *
 * @author Yamamoto Keita
 *
 */
public class TextUtilsTest {
	/**
	 *
	 */
	@Test
	public void getExtentionTest() {
		assertEquals(true, TextUtils.getExtention("aaa.ext").equals("ext"));
	}

	/**
	 *
	 */
	@Test
	public void combineTest() {
		String[] strs = {"a", "b", "c"};
		String combined = TextUtils.combine(strs, ",");
		assertEquals(true, combined.equals("a,b,c"));

		strs = new String[]{"a"};
		combined = TextUtils.combine(strs, ",");
		assertEquals(true, combined.equals("a"));

		strs = new String[]{"a", "b"};
		combined = TextUtils.combine(strs, "");
		assertEquals(true, combined.equals("ab"));

		strs = new String[]{"a", "b", "c"};
		combined = TextUtils.combine(strs, ",,");
		assertEquals(true, combined.equals("a,,b,,c"));
	}

	/**
	 *
	 */
	@Test
	public void padRight() {
		String res = TextUtils.padRight("123", 6, '0');
		assertEquals("123000", res);

		res = TextUtils.padRight("123", 3, '0');
		assertEquals("123", res);

		res = TextUtils.padRight("123", 2, '0');
		assertEquals("12", res);
	}

	/**
	 *
	 */
	@Test
	public void splitBySpaceTest() {
		String src = "111 222   333";
		String[] res = TextUtils.splitBySpace(src);
		assertEquals(3, res.length);
		assertEquals("111", res[0]);
		assertEquals("222", res[1]);
		assertEquals("333", res[2]);

		src = "  111 222   333";
		res = TextUtils.splitBySpace(src);
		assertEquals(3, res.length);
		assertEquals("111", res[0]);
		assertEquals("222", res[1]);
		assertEquals("333", res[2]);

		src = "111 222   333  ";
		res = TextUtils.splitBySpace(src);
		assertEquals(3, res.length);
		assertEquals("111", res[0]);
		assertEquals("222", res[1]);
		assertEquals("333", res[2]);

		src = "1";
		res = TextUtils.splitBySpace(src);
		assertEquals(1, res.length);
		assertEquals("1", res[0]);

		src = "     ";
		res = TextUtils.splitBySpace(src);
		assertEquals(0, res.length);

		src = "";
		res = TextUtils.splitBySpace(src);
		assertEquals(0, res.length);
	}

	/**
	 *
	 */
	@Test
	public void statckTraceTest() {
		String str = TextUtils.stackTraceToString(new IllegalStateException("最初のエラー").initCause(new IOException()));

		assertNotNull(str);
		assertTrue(0 < str.length());
		assertTrue(str.startsWith("java.lang.IllegalStateException: 最初のエラー"));
		assertTrue(str.contains("Caused by: java.io.IOException"));
	}
}
