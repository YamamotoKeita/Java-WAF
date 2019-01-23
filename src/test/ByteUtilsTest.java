package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jp.co.altonotes.util.ByteUtils;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Yamamoto Keita
 *
 */
public class ByteUtilsTest {
	
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
	public void binaryStringTest() {
		byte b = 1;
		assertEquals("00000001", ByteUtils.toBinaryString(b));
		
		b = 2;
		assertEquals("00000010", ByteUtils.toBinaryString(b));
		
		b = 4;
		assertEquals("00000100", ByteUtils.toBinaryString(b));
		
		b = (byte)128;
		assertEquals("10000000", ByteUtils.toBinaryString(b));
	}

	/**
	 * 
	 */
	@Test
	public void binaryToByteTest() {
		byte b = 1;
		assertEquals(b, ByteUtils.binaryToByte("00000001"));
		
		b = 2;
		assertEquals(b, ByteUtils.binaryToByte("00000010"));
		
		b = 4;
		assertEquals(b, ByteUtils.binaryToByte("00000100"));
		
		b = (byte)128;
		assertEquals(b, ByteUtils.binaryToByte("10000000"));
	}

}
