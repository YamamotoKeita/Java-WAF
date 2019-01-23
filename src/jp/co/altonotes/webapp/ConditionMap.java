package jp.co.altonotes.webapp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.webapp.exception.DuplicateConditionException;

/**
 * HTTP���N�G�X�g�R���f�B�V�����ƃI�u�W�F�N�g�̃}�b�v
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public class ConditionMap<T> {

	private T defaultObject;
	private Map<RequestCondition, T> map = new HashMap<RequestCondition, T>();

	/**
	 * RequestCondition�ƃI�u�W�F�N�g�̃Z�b�g��o�^����
	 *
	 * @param condition
	 * @param obj
	 * @throws DuplicateConditionException
	 */
	public void put(RequestCondition condition, T obj) throws DuplicateConditionException {
		if (condition == null) {
			if (defaultObject == null) {
				defaultObject = obj;
			} else {
				throw new DuplicateConditionException("���Ƀf�t�H���g�̃I�u�W�F�N�g���o�^����Ă��܂��F" + obj);
			}
		} else if(map.containsKey(condition)) {
			throw new DuplicateConditionException("���ɓ���������RequestCondition���o�^����Ă��܂��F" + condition);
		} else {
			map.put(condition, obj);
		}
	}

	/**
	 * Http���N�G�X�g�Ƀ}�b�`����I�u�W�F�N�g���擾����
	 *
	 * @param req
	 * @return �����̃��N�G�X�g�ɑΉ�����I�u�W�F�N�g
	 */
	public T get(HttpServletRequest req) {
		Set<Entry<RequestCondition, T>> entrySet = map.entrySet();
		for (Entry<RequestCondition, T> entry : entrySet) {
			if (entry.getKey().match(req)) {
				return entry.getValue();
			}
		}

		if (defaultObject != null) {
			return defaultObject;
		}

		return null;
	}

	/**
	 * �ێ�����S�ẴI�u�W�F�N�g���擾����
	 *
	 * @return ���̃}�b�v���ێ�����S�ẴI�u�W�F�N�g
	 */
	public Collection<T> values() {
		Collection<T> values = map.values();
		if (defaultObject != null) {
			values.add(defaultObject);
		}
		return values;
	}
}
