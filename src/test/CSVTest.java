package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jp.co.altonotes.csv.CSVLine;
import jp.co.altonotes.csv.CSVReader;

import org.junit.Test;

/**
 *
 * @author Yamamoto Keita
 *
 */
public class CSVTest {

	/**
	 *
	 */
	@Test
	public void test() {
		CSVReader reader = new CSVReader("a,b,c\nd,e,f");

		for (CSVLine line : reader.lines()) {
			assertTrue(line.size() == 3);
		}

		CSVLine[] lines = reader.lines();
		assertTrue(lines.length == 2);

		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				assertEquals("a", lines[i].getItem(0));
				assertEquals("b", lines[i].getItem(1));
				assertEquals("c", lines[i].getItem(2));
			} else if (i == 1) {
				assertEquals("d", lines[i].getItem(0));
				assertEquals("e", lines[i].getItem(1));
				assertEquals("f", lines[i].getItem(2));
			} else {
				fail();
			}
		}
	}
}
