package jp.co.altonotes.webapp.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Gatewayアノテーションのパラメーターのパースなどを行うユーティリティー。
 *
 * @author Yamamoto Keita
 *
 */
public class GatewayUtils {
	private static final int METHOD_ANY = 0;
	private static final int METHOD_GET = 1;
	private static final int METHOD_POST = 2;

	/**
	 * Gateway の param に指定した値から key=value の Map を作成する。
	 *
	 * @param params
	 * @return リクエストパラメーターのマップ
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
	 * 文字列のRequestMethodをintのコード値に変換する
	 *
	 * @param method
	 * @return リクエストメソッドのコード値
	 */
	public static int parseMethod(String method) {
		if (method == null || method.length() == 0) {
			return METHOD_ANY;
		} else if (method.equalsIgnoreCase("GET")) {
			return METHOD_GET;
		} else if (method.equalsIgnoreCase("POST")) {
			return METHOD_POST;
		} else {
			throw new IllegalArgumentException("method=\"" + method + "\"" + " は不正です。Gatewayのmethodには\"GET\", \"POST\" のみ指定できます。");
		}
	}

}
