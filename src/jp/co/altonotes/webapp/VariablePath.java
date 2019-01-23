package jp.co.altonotes.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.util.Checker;

/**
 * ${} 形式の変数を含むパス
 * ex. /account/${id}
 *
 * @author Yamamoto Keita
 *
 */
public final class VariablePath {

	private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^\\}]*\\}");

	private String sourcePath;
	private List<String> pathArray = new ArrayList<String>();
	private List<Boolean> variableFlags = new ArrayList<Boolean>();

	/**
	 * コンストラクター
	 *
	 * @param path
	 */
	public VariablePath(String path) {
		this.sourcePath = path;
		parsePath(path);
	}

	/**
	 * 引数のパスが変数を含むか判定する
	 *
	 * @param path
	 * @return 引数のパスが変数を含む場合<code>true</code>
	 */
	public static boolean containsVariable(String path) {
		Matcher mtr = VARIABLE_PATTERN.matcher(path);
		return mtr.find();
	}

	/**
	 * パスの中の変数をパースする。
	 *
	 * @param path
	 */
	private void parsePath(String path){
		if (path == null || path.length() == 0) {
			throw new IllegalArgumentException("pathがnullまたは空文字です。");
		}

		int idx = 0;
		int start = 0;
		int variableStart;
		int variableEnd;

		Matcher mtr = VARIABLE_PATTERN.matcher(path);

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
	}


	/**
	 * 引数に指定したパスから変数のMapを取得する。
	 * 指定したパスがこのパスのフォーマットに合致しない場合はnullを返す。
	 *
	 * @param path
	 * @return 引数のパスからに対応する、パス変数のMap
	 */
	public Map<String, String> getVariableMap(String path) {
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

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sourcePath;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return sourcePath.hashCode();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariablePath) {
			VariablePath compared = (VariablePath) obj;
			return sourcePath.equals(compared.sourcePath);
		} else {
			return false;
		}
	}
}
