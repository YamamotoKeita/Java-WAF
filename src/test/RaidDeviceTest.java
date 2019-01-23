package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jp.co.altonotes.unix.RaidDevice;

import org.junit.Test;

/**
 *
 * @author Yamamoto Keita
 *
 */
public class RaidDeviceTest {

	private String src = "Personalities : [raid1]\r\n" +
						  "md1 : active raid1 sdb1[1] sda1[0]\r\n" +
						  "      152512 blocks [2/2] [UU]\r\n" +
						  "\r\n" +
						  "md0 : active raid1 sdb2[1] sda2[0]\r\n" +
						  "      12289600 blocks [2/2] [UU]\r\n" +
						  "\r\n" +
						  "md2 : active raid1 sdb3[1] sda3[0]\r\n" +
						  "      5116608 blocks [2/2] [UU]\r\n" +
						  "\r\n" +
						  "md3 : inactive raid1 sdb4[1] sda4[0]\r\n" +
						  "      99659136 blocks [2/1] [UD]\r\n" +
						  "\r\n" +
						  "unused devices: <none>\r\n";

	/**
	 *
	 */
	@Test
	public void test() {
		RaidDevice[] raids = RaidDevice.createState(src);

		assertEquals(4, raids.length);

		assertEquals("md1", raids[0].getName());
		assertEquals(152512, raids[0].getSize());
		assertEquals("raid1", raids[0].getRaidType());
		assertEquals("active", raids[0].getDeviceState());
		assertEquals("2/2", raids[0].getDiskNumber());
		assertEquals("UU", raids[0].getDiskState());
		assertTrue(raids[0].isNormal());

		assertEquals("md3", raids[3].getName());
		assertEquals(99659136, raids[3].getSize());
		assertEquals("raid1", raids[3].getRaidType());
		assertEquals("inactive", raids[3].getDeviceState());
		assertEquals("2/1", raids[3].getDiskNumber());
		assertEquals("UD", raids[3].getDiskState());
		assertFalse(raids[3].isNormal());
	}
}
