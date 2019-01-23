package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import jp.co.altonotes.unix.VMSTAT;

import org.junit.Test;

/**
 *
 * @author Yamamoto Keita
 *
 */
public class VMSTATTest {

	private String src = "procs -----------memory---------- ---swap-- -----io---- --system-- -----cpu------\r\n" +
						  " r  b   swpd   free  inact active   si   so    bi    bo   in   cs us sy id wa st\r\n" +
						  " 7  6    123   7424    420    409    0    0     0     0    3    4  0  0  38  0  0";

	/**
	 *
	 */
	@Test
	public void test() {
		VMSTAT vmstat = VMSTAT.createState(src);

		assertEquals(7, vmstat.waitingProcess());
		assertEquals(6, vmstat.blockedProcess());
		assertEquals(62, vmstat.cpuUsage());
		assertEquals(7844, vmstat.freeMemory());
		assertFalse(vmstat.hasSwapOut());
		assertEquals(123, vmstat.swapAmount());
		assertEquals(0, vmstat.swapOut());
	}
}
