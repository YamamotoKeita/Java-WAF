package jp.co.altonotes.mobile;

import javax.servlet.http.HttpServletRequest;

/**
 * �g�ђ[�����N�G�X�g�w�b�_�[���B
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
	 * �R���X�g���N�^�[�B
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
	 * �R���X�g���N�^�[�B
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
	 * �R���X�g���N�^�[�B
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
	 * HttpServletRequest����C���X�^���X���쐬����B
	 *
	 * @param req
	 * @return �쐬���ꂽ<code>MobileInfo</code>�C���X�^���X
	 */
	public static MobileInfo createFromRequest(HttpServletRequest req) {
		return new MobileInfo(req);
	}

	/**
	 * UserAgent����C���X�^���X���쐬����B
	 * @param userAgent 
	 *
	 * @return �쐬���ꂽ<code>MobileInfo</code>�C���X�^���X
	 */
	public static MobileInfo createFromUserAgent(String userAgent) {
		return new MobileInfo(userAgent);
	}

	/**
	 * UserAgent�^�C�v�𔻒肷��B
	 *
	 * @param userAgent
	 * @return UserAgent�ɉ�����<code>MobileAgentType</code>
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
	 * Mozilla��UserAgent�^�C�v�𔻒肷��B
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
	 * iPhone��UserAgent�^�C�v�𔻒肷��B
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

		// �o�[�W�����̔���
		String verStr = userAgent.substring(idx, idx + 3);

		// ���W���[�o�[�W�����̎擾
		double majarVer;
		try {
			majarVer = Integer.parseInt(verStr.substring(0, 1));
		} catch (NumberFormatException e) {
			return MobileAgentType.I_PHONE4_OR_LATER; // �\�����ʃt�H�[�}�b�g�̏ꍇ�́A���V�������̂��Ɖ��肷��
		}

		// �}�C�i�[�o�[�W�����̎擾
		double minorVer;
		try {
			minorVer = Integer.parseInt(verStr.substring(2, 3));
		} catch (NumberFormatException e) {
			minorVer = 0; // �}�C�i�[�o�[�W�������\�����ʃt�H�[�}�b�g�̏ꍇ0�ɂ��Ă���
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
	 * �����̃��N�G�X�g�w�b�_�[���@��ɉ������[���ŗL��UID���擾����B
	 *
	 * @param type
	 * @param req
	 * @return �[���ŗL��UID
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
	 * ���N�G�X�g�w�b�_�Ɋ܂܂��[���ŗL��UID���擾����B<br>
	 * �w�b�_�̃p�����[�^�[����l�̃t�H�[�}�b�g�͋@��ɂ��قȂ�B<br>
	 * ���݂��Ȃ��ꍇ��<code>null</code>��Ԃ��B
	 *
	 * @return ���N�G�X�g�w�b�_�Ɋ܂܂��[���ŗL��UID�B
	 */
	public String getUID() {
		return uid;
	}

	/**
	 * @return ���[�U�[�G�[�W�F���g�B
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @return �g�у��[�U�[�G�[�W�F���g��enum
	 */
	public MobileAgentType getAgentType() {
		return agentType;
	}

	/**
	 * @return DoCoMo�@��̏ꍇ<code>true</code>
	 */
	public boolean isDocomo() {
		return agentType == MobileAgentType.DOCOMO;
	}

	/**
	 * @return SoftBank�@��̏ꍇ<code>true</code>
	 */
	public boolean isSoftBank() {
		return isSoftBank(agentType);
	}

	/**
	 * @return au�@��̏ꍇ<code>true</code>
	 */
	public boolean isAU() {
		return agentType == MobileAgentType.AU;
	}

	/**
	 * @return iPhone�̏ꍇ<code>true</code>�BOS�o�[�W�����͖��Ȃ��B
	 */
	public boolean isIPhone() {
		return agentType == MobileAgentType.I_PHONE3_OR_OLDER ||
			   agentType == MobileAgentType.I_PHONE3_1 ||
			   agentType == MobileAgentType.I_PHONE4_OR_LATER;
	}

	/**
	 * @return HTML5���T�|�[�g����o�[�W������iPhone�̏ꍇ<code>true</code>
	 */
	public boolean isIPhoneSupportingHTML5() {
		return agentType == MobileAgentType.I_PHONE3_1 ||
			   agentType == MobileAgentType.I_PHONE4_OR_LATER;
	}

	/**
	 * @return Android�̏ꍇ<code>true</code>
	 */
	public boolean isAndroid() {
		return agentType == MobileAgentType.ANDROID;
	}

	/**
	 * @return WILLCOM�@��̏ꍇ<code>true</code>
	 */
	public boolean isWillcom() {
		return agentType == MobileAgentType.WILLCOM;
	}

	/**
	 * @return ���̃N���X�ŃT�|�[�g���ꂽ���m�̋@��̏ꍇ<code>true</code>
	 */
	public boolean isKnownAgent() {
		return agentType != MobileAgentType.UNKNOWN;
	}

	/**
	 * �����̃��[�U�[�G�[�W�F���g�̃L�����A��Softbank�����肷��B<br>
	 * JPhone, Vodafone, Softbank�̏ꍇ<code>true</code>��Ԃ��B<br>
	 * �������AiPhone�͊܂܂�Ȃ��B
	 *
	 * @param agent
	 * @return �����̃��[�U�[�G�[�W�F���g��Softbank�ɑ�����ꍇ<code>true</code>
	 */
	public static boolean isSoftBank(MobileAgentType agent) {
		return agent == MobileAgentType.SOFTBANK ||
			   agent == MobileAgentType.VODAFONE ||
			   agent == MobileAgentType.J_PHONE ||
			   agent == MobileAgentType.J_PHONE_C ||
			   agent == MobileAgentType.VODAFONE_MOTOROLA;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + agentType + "][" + uid + "]" + userAgent;
	}
}
