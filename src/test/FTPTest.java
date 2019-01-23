package test;

import java.io.IOException;
import java.net.SocketException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.altonotes.ftp.FTPClientEx;

public class FTPTest {
	public static FTPClientEx sClient1 = null;
	public static FTPClientEx sClient2 = null;

	@BeforeClass
	public static void init() throws SocketException, IOException {
		sClient1 = new FTPClientEx("upload.sepia.dti.ne.jp", "zigzag@sepia.dti.ne.jp", "oozingpin9");
		sClient2 = new FTPClientEx("buncholine.web.fc2.com", "buncholine", "sakpo398!");
	}

	@Test
	public void statusTest() throws IOException {
//		System.out.println(sClient1.getCurrentPath());
//		System.out.println(sClient2.getCurrentPath());
//
//		//DTI
//		System.out.println(sClient1.getStatus("/public_html"));
//		System.out.println(sClient1.getStatus("/public_html/BC"));
//
//		//•¶’¹
//		System.out.println(sClient2.getStatus("/"));
//		System.out.println(sClient2.getStatus("/works"));

	}

	@AfterClass
	public static void end() {
		sClient1.disconnect();
		sClient2.disconnect();
	}
}
