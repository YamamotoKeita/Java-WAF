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

	/** ���s�ΏۂƂȂ�p�X */
	private String targetPath;
	private String targetRealPath;

	/** ���s�ΏۂƂȂ�HTTP���N�G�X�g���\�b�h */
	private String targetRequestMethod;

	/** ���s�ΏۂƂȂ郊�N�G�X�p�����[�^�[ */
	private Map<String, String> targetParams;

	private boolean hasWildPath = false;
	private boolean hasVariable = false;

	private List<String> pathArray = new ArrayList<String>();
	private List<Boolean> variableFlags = new ArrayList<Boolean>();

	/**
	 * �R���X�g���N�^�[�B
	 *
	 */
	public RequestGateway() {}

	/**
	 * �R���X�g���N�^�[�B
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
			throw new IllegalArgumentException("@Gateway���\�b�h�ɂ͈�����ݒ�ł��܂���B" + obj.getClass().getName() + "#" + method.getName());
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
	 * �p�X�̒��̕ϐ����p�[�X����B
	 *
	 * @param path
	 */
	private static boolean parsePath(String path, List<String> pathArray, List<Boolean> variableFlags){
		if (path == null) {
			throw new IllegalArgumentException("path��null�ł��B");
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
				throw new IllegalArgumentException("${}�ϐ���A�������邱�Ƃ͂ł��܂���B:" + path);
			}
			lastFlag = flag;
		}

		return hasVariable;
	}

	/**
	 * GatewayInvoker���擾����B
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
	 * ���N�G�X�g���\�b�h������Gateway�̑Ώۂ����肷��B
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
	 * ���N�G�X�g�p�����[�^�[������Gateway�̑Ώۂ����肷��B
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
	 * �p�X����ϐ���Map���擾����B
	 * �p�X������Gateway�̃t�H�[�}�b�g�ɍ��v���Ȃ��ꍇ��null��Ԃ��B
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
	 * (�� Javadoc)
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
