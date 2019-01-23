package jp.co.altonotes.webapp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.webapp.exception.DuplicateConditionException;

/**
 * HTTPリクエストコンディションとオブジェクトのマップ
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public class ConditionMap<T> {

	private T defaultObject;
	private Map<RequestCondition, T> map = new HashMap<RequestCondition, T>();

	/**
	 * RequestConditionとオブジェクトのセットを登録する
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
				throw new DuplicateConditionException("既にデフォルトのオブジェクトが登録されています：" + obj);
			}
		} else if(map.containsKey(condition)) {
			throw new DuplicateConditionException("既に同じ条件のRequestConditionが登録されています：" + condition);
		} else {
			map.put(condition, obj);
		}
	}

	/**
	 * Httpリクエストにマッチするオブジェクトを取得する
	 *
	 * @param req
	 * @return 引数のリクエストに対応するオブジェクト
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
	 * 保持する全てのオブジェクトを取得する
	 *
	 * @return このマップが保持する全てのオブジェクト
	 */
	public Collection<T> values() {
		Collection<T> values = map.values();
		if (defaultObject != null) {
			values.add(defaultObject);
		}
		return values;
	}
}
