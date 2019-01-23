package jp.co.altonotes.mobile;

/**
 * �g�т�UserAgent�^�C�v�ꗗ
 * �ǂ�ǂ�V�����̂�������Ǝv���̂ŁA�p���I�ȍX�V���K�v
 *
 * @author Yamamoto Keita
 *
 */
public enum MobileAgentType {
	/** �s����UserAgent */
	UNKNOWN(0, "�s��"),

	/** Docomo�@���UserAgent */
	DOCOMO(1, "DoCoMo"),

	/** Android */
	ANDROID(2, "DoCoMo Android"),

	/** Softbank */
	SOFTBANK(3, "SoftBank"),

	/** Vodafone */
	VODAFONE(4, "Vodafone"),

	/** MOTOROLA�А�Vodafone */
	VODAFONE_MOTOROLA(5, "Vodafone Motorola"),

	/** JPhone */
	J_PHONE(6, "J-PHONE"),

	/** JPhone C�^ */
	J_PHONE_C(7, "J-PHONE C�^"),

	/** iPhone 3.0 �܂���3.0���O */
	I_PHONE3_OR_OLDER(8, "iPhone3.0 or older"),

	/** iPhone 3.1 */
	I_PHONE3_1(9, "iPhone3.1"),

	/** iPhone 4�ȍ~ */
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
	 * �R���X�g���N�^�[�B
	 *
	 * @param code
	 * @param name
	 */
	private MobileAgentType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * @return �R�[�h�l
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return �^�C�v�̖���
	 */
	public String getName() {
		return name;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
