package jp.co.altonotes.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.util.Checker;

/**
 * ${} �`���̕ϐ����܂ރp�X
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
	 * �R���X�g���N�^�[
	 *
	 * @param path
	 */
	public VariablePath(String path) {
		this.sourcePath = path;
		parsePath(path);
	}

	/**
	 * �����̃p�X���ϐ����܂ނ����肷��
	 *
	 * @param path
	 * @return �����̃p�X���ϐ����܂ޏꍇ<code>true</code>
	 */
	public static boolean containsVariable(String path) {
		Matcher mtr = VARIABLE_PATTERN.matcher(path);
		return mtr.find();
	}

	/**
	 * �p�X�̒��̕ϐ����p�[�X����B
	 *
	 * @param path
	 */
	private void parsePath(String path){
		if (path == null || path.length() == 0) {
			throw new IllegalArgumentException("path��null�܂��͋󕶎��ł��B");
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
				throw new IllegalArgumentException("${}�ϐ���A�������邱�Ƃ͂ł��܂���B:" + path);
			}
			lastFlag = flag;
		}
	}


	/**
	 * �����Ɏw�肵���p�X����ϐ���Map���擾����B
	 * �w�肵���p�X�����̃p�X�̃t�H�[�}�b�g�ɍ��v���Ȃ��ꍇ��null��Ԃ��B
	 *
	 * @param path
	 * @return �����̃p�X����ɑΉ�����A�p�X�ϐ���Map
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
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sourcePath;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return sourcePath.hashCode();
	}

	/*
	 * (�� Javadoc)
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
