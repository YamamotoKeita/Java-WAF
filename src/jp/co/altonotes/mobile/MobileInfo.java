package jp.co.altonotes.mobile;

import javax.servlet.http.HttpServletRequest;

/**
 * 携帯端末リクエストヘッダー情報。
 *
 * @author Yamamoto Keita
 *
 */
public class MobileInfo {
	private static final String IPHONE_VERSION_HEADER = "CPU iPhone OS ";

	private String userAgent;
	private MobileAgentType agentType = MobileAgentType.UNKNOWN;
	private String uid;

	/**
	 * コンストラクター。
	 *
	 * @param req
	 */
	private MobileInfo(HttpServletRequest req) {
		this.userAgent = req.getHeader("User-Agent");

		if (userAgent != null) {
			this.agentType = getType(userAgent);
		}

		this.uid = getUID(agentType, req);
	}

	/**
	 * コンストラクター。
	 *
	 * @param userAgent
	 * @param uid
	 */
	public MobileInfo(String userAgent, String uid) {
		this.userAgent = userAgent;

		if (userAgent != null) {
			this.agentType = getType(userAgent);
		}

		this.uid = uid;
	}

	/**
	 * コンストラクター。
	 *
	 * @param userAgent
	 */
	public MobileInfo(String userAgent) {
		this.userAgent = userAgent;

		if (userAgent != null) {
			this.agentType = getType(userAgent);
		}
	}

	/**
	 * HttpServletRequestからインスタンスを作成する。
	 *
	 * @param req
	 * @return 作成された<code>MobileInfo</code>インスタンス
	 */
	public static MobileInfo createFromRequest(HttpServletRequest req) {
		return new MobileInfo(req);
	}

	/**
	 * UserAgentからインスタンスを作成する。
	 * @param userAgent 
	 *
	 * @return 作成された<code>MobileInfo</code>インスタンス
	 */
	public static MobileInfo createFromUserAgent(String userAgent) {
		return new MobileInfo(userAgent);
	}

	/**
	 * UserAgentタイプを判定する。
	 *
	 * @param userAgent
	 * @return UserAgentに応じた<code>MobileAgentType</code>
	 */
	public static MobileAgentType getType(String userAgent) {
		MobileAgentType type = MobileAgentType.UNKNOWN;

		if (userAgent.contains("DoCoMo/")) {
			type = MobileAgentType.DOCOMO;
		} else if (userAgent.startsWith("SoftBank/")) {
			type = MobileAgentType.SOFTBANK;
		} else if (userAgent.startsWith("KDDI")) {
			type = MobileAgentType.AU;
		} else if (userAgent.startsWith("Vodafone/")) {
			type = MobileAgentType.VODAFONE;
		} else if (userAgent.startsWith("Mozilla/")) {
			type = getMozillaType(userAgent);
		} else if (userAgent.startsWith("J-PHONE/1") || userAgent.startsWith("J-PHONE/2") || userAgent.startsWith("J-PHONE/3")) {
			type = MobileAgentType.J_PHONE_C;
		} else if (userAgent.startsWith("J-PHONE/")) {
			type = MobileAgentType.J_PHONE;
		} else if (userAgent.startsWith("UP.Browser/")) {
			type = MobileAgentType.TU_KA;
		} else if (userAgent.startsWith("MOT-V")) {
			type = MobileAgentType.VODAFONE_MOTOROLA;
		}
		return type;
	}

	/**
	 * MozillaのUserAgentタイプを判定する。
	 *
	 * @param userAgent
	 * @return
	 */
	private static MobileAgentType getMozillaType(String userAgent) {
		MobileAgentType type = MobileAgentType.UNKNOWN;
		if (userAgent.contains("iPhone")) {
			type = getiPhoneType(userAgent);
		} else if (userAgent.contains("Android")) {
			type = MobileAgentType.ANDROID;
		} else if (userAgent.contains("WILLCOM")) {
			type = MobileAgentType.WILLCOM;
		} else if (userAgent.contains("DDIPOCKET")) {
			type = MobileAgentType.AIR_EDGE_PHONE;
		}

		return type;
	}

