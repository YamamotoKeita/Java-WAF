package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jp.co.altonotes.model.IPAddress;

import org.junit.Test;

public class IPAddressTest {

	@Test
	public void contains$サブネットマスク指定なし() {
		IPAddress ip = new IPAddress("126.240.0.0");

		assertEquals(false, ip.match("126.239.255.254"));
		assertEquals(false, ip.match("126.239.255.255"));
		assertEquals(true, ip.match("126.240.0.0"));
		assertEquals(false, ip.match("126.240.0.1"));
		assertEquals(false, ip.match("126.240.0.2"));
	}

	@Test
	public void contains$サブネットマスク指定あり() {
		IPAddress ip = new IPAddress("126.240.0.0", 12);

		assertEquals(false, ip.match("126.239.255.254"));
		assertEquals(false, ip.match("126.239.255.255"));
		assertEquals(true, ip.match("126.240.0.0"));
		assertEquals(true, ip.match("126.240.0.1"));
		assertEquals(true, ip.match("126.250.0.0"));
		assertEquals(true, ip.match("126.255.255.254"));
		assertEquals(true, ip.match("126.255.255.255"));
		assertEquals(false, ip.match("127.0.0.0"));
		assertEquals(false, ip.match("127.0.0.1"));
	}

	@Test
	public void contains$サブネットマスク指定あり2() {
		IPAddress ip = new IPAddress("126.224.0.0", 11);

		assertEquals(false, ip.match("126.223.255.254"));
		assertEquals(false, ip.match("126.223.255.255"));
		assertEquals(true, ip.match("126.224.0.0"));
		assertEquals(true, ip.match("126.224.0.1"));
		assertEquals(true, ip.match("126.250.0.0"));
		assertEquals(true, ip.match("126.255.255.254"));
		assertEquals(true, ip.match("126.255.255.255"));
		assertEquals(false, ip.match("127.0.0.0"));
		assertEquals(false, ip.match("127.0.0.1"));
	}

	@Test
	public void 最大最小() {
		IPAddress ip = new IPAddress("126.240.0.0", 12);
		assertEquals("126.240.0.0", ip.min());
		assertEquals("126.255.255.255", ip.max());
	}

	@Test(expected=IllegalArgumentException.class)
	public void コンストラクター$ブロック数が多い() {
		new IPAddress("126.240.0.0.0");
	}

	@Test(expected=IllegalArgumentException.class)
	public void コンストラクター$空文字() {
		new IPAddress("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void コンストラクター$数字パース不可() {
		new IPAddress("126.240.0.A");
	}

	@Test(expected=IllegalArgumentException.class)
	public void コンストラクター$数値が不正() {
		new IPAddress("126.240.0.256");
	}

	@Test(expected=IllegalArgumentException.class)
	public void コンストラクターwithサブネットマスク$ブロック数が多い() {
		new IPAddress("126.240.0.0.0", 12);
	}

	@Test
	public void ローカルホストおよびイントラネットの判定() {
		assertTrue(IPAddress.isLocalHost("127.0.0.1"));
		assertFalse(IPAddress.isLocalHost("127.0.0.2"));

		assertFalse(IPAddress.isIntranet("192.167.255.255"));
		assertTrue(IPAddress.isIntranet("192.168.0.0"));
		assertTrue(IPAddress.isIntranet("192.168.255.255"));
		assertFalse(IPAddress.isIntranet("192.169.0.0"));
	}
}
