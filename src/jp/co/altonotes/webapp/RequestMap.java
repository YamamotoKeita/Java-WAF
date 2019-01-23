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
 * HTTPリクエストとオブジェクトのマップ。
 * リクエストパス、リクエストメソッド、リクエストパラメーターによるマッピングができる。
 * 該当なしの場合に適用されるデフォルト値も指定が可能。
 *
 * パスは末尾の * によるワイルドカード指定と、${}による変数指定ができる。
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public final class RequestMap<T> {

	/** デフォルトオブジェクト	*/
	private T defaultObject = null;

	/** パスとオブジェクトのマップ	*/
	private Map<String, ConditionMap<T>> pathMap = new HashMap<String, ConditionMap<T>>();

	/** ワイルドパスとオブジェクトのマップ	*/
	private Map<String, ConditionMap<T>> wildPathMap = new HashMap<String, ConditionMap<T>>();

	/** 変数パスとオブジェクトのマップ	*/
	private Map<VariablePath, ConditionMap<T>> variablePathMap = new HashMap<VariablePath, ConditionMap<T>>();

	/**
	 * デフォルトのオブジェクトをセットする。
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
			throw new IllegalStateException(path + " に対して重複して処理がひも付けられました。\r\n" + e);
		}
	}

	/**
	 * パスとオブジェクトのマップを追加する。
	 *
	 * @param path
	 * @param obj
	 */
	public synchronized void add(String path, T obj) {
		add(path, null, obj);
	}

	/**
	 * リクエストに対応するオブジェクトを取得する。<br>
	 * パス変数がある場合、パス変数をRequestInfoにセットする
	 *
	 * @param req
	 * @return リクエストに対応するオブジェクト
	 */
	public T get(HttpServletRequest req) {
		return get(null, req);
	}

	/**
	 * リクエストに対応するオブジェクトを取得する。
	 *
	 * @param reqInfo
	 * @param req
	 * @return リクエストに対応するオブジェクト
	 */
	public T get(RequestInfo reqInfo, HttpServletRequest req) {

		T obj = null;
		String path = RequestInfo.getRequestPath(req);

		// 一意パス
		ConditionMap<T> conditionMap = pathMap.get(TextUtils.rightTrim(path, '/'));
		if (conditionMap != null) {
			obj = conditionMap.get(req);
		}
		if (obj != null) {
			return obj;
		}

		// ワイルドカードパス
		Set<Entry<String, ConditionMap<T>>> entrySet = wildPathMap.entrySet();
		for (Entry<String, ConditionMap<T>> entry : entrySet) {
			if (path.startsWith(entry.getKey())) {
				obj = entry.getValue().get(req);
				if (obj != null) {
					return obj;
				}
			}
		}

		// 変数パス
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

		//デフォルト
		return defaultObject;
	}

	/**
	 * Requestアトリビュートにパス変数のマップをセットする
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
	 * マップが保持するすべてのオブジェクトを取得する。
	 *
	 * @return マップが保持するすべてのオブジェクト
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