	/**
	 * iPhoneのUserAgentタイプを判定する。
	 *
	 * @param userAgent
	 * @return
	 */
	private static MobileAgentType getiPhoneType(String userAgent) {
		//1.0	:Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3
		//3.0	:Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X;ja-jp AppleWebKit/528.18 (KHTML,like Gecko) Version/4.0 Mobile/7A341 Safari/528.16
		//3.1	:Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1 like Mac OS X; ja-jp) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7C144 Safari/528.16
		//4.0	:Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7

		MobileAgentType type = MobileAgentType.I_PHONE3_OR_OLDER;

		int idx = userAgent.indexOf(IPHONE_VERSION_HEADER);

		if (idx == -1) {
			return MobileAgentType.I_PHONE3_OR_OLDER;
		}

		idx += IPHONE_VERSION_HEADER.length();

		if (userAgent.length() < idx + 3) {
			return MobileAgentType.I_PHONE3_OR_OLDER;
		}

		// バージョンの判定
		String verStr = userAgent.substring(idx, idx + 3);

		// メジャーバージョンの取得
		double majarVer;
		try {
			majarVer = Integer.parseInt(verStr.substring(0, 1));
		} catch (NumberFormatException e) {
			return MobileAgentType.I_PHONE4_OR_LATER; // 予期せぬフォーマットの場合は、より新しいものだと仮定する
		}

		// マイナーバージョンの取得
		double minorVer;
		try {
			minorVer = Integer.parseInt(verStr.substring(2, 3));
		} catch (NumberFormatException e) {
			minorVer = 0; // マイナーバージョンが予期せぬフォーマットの場合0にしておく
		}

		double ver = majarVer + (minorVer / 10);
		if (4 <= ver) {
			type = MobileAgentType.I_PHONE4_OR_LATER;
		} else if (3.1 <= ver) {
			type = MobileAgentType.I_PHONE3_1;
		}

		return type;
	}

	/**
	 * 引数のリクエストヘッダーより機種に応じた端末固有のUIDを取得する。
	 *
	 * @param type
	 * @param req
	 * @return 端末固有のUID
	 */
	public static String getUID(MobileAgentType type, HttpServletRequest req) {
		String uid = null;
		switch (type) {
		case DOCOMO:
			uid = req.getHeader("X-DCMGUID");
			break;
		case SOFTBANK:
		case VODAFONE:
		case J_PHONE:
		case J_PHONE_C:
			uid = req.getHeader("X-JPHONE-UID");
			break;
		case AU:
			uid = req.getHeader("X-UP-SUBNO");
			break;
		}

		if (uid != null && uid.length() == 0) {
			uid = null;
		}
		return uid;
	}

	/**
	 * リクエストヘッダに含まれる端末固有のUIDを取得する。<br>
	 * ヘッダのパラメーター名や値のフォーマットは機種により異なる。<br>
	 * 存在しない場合は<code>null</code>を返す。
	 *
	 * @return リクエストヘッダに含まれる端末固有のUID。
	 */
	public String getUID() {
		return uid;
	}

	/**
	 * @return ユーザーエージェント。
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @return 携帯ユーザーエージェントのenum
	 */
	public MobileAgentType getAgentType() {
		return agentType;
	}

	/**
	 * @return DoCoMo機種の場合<code>true</code>
	 */
	public boolean isDocomo() {
		return agentType == MobileAgentType.DOCOMO;
	}

	/**
	 * @return SoftBank機種の場合<code>true</code>
	 */
	public boolean isSoftBank() {
		return isSoftBank(agentType);
	}

	/**
	 * @return au機種の場合<code>true</code>
	 */
	public boolean isAU() {
		return agentType == MobileAgentType.AU;
	}

	/**
	 * @return iPhoneの場合<code>true</code>。OSバージョンは問わない。
	 */
	public boolean isIPhone() {
		return agentType == MobileAgentType.I_PHONE3_OR_OLDER ||
			   agentType == MobileAgentType.I_PHONE3_1 ||
			   agentType == MobileAgentType.I_PHONE4_OR_LATER;
	}

	/**
	 * @return HTML5をサポートするバージョンのiPhoneの場合<code>true</code>
	 */
	public boolean isIPhoneSupportingHTML5() {
		return agentType == MobileAgentType.I_PHONE3_1 ||
			   agentType == MobileAgentType.I_PHONE4_OR_LATER;
	}

	/**
	 * @return Androidの場合<code>true</code>
	 */
	public boolean isAndroid() {
		return agentType == MobileAgentType.ANDROID;
	}

	/**
	 * @return WILLCOM機種の場合<code>true</code>
	 */
	public boolean isWillcom() {
		return agentType == MobileAgentType.WILLCOM;
	}

	/**
	 * @return このクラスでサポートされた既知の機種の場合<code>true</code>
	 */
	public boolean isKnownAgent() {
		return agentType != MobileAgentType.UNKNOWN;
	}

	/**
	 * 引数のユーザーエージェントのキャリアがSoftbankか判定する。<br>
	 * JPhone, Vodafone, Softbankの場合<code>true</code>を返す。<br>
	 * ただし、iPhoneは含まれない。
	 *
	 * @param agent
	 * @return 引数のユーザーエージェントがSoftbankに属する場合<code>true</code>
	 */
	public static boolean isSoftBank(MobileAgentType agent) {
		return agent == MobileAgentType.SOFTBANK ||
			   agent == MobileAgentType.VODAFONE ||
			   agent == MobileAgentType.J_PHONE ||
			   agent == MobileAgentType.J_PHONE_C ||
			   agent == MobileAgentType.VODAFONE_MOTOROLA;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + agentType + "][" + uid + "]" + userAgent;
	}
}
