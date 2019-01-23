package jp.co.altonotes.webapp.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway�A�m�e�[�V�����̃p�����[�^�[�̃p�[�X�Ȃǂ��s�����[�e�B���e�B�[�B
 *
 * @author Yamamoto Keita
 *
 */
public class GatewayUtils {
	private static final int METHOD_ANY = 0;
	private static final int METHOD_GET = 1;
	private static final int METHOD_POST = 2;

	/**
	 * Gateway �� param �Ɏw�肵���l���� key=value �� Map ���쐬����B
	 *
	 * @param params
	 * @return ���N�G�X�g�p�����[�^�[�̃}�b�v
	 */
	public static Map<String, String> parseParams(String[] params) {
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			param = param.trim();
			if (param.length() == 0) {
				continue;
			}

			int idx = param.indexOf('=');

			String key = null;
			String value = null;

			if (idx == -1) {
				key = param;
			} else {
				key = param.substring(0, idx);
				value = param.substring(idx + 1, param.length());
			}

			map.put(key, value);
		}

		return map;
	}

	/**
	 * �������RequestMethod��int�̃R�[�h�l�ɕϊ�����
	 *
	 * @param method
	 * @return ���N�G�X�g���\�b�h�̃R�[�h�l
	 */
	public static int parseMethod(String method) {
		if (method == null || method.length() == 0) {
			return METHOD_ANY;
		} else if (method.equalsIgnoreCase("GET")) {
			return METHOD_GET;
		} else if (method.equalsIgnoreCase("POST")) {
			return METHOD_POST;
		} else {
			throw new IllegalArgumentException("method=\"" + method + "\"" + " �͕s���ł��BGateway��method�ɂ�\"GET\", \"POST\" �̂ݎw��ł��܂��B");
		}
	}

}
