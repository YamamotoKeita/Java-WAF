package webapp;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.util.TextUtils;
import jp.co.altonotes.webapp.handler.SimpleMethodInvoker;

public class RequestGateway {

	public static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^\\}]*\\}");

	private Object obj;
	private Method method;

	/** 実行対象となるパス */
	private String targetPath;
	private String targetRealPath;

	/** 実行対象となるHTTPリクエストメソッド */
	private String targetRequestMethod;

	/** 実行対象となるリクエスパラメーター */
	private Map<String, String> targetParams;

	private boolean hasWildPath = false;
	private boolean hasVariable = false;

	private List<String> pathArray = new ArrayList<String>();
	private List<Boolean> variableFlags = new ArrayList<Boolean>();

	/**
	 * コンストラクター。
	 *
	 */
	public RequestGateway() {}

	/**
	 * コンストラクター。
	 *
	 * @param obj
	 * @param method
	 * @param targetPath
	 * @param targetRequestMethod
	 * @param targetParams
	 */
	public RequestGateway(Object obj, Method method, String targetPath, String targetRequestMethod, Map<String, String> targetParams) {
		Class<?>[] argTypes = method.getParameterTypes();
		if (argTypes.length != 0) {
			throw new IllegalArgumentException("@Gatewayメソッドには引数を設定できません。" + obj.getClass().getName() + "#" + method.getName());
		}

		this.obj = obj;
		this.method = method;
		this.targetRequestMethod = targetRequestMethod;
		this.targetParams = targetParams;

		setPath(targetPath);
	}

	public void setPath(String path) {
		this.targetPath = path;
		this.targetRealPath = path;

		if (path.endsWith("*")) {
			this.hasWildPath = true;
			this.targetRealPath = path.substring(0, path.length() - 1);
		} else if (path.endsWith("/")) {
			this.targetRealPath = TextUtils.rightTrim(path, '/');
		}

		hasVariable = parsePath(targetRealPath, pathArray, variableFlags);

	}

	/**
	 * パスの中の変数をパースする。
	 *
	 * @param path
	 */
	private static boolean parsePath(String path, List<String> pathArray, List<Boolean> variableFlags){
		if (path == null) {
			throw new IllegalArgumentException("pathがnullです。");
		}

		int idx = 0;
		int start = 0;
		int variableStart;
		int variableEnd;

		Matcher mtr = VARIABLE_PATTERN.matcher(path);
		boolean hasVariable = false;

		while (mtr.find()) {
			variableStart = mtr.start();
			variableEnd = mtr.end();

			String pathFragment = path.substring(start, variableStart);
			if (Checker.isNotEmpty(pathFragment)) {
				pathArray.add(pathFragment);
				variableFlags.add(false);
			}

			String variableName = path.substring(variableStart + 2, variableEnd - 1);
			pathArray.add(variableName);
			variableFlags.add(true);
			hasVariable = true;
			idx++;

			start = variableEnd;
		}

		String lastPath = path.substring(start);
		if (Checker.isNotEmpty(lastPath)) {
			pathArray.add(lastPath);
			variableFlags.add(false);
		}

		boolean lastFlag = false;
		for (boolean flag : variableFlags) {
			if (lastFlag && flag) {
				throw new IllegalArgumentException("${}変数を連続させることはできません。:" + path);
			}
			lastFlag = flag;
		}

		return hasVariable;
	}

	/**
	 * GatewayInvokerを取得する。
	 *
	 * @param path
	 * @param req
	 * @param resp
	 * @return
	 */
	public SimpleMethodInvoker getInvoker(String path, HttpServletRequest req, HttpServletResponse resp) {
		return new SimpleMethodInvoker(obj, method);
	}

	/**
	 * リクエストメソッドがこのGatewayの対象か判定する。
	 *
	 * @param req
	 * @return
	 */
	public boolean isMatchingMethod(HttpServletRequest req) {
		if (Checker.isEmpty(targetRequestMethod)) {
			return true;
		}
		String requestMethod = req.getMethod();
		if (requestMethod.equalsIgnoreCase(this.targetRequestMethod)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * リクエストパラメーターがこのGatewayの対象か判定する。
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isMatchingParams(HttpServletRequest req) {
		if (targetParams.size() == 0) {
			return true;
		}

		Map<String, String[]> requestParams = req.getParameterMap();

		Set<Entry<String, String>> paramSet = targetParams.entrySet();
		for (Entry<String, String> entry : paramSet) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!requestParams.containsKey(key)) {
				return false;
			}
			if (value == null) {
				continue;
			}
			String[] values = requestParams.get(key);
			String valueString = TextUtils.combine(values, ",");
			if (!value.equals(valueString)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * パスから変数のMapを取得する。
	 * パスがこのGatewayのフォーマットに合致しない場合はnullを返す。
	 *
	 * @param path
	 * @return
	 */
	public Map<String, String> getPathVariableMap(String path) {
		Map<String, String> map = new HashMap<String, String>();
		int start = 0;
		int end = 0;

		String variable = null;
		for (int i = 0; i < pathArray.size() - 1; i++) {
			if (variableFlags.get(i)) {
				end = path.indexOf(pathArray.get(i + 1), start);
				if (end == -1) {
					return null;
				}
				variable = path.substring(start, end);
				map.put(pathArray.get(i), variable);
				start = end;
			} else {
				if (path.indexOf(pathArray.get(i), start) == start) {
					start += pathArray.get(i).length();
				} else {
					return null;
				}
			}
		}

		int lastIndex = variableFlags.size() - 1;
		if (variableFlags.get(lastIndex)) {
			map.put(pathArray.get(lastIndex), path.substring(start, path.length()));
		} else if(path.indexOf(pathArray.get(lastIndex), start) != start) {
			return null;
		}

		return map;
	}

	public boolean hasWildPath() {
		return hasWildPath;
	}

	public boolean hasVariable() {
		return hasVariable;
	}

	public String getPath() {
		return this.targetPath;
	}

	public String getRealPath() {
		return targetRealPath;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "[MethodGateway]" + obj.getClass().getName() + "#" + method.getName() + "\n";
		str += "path=" + targetPath + "\n";
		str += "method=" + targetRequestMethod + "\n";
		str += "params=" + targetParams;
		return str;
	}
}
