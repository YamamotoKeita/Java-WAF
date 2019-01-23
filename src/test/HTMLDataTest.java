package test;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.co.altonotes.html.HTMLData;
import jp.co.altonotes.http.HttpHeader;
import jp.co.altonotes.io.IOUtils;

import org.junit.Test;

public class HTMLDataTest {

	@Test
	public void parseTest() throws IOException {
		byte[] data = IOUtils.loadResource(this.getClass(), "sample.html");
		HTMLData html = new HTMLData(data, "Shift_JIS");
		HttpHeader metaHeader = html.getMetaHeader();

		assertEquals("testvalue", metaHeader.getParameter("test"));
		assertEquals("euc-jp", metaHeader.getCharset());
		assertEquals(null, metaHeader.getParameter("comment"));
	}
}
