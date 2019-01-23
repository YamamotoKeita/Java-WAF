package test;

import static org.junit.Assert.assertEquals;
import jp.co.altonotes.unix.DiskState;

import org.junit.Test;

/**
 *
 * @author Yamamoto Keita
 *
 */
public class DiskStateTest {

	private String src = "Filesystem           1M-ブロック    使用   使用可 使用% マウント位置\r\n" +
						  "/dev/md3                 94274      6313     83095   8% /\r\n" +
						  "/dev/md2                  4841       138      4453   4% /home\r\n" +
						  "/dev/md1                   145        12       126   9% /boot\r\n" +
						  "tmpfs                     3959         0      3959   0% /dev/shm";

	/**
	 *
	 */
	@Test
	public void test() {
		DiskState[] disks = DiskState.createState(src);
		assertEquals(4, disks.length);

		assertEquals("/dev/md3", disks[0].getFileSystem());
		assertEquals(94274, disks[0].getMaxSize());
		assertEquals(6313, disks[0].getUseSize());
		assertEquals(83095, disks[0].getFreeSize());
		assertEquals(8, disks[0].getUsePercentage());
		assertEquals("/", disks[0].getMountPoint());
	}

}
