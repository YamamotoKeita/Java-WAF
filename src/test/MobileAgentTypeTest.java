package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.mobile.MobileAgentType;
import jp.co.altonotes.mobile.MobileInfo;

import org.junit.BeforeClass;
import org.junit.Test;


public class MobileAgentTypeTest {
	@BeforeClass
	public static void init() {
	}

	@Test
	public void コード値チェック() {
		MobileAgentType[] types = MobileAgentType.values();

		List<Integer> codeList = new ArrayList<Integer>();
		for (MobileAgentType type : types) {
			for (Integer existingCode : codeList) {
				if (type.getCode() == existingCode) {
					fail("重複したコードがあります：" + existingCode);
				}
			}
			codeList.add(type.getCode());
		}
	}

	@Test
	public void docomoの判定() {
		String ua = "DoCoMo/2.0 F900i(c100;TB;W22H12)";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.DOCOMO, type);
	}

	@Test
	public void auの判定() {
		String ua = "KDDI-SA31 UP.Browser/6.2.0.7.3.129 (GUI) MMP/2.0";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.AU, type);
	}

	@Test
	public void softbankの判定() {
		String ua = "SoftBank/1.0/842P/PJP10 Widgets/Widgets/1.0";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.SOFTBANK, type);
	}

	@Test
	public void iPhone1の判定() {
		// 1.0
		String ua = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE3_OR_OLDER, type);
	}

	@Test
	public void iPhone3の判定() {
		// 3.0
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X;ja-jp AppleWebKit/528.18 (KHTML,like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE3_OR_OLDER, type);
	}

	@Test
	public void iPhone3_1の判定() {
		// 3.1
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1 like Mac OS X; ja-jp) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7C144 Safari/528.16";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE3_1, type);
	}

	@Test
	public void iPhone4の判定() {
		// 4.0
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE4_OR_LATER, type);
	}

	@Test
	public void iPhone5_1の判定() {
		// 5.0
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE4_OR_LATER, type);
	}

	@Test
	public void iPhone変なバージョン() {
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS A_B like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE4_OR_LATER, type);
	}

	@Test
	public void iPhone4マイナーバージョンだけ変() {
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_X like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE4_OR_LATER, type);
	}

	@Test
	public void iPhone3マイナーバージョンだけ変() {
		String ua = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_X like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.I_PHONE3_OR_OLDER, type);
	}

	@Test
	public void アンドロイドの判定() {
		String ua = "Mozilla/5.0 (Linux; U; Android 1.0; en-us; dream) AppleWebKit/525.10+ (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2";
		MobileAgentType type = MobileInfo.getType(ua);
		assertEquals(MobileAgentType.ANDROID, type);
	}

}
