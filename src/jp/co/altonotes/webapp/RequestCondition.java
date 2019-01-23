package jp.co.altonotes.webapp;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.util.TextUtils;

/**
 * HttpRequestの状態<br>
 * GET, POSTなどのリクエストメソッドおよび、リクエストパラメーターの値を保持し、HttpServletRequestが条件にマッチするか判定する。
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestCondition {

	/** 対象となるHTTPリクエストメソッド */
	private String targetMethod;

	/** 対象となるリクエスパラメーター */
	private Map<String, String> targetParams;

	/**
	 * コンストラクター
	 *
	 * @param targetMethod
	 * @param targetParams
	 */
	public RequestCondition(String targetMethod, Map<String, String> targetParams) {
		this.targetMethod = targetMethod;
		this.targetParams = targetParams;
	}

	/**
	 * 引数のRequestがこのConditionにマッチするか判定する
	 *
	 * @param req
	 * @return 引数のRequestが条件にマッチする場合<code>true</code>
	 */
	public boolean match(HttpServletRequest req) {
		return matchMethod(req) && matchParams(req);
	}

	/**
	 * リクエストメソッドがマッチするか判定する。
	 *
	 * @param req
	 * @return
	 */
	private boolean matchMethod(HttpServletRequest req) {
		if (targetMethod == null || targetMethod.length() == 0) {
			return true;
		}
		String requestMethod = req.getMethod();
		if (requestMethod.equalsIgnoreCase(this.targetMethod)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * リクエストパラメーターがマッチするか判定する。
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean matchParams(HttpServletRequest req) {
		if (targetParams.size() == 0) {
			return true;
		}

		Map<String, String[]> requestParams = req.getParameterMap();

		Set<Entry<String, String>> paramSet = targetParams.entrySet();
		// 検証用パラメーターを確認
		for (Entry<String, String> target : paramSet) {
			String targetKey = target.getKey();
			String targetValue = target.getValue();
			String attrString = attributeString(req, targetKey);
			
			if (!requestParams.containsKey(targetKey) && attrString == null) {
				return false;
			}
			// 値指定がない場合はOK
			if (targetValue == null) {
				continue;
			}
			
			String valueString = null;
			// アトリビュートに値があればそちらをチェック
			if (attrString != null) {
				valueString = attrString;
			}
			// アトリビュートに値がなければリクエストパラメーターをチェック
			else {
				String[] values = requestParams.get(targetKey);
				valueString = TextUtils.combine(values, ",");
			}
			
			if (!targetValue.equals(valueString)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 属性値を文字列として取得する
	 * @param req
	 * @param targetKey
	 * @return 属性値を
	 */
	private String attributeString(HttpServletRequest req, String targetKey) {
		Object attribute = req.getAttribute(targetKey);
		if (attribute != null) {
			if (attribute instanceof String) {
				return (String) attribute;
			} else if (attribute instanceof String[]) {
				return TextUtils.combine((String[])attribute, ",");
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((targetMethod == null) ? 0 : targetMethod.hashCode());
		result = prime * result
				+ ((targetParams == null) ? 0 : targetParams.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestCondition other = (RequestCondition) obj;
		if (targetMethod == null) {
			if (other.targetMethod != null)
				return false;
		} else if (!targetMethod.equals(other.targetMethod))
			return false;
		if (targetParams == null) {
			if (other.targetParams != null)
				return false;
		} else if (!targetParams.equals(other.targetParams))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[method=" + targetMethod + ", params=" + targetParams + "]";
	}


}
