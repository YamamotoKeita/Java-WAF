package jp.co.altonotes.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ディレクトリ配下のファイルを再帰的に処理する。
 *
 * @author Yamamoto Keita
 *
 */
public abstract class DirectorySearcher {

	private File root;
	private List<String> targetExtentions;
	private List<String> excludedExtentions;
	private FileFilter filter;

	/**
	 * コンストラクター
	 * @param directoryPath
	 */
	public DirectorySearcher(String directoryPath) {
		root = new File(directoryPath);
	}

	/**
	 * 処理を開始する
	 * @throws IOException
	 */
	public void startProcess() throws IOException {
		searchDirectory(root);
	}

	/**
	 * ディレクトリ配下の全ファイルを再帰的に処理する。
	 *
	 * @param dir
	 * @throws IOException
	 */
	private void searchDirectory(File dir) throws IOException {
		File[] children = dir.listFiles();
		for (File file : children) {
			if (file.isDirectory()) {
				searchDirectory(file);
			} else if (isAcceptable(file)) {
				operateFile(file);
			}
		}
	}

	/**
	 * ファイルを処理する
	 *
	 * @param file
	 * @throws IOException 
	 */
	public abstract void operateFile(File file) throws IOException;

	/**
	 * 指定したファイルが処理対象か判定する。
	 *
	 * @param file
	 * @return
	 */
	private boolean isAcceptable(File file) {
		String ext = TextUtils.getExtention(file.getName());

		// 受け入れ拡張子の検証
		if (targetExtentions != null && !Checker.contains(targetExtentions, ext)) {
			return false;
		}

		// 除外拡張子の検証
		if (excludedExtentions != null && Checker.contains(excludedExtentions, ext)) {
			return false;
		}

		// フィルター
		if (filter != null && !filter.accept(file)) {
			return false;
		}

		return true;
	}

	/**
	 * 対象の拡張子をセットする。
	 *
	 * @param list
	 */
	public void setTargetExtentions(List<String> list) {
		targetExtentions = list;
	}

	/**
	 * 対象の拡張子をセットする。
	 *
	 * @param array
	 */
	public void setTargetExtentions(String[] array) {
		if (targetExtentions == null) {
			targetExtentions = new ArrayList<String>();
		}
		for (String string : array) {
			targetExtentions.add(string);
		}
	}

	/**
	 * 対象の拡張子を追加する。
	 *
	 * @param str
	 */
	public void addTargetExtention(String str) {
		if (targetExtentions == null) {
			targetExtentions = new ArrayList<String>();
		}
		targetExtentions.add(str);
	}

	/**
	 * 除外対象の拡張子をセットする。
	 *
	 * @param list
	 */
	public void setExcludedExtentions(List<String> list) {
		excludedExtentions = list;
	}

	/**
	 * 除外対象の拡張子をセットする。
	 *
	 * @param array
	 */
	public void setExcludedExtentions(String[] array) {
		if (excludedExtentions == null) {
			excludedExtentions = new ArrayList<String>();
		}
		for (String string : array) {
			excludedExtentions.add(string);
		}
	}

	/**
	 * 除外対象の拡張子を追加する。
	 *
	 * @param str
	 */
	public void addExcludedExtention(String str) {
		if (excludedExtentions == null) {
			excludedExtentions = new ArrayList<String>();
		}
		excludedExtentions.add(str);
	}

	/**
	 * フィルターをセットする。
	 *
	 * @param filter
	 */
	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	/**
	 * ルートディレクトリを取得する。
	 *
	 * @return ルートディレクトリ
	 */
	public File getRoot() {
		return root;
	}
}
