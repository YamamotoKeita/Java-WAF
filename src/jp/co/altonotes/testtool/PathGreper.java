package jp.co.altonotes.testtool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.util.DirectorySearcher;

/**
 * プロジェクト内のパスの記述を抜き出す。
 *
 * @author Yamamoto Keita
 *
 */
public class PathGreper extends DirectorySearcher {
	private static final Pattern PATTERN_HREF = Pattern.compile("href=\"[^\"]*\"");
	private static final Pattern PATTERN_ACTION = Pattern.compile("action=\"[^\"]*\"");
	private static final Pattern PATTERN_SRC = Pattern.compile("src=\"[^\"]*\"");

	private final static String[] EXCLUDED_EXTENTIONS = {"jar", "doc", "zip", "png", "jpg", "gif", "class", "ico", "css"};

	private List<String> list = new ArrayList<String>();

	/**
	 * コンストラクター
	 *
	 * @param directoryPath
	 */
	public PathGreper(String directoryPath) {
		super(directoryPath);
		setExcludedExtentions(EXCLUDED_EXTENTIONS);
	}

	@Override
	public void operateFile(File file) throws IOException {
		String text = IOUtils.readString(file, "UTF-8");

		addValues(PATTERN_HREF, text, file);
		addValues(PATTERN_ACTION, text, file);
		addValues(PATTERN_SRC, text, file);
	}

	/**
	 * リストを出力する。
	 */
	public void printList() {
		for (String str : list) {
			System.out.println(str);
		}
	}

	/**
	 * 指定パターンの属性値をリストに追加する。
	 *
	 * @param pattern
	 * @param text
	 */
	private void addValues(Pattern pattern, String text, File file) {
		Matcher mtr = pattern.matcher(text);
		while (mtr.find()) {
			String value = parseValue(mtr.group());
			boolean exists = false;
			for (String existing : list) {
				if (existing.equals(value)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				list.add(value + "\t" + getReletivePath(file));
			}
		}
	}

	/**
	 * ファイルのルートディレクトリからの相対パスを取得する。
	 *
	 * @param file
	 * @return
	 */
	public String getReletivePath(File file) {
		String absPath = file.getAbsolutePath();
		String rootPath = getRoot().getAbsolutePath();
		if (absPath.startsWith(rootPath)) {
			return absPath.substring(rootPath.length());
		} else {
			return absPath;
		}
	}

	/**
	 * Attributeから属性を抜き出す。
	 *
	 * @param attribute
	 * @return
	 */
	private String parseValue(String attribute) {
		String value = attribute.substring(attribute.indexOf('"') + 1, attribute.lastIndexOf('"'));
		return value;
	}

}
