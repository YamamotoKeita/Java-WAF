package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jp.co.altonotes.model.IPAddress;

import org.junit.Test;

public class IPAddressTest {

	@Test
	public void contains$�T�u�l�b�g�}�X�N�w��Ȃ�() {
		IPAddress ip = new IPAddress("126.240.0.0");

		assertEquals(false, ip.match("126.239.255.254"));
		assertEquals(false, ip.match("126.239.255.255"));
		assertEquals(true, ip.match("126.240.0.0"));
		assertEquals(false, ip.match("126.240.0.1"));
		assertEquals(false, ip.match("126.240.0.2"));
	}

	@Test
	public void contains$�T�u�l�b�g�}�X�N�w�肠��() {
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
	public void contains$�T�u�l�b�g�}�X�N�w�肠��2() {
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
	public void �ő�ŏ�() {
		IPAddress ip = new IPAddress("126.240.0.0", 12);
		assertEquals("126.240.0.0", ip.min());
		assertEquals("126.255.255.255", ip.max());
	}

	@Test(expected=IllegalArgumentException.class)
	public void �R���X�g���N�^�[$�u���b�N��������() {
		new IPAddress("126.240.0.0.0");
	}

	@Test(expected=IllegalArgumentException.class)
	public void �R���X�g���N�^�[$�󕶎�() {
		new IPAddress("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void �R���X�g���N�^�[$�����p�[�X�s��() {
		new IPAddress("126.240.0.A");
	}

	@Test(expected=IllegalArgumentException.class)
	public void �R���X�g���N�^�[$���l���s��() {
		new IPAddress("126.240.0.256");
	}

	@Test(expected=IllegalArgumentException.class)
	public void �R���X�g���N�^�[with�T�u�l�b�g�}�X�N$�u���b�N��������() {
		new IPAddress("126.240.0.0.0", 12);
	}

	@Test
	public void ���[�J���z�X�g����уC���g���l�b�g�̔���() {
		assertTrue(IPAddress.isLocalHost("127.0.0.1"));
		assertFalse(IPAddress.isLocalHost("127.0.0.2"));

		assertFalse(IPAddress.isIntranet("192.167.255.255"));
		assertTrue(IPAddress.isIntranet("192.168.0.0"));
		assertTrue(IPAddress.isIntranet("192.168.255.255"));
		assertFalse(IPAddress.isIntranet("192.169.0.0"));
	}
}
