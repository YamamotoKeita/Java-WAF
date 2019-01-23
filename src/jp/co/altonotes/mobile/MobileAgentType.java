package jp.co.altonotes.mobile;

/**
 * 携帯のUserAgentタイプ一覧
 * どんどん新しいのが増えると思うので、継続的な更新が必要
 *
 * @author Yamamoto Keita
 *
 */
public enum MobileAgentType {
	/** 不明なUserAgent */
	UNKNOWN(0, "不明"),

	/** Docomo機種のUserAgent */
	DOCOMO(1, "DoCoMo"),

	/** Android */
	ANDROID(2, "DoCoMo Android"),

	/** Softbank */
	SOFTBANK(3, "SoftBank"),

	/** Vodafone */
	VODAFONE(4, "Vodafone"),

	/** MOTOROLA社製Vodafone */
	VODAFONE_MOTOROLA(5, "Vodafone Motorola"),

	/** JPhone */
	J_PHONE(6, "J-PHONE"),

	/** JPhone C型 */
	J_PHONE_C(7, "J-PHONE C型"),

	/** iPhone 3.0 または3.0より前 */
	I_PHONE3_OR_OLDER(8, "iPhone3.0 or older"),

	/** iPhone 3.1 */
	I_PHONE3_1(9, "iPhone3.1"),

	/** iPhone 4以降 */
	I_PHONE4_OR_LATER(10, "iPhone4 or later"),

	/** au */
	AU(11, "au"),

	/** TU-KA */
	TU_KA(12, "TU-KA"),

	/** WILLCOM */
	WILLCOM(13, "Willcom"),

	/** Air H" Phone */
	AIR_EDGE_PHONE(14, "Air H\" Phone");

	private int code;
	private String name;

	/**
	 * コンストラクター。
	 *
	 * @param code
	 * @param name
	 */
	private MobileAgentType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * @return コード値
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return タイプの名称
	 */
	public String getName() {
		return name;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
