package jp.co.altonotes.webapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.exception.DuplicateConditionException;

/**
 * HTTP���N�G�X�g�ƃI�u�W�F�N�g�̃}�b�v�B
 * ���N�G�X�g�p�X�A���N�G�X�g���\�b�h�A���N�G�X�g�p�����[�^�[�ɂ��}�b�s���O���ł���B
 * �Y���Ȃ��̏ꍇ�ɓK�p�����f�t�H���g�l���w�肪�\�B
 *
 * �p�X�͖����� * �ɂ�郏�C���h�J�[�h�w��ƁA${}�ɂ��ϐ��w�肪�ł���B
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public final class RequestMap<T> {

	/** �f�t�H���g�I�u�W�F�N�g	*/
	private T defaultObject = null;

	/** �p�X�ƃI�u�W�F�N�g�̃}�b�v	*/
	private Map<String, ConditionMap<T>> pathMap = new HashMap<String, ConditionMap<T>>();

	/** ���C���h�p�X�ƃI�u�W�F�N�g�̃}�b�v	*/
	private Map<String, ConditionMap<T>> wildPathMap = new HashMap<String, ConditionMap<T>>();

	/** �ϐ��p�X�ƃI�u�W�F�N�g�̃}�b�v	*/
	private Map<VariablePath, ConditionMap<T>> variablePathMap = new HashMap<VariablePath, ConditionMap<T>>();

	/**
	 * �f�t�H���g�̃I�u�W�F�N�g���Z�b�g����B
	 *
	 * @param obj
	 */
	public synchronized void setDefault(T obj) {
		defaultObject = obj;
	}

	/**
	 *
	 * @param path
	 * @param condition
	 * @param obj
	 */
	public synchronized void add(String path, RequestCondition condition, T obj) {

		try {
			if (VariablePath.containsVariable(path)) {
				VariablePath variablePath = new VariablePath(path);
				ConditionMap<T> existing = variablePathMap.get(variablePath);

				if (existing == null) {
					ConditionMap<T> conditionMap = new ConditionMap<T>();
					conditionMap.put(condition, obj);
					variablePathMap.put(variablePath, conditionMap);
				} else {
					existing.put(condition, obj);
				}

			} else if (path.endsWith("*")) {
				path = TextUtils.rightTrim(path, '*');
				ConditionMap<T> existing = wildPathMap.get(path);

				if (existing == null) {
					ConditionMap<T> conditionMap = new ConditionMap<T>();
					conditionMap.put(condition, obj);
					wildPathMap.put(path, conditionMap);
				} else {
					existing.put(condition, obj);
				}

			} else {
				path = TextUtils.rightTrim(path, '/');
				ConditionMap<T> existing = pathMap.get(path);

				if (existing == null) {
					ConditionMap<T> conditionMap = new ConditionMap<T>();
					conditionMap.put(condition, obj);
					pathMap.put(path, conditionMap);
				} else {
					existing.put(condition, obj);
				}
			}
		} catch (DuplicateConditionException e) {
			throw new IllegalStateException(path + " �ɑ΂��ďd�����ď������Ђ��t�����܂����B\r\n" + e);
		}
	}

	/**
	 * �p�X�ƃI�u�W�F�N�g�̃}�b�v��ǉ�����B
	 *
	 * @param path
	 * @param obj
	 */
	public synchronized void add(String path, T obj) {
		add(path, null, obj);
	}

	/**
	 * ���N�G�X�g�ɑΉ�����I�u�W�F�N�g���擾����B<br>
	 * �p�X�ϐ�������ꍇ�A�p�X�ϐ���RequestInfo�ɃZ�b�g����
	 *
	 * @param req
	 * @return ���N�G�X�g�ɑΉ�����I�u�W�F�N�g
	 */
	public T get(HttpServletRequest req) {
		return get(null, req);
	}

	/**
	 * ���N�G�X�g�ɑΉ�����I�u�W�F�N�g���擾����B
	 *
	 * @param reqInfo
	 * @param req
	 * @return ���N�G�X�g�ɑΉ�����I�u�W�F�N�g
	 */
	public T get(RequestInfo reqInfo, HttpServletRequest req) {

		T obj = null;
		String path = RequestInfo.getRequestPath(req);

		// ��Ӄp�X
		ConditionMap<T> conditionMap = pathMap.get(TextUtils.rightTrim(path, '/'));
		if (conditionMap != null) {
			obj = conditionMap.get(req);
		}
		if (obj != null) {
			return obj;
		}

		// ���C���h�J�[�h�p�X
		Set<Entry<String, ConditionMap<T>>> entrySet = wildPathMap.entrySet();
		for (Entry<String, ConditionMap<T>> entry : entrySet) {
			if (path.startsWith(entry.getKey())) {
				obj = entry.getValue().get(req);
				if (obj != null) {
					return obj;
				}
			}
		}

		// �ϐ��p�X
		Set<Entry<VariablePath, ConditionMap<T>>> VariablePathSet = variablePathMap.entrySet();
		for (Entry<VariablePath, ConditionMap<T>> entry : VariablePathSet) {
			Map<String, String> variableMap = entry.getKey().getVariableMap(path);
			if (variableMap == null) {
				continue;
			}

			obj = entry.getValue().get(req);
			if (obj != null && reqInfo != null) {
				setPathVariableMap(reqInfo, variableMap);
				return obj;
			}
		}

		//�f�t�H���g
		return defaultObject;
	}

	/**
	 * Request�A�g���r���[�g�Ƀp�X�ϐ��̃}�b�v���Z�b�g����
	 *
	 * @param req
	 * @param variableMap
	 */
	private void setPathVariableMap(RequestInfo delegate, Map<String, String> variableMap) {

		Map<String, String> existingMap = delegate.getPathVariableMap();

		if (existingMap != null) {
			Set<Entry<String, String>> entrySet = variableMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				existingMap.put(entry.getKey(), entry.getValue());
			}
		} else {
			delegate.setPathVariable(variableMap);
		}
	}

	/**
	 * �}�b�v���ێ����邷�ׂẴI�u�W�F�N�g���擾����B
	 *
	 * @return �}�b�v���ێ����邷�ׂẴI�u�W�F�N�g
	 */
	public List<T> values() {
		ArrayList<T> all = new ArrayList<T>();

		Collection<ConditionMap<T>> list = pathMap.values();
		for (ConditionMap<T> conditionMap : list) {
			all.addAll(conditionMap.values());
		}

		list = wildPathMap.values();
		for (ConditionMap<T> conditionMap : list) {
			all.addAll(conditionMap.values());
		}

		list = variablePathMap.values();
		for (ConditionMap<T> conditionMap : list) {
			all.addAll(conditionMap.values());
		}

		if (defaultObject != null) {
			all.add(defaultObject);
		}

		return all;
	}
}
