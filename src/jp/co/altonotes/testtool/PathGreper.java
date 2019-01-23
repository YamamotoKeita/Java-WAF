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
 * �v���W�F�N�g���̃p�X�̋L�q�𔲂��o���B
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
	 * �R���X�g���N�^�[
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
	 * ���X�g���o�͂���B
	 */
	public void printList() {
		for (String str : list) {
			System.out.println(str);
		}
	}

	/**
	 * �w��p�^�[���̑����l�����X�g�ɒǉ�����B
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
	 * �t�@�C���̃��[�g�f�B���N�g������̑��΃p�X���擾����B
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
	 * Attribute���瑮���𔲂��o���B
	 *
	 * @param attribute
	 * @return
	 */
	private String parseValue(String attribute) {
		String value = attribute.substring(attribute.indexOf('"') + 1, attribute.lastIndexOf('"'));
		return value;
	}

}
