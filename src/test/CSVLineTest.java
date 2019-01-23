package test;

import static org.junit.Assert.assertEquals;
import jp.co.altonotes.csv.CSVLine;

import org.junit.Test;

/**
 * CSVLine‚ÌƒeƒXƒg
 * @author Yamamoto Keita
 *
 */
public class CSVLineTest {

	/**
	 *
	 */
	@Test
	public void testCSVLine() {
		String str = null;
		String[] elements = null;
		CSVLine line = null;

		str = "a,b,c";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(3, elements.length);
		assertEquals("a", elements[0]);
		assertEquals("b", elements[1]);
		assertEquals("c", elements[2]);

		str = "";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(1, elements.length);
		assertEquals("", elements[0]);

		str = ",,";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(3, elements.length);
		assertEquals("", elements[0]);
		assertEquals("", elements[1]);
		assertEquals("", elements[2]);

		str = "a,\"bbbbb\",c";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(3, elements.length);
		assertEquals("a", elements[0]);
		assertEquals("bbbbb", elements[1]);
		assertEquals("c", elements[2]);

		str = "a,\"bb,bbb\",c";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(3, elements.length);
		assertEquals("a", elements[0]);
		assertEquals("bb,bbb", elements[1]);
		assertEquals("c", elements[2]);

		str = "a,bb\"\"bbb,c";
		line = new CSVLine(str);
		elements = line.toStringArray();
		assertEquals(3, elements.length);
		assertEquals("a", elements[0]);
		assertEquals("bb\"bbb", elements[1]);
		assertEquals("c", elements[2]);
	}

}
