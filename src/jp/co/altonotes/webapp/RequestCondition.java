package jp.co.altonotes.webapp;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.util.TextUtils;

/**
 * HttpRequest�̏��<br>
 * GET, POST�Ȃǂ̃��N�G�X�g���\�b�h����сA���N�G�X�g�p�����[�^�[�̒l��ێ����AHttpServletRequest�������Ƀ}�b�`���邩���肷��B
 *
 * @author Yamamoto Keita
 *
 */
public final class RequestCondition {

	/** �ΏۂƂȂ�HTTP���N�G�X�g���\�b�h */
	private String targetMethod;

	/** �ΏۂƂȂ郊�N�G�X�p�����[�^�[ */
	private Map<String, String> targetParams;

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param targetMethod
	 * @param targetParams
	 */
	public RequestCondition(String targetMethod, Map<String, String> targetParams) {
		this.targetMethod = targetMethod;
		this.targetParams = targetParams;
	}

	/**
	 * ������Request������Condition�Ƀ}�b�`���邩���肷��
	 *
	 * @param req
	 * @return ������Request�������Ƀ}�b�`����ꍇ<code>true</code>
	 */
	public boolean match(HttpServletRequest req) {
		return matchMethod(req) && matchParams(req);
	}

	/**
	 * ���N�G�X�g���\�b�h���}�b�`���邩���肷��B
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
	 * ���N�G�X�g�p�����[�^�[���}�b�`���邩���肷��B
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
		// ���ؗp�p�����[�^�[���m�F
		for (Entry<String, String> target : paramSet) {
			String targetKey = target.getKey();
			String targetValue = target.getValue();
			String attrString = attributeString(req, targetKey);
			
			if (!requestParams.containsKey(targetKey) && attrString == null) {
				return false;
			}
			// �l�w�肪�Ȃ��ꍇ��OK
			if (targetValue == null) {
				continue;
			}
			
			String valueString = null;
			// �A�g���r���[�g�ɒl������΂�������`�F�b�N
			if (attrString != null) {
				valueString = attrString;
			}
			// �A�g���r���[�g�ɒl���Ȃ���΃��N�G�X�g�p�����[�^�[���`�F�b�N
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
	 * �����l�𕶎���Ƃ��Ď擾����
	 * @param req
	 * @param targetKey
	 * @return �����l��
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
