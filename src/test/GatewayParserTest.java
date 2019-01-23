package test;

import static org.junit.Assert.*;

import java.util.Map;

import jp.co.altonotes.webapp.annotation.GatewayUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public class GatewayParserTest {
	@BeforeClass
	public static void init() {
	}

	@Test
	public void name�݂̂̃p�[�X() {
		String[] param = {"name"};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(1, map.size());
		assertEquals(true, map.containsKey("name"));
		assertEquals(null, map.get("name"));
	}

	@Test
	public void name��value�̃p�[�X() {
		String[] param = {"name=value"};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(1, map.size());
		assertEquals("value", map.get("name"));
	}

	@Test
	public void name��value�����̃p�[�X() {
		String[] param = {"name1=value1", "name2=value2", "name3=value3"};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(3, map.size());
		assertEquals("value1", map.get("name1"));
		assertEquals("value2", map.get("name2"));
		assertEquals("value3", map.get("name3"));
	}

	@Test
	public void name��value���������p�X�y�[�X����̃p�[�X() {
		String param[] = {"name1=value1", "name2=value2", "name3=value3"};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(3, map.size());
		assertEquals("value1", map.get("name1"));
		assertEquals("value2", map.get("name2"));
		assertEquals("value3", map.get("name3"));
	}

	@Test
	public void name��value�����name�݂̂̕��������p�X�y�[�X����̃p�[�X() {
		String param[] = {"name1", "name2=value2", "name3=value3"};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(3, map.size());
		assertEquals(null, map.get("name1"));
		assertEquals("value2", map.get("name2"));
		assertEquals("value3", map.get("name3"));
	}

	@Test
	public void �󕶎�param�̃p�[�X() {
		String param[] = {""};
		Map<String, String> map = GatewayUtils.parseParams(param);

		assertEquals(0, map.size());
	}

}
