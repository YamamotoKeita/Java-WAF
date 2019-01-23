package jp.co.altonotes.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * �f�B���N�g���z���̃t�@�C�����ċA�I�ɏ�������B
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
	 * �R���X�g���N�^�[
	 * @param directoryPath
	 */
	public DirectorySearcher(String directoryPath) {
		root = new File(directoryPath);
	}

	/**
	 * �������J�n����
	 * @throws IOException
	 */
	public void startProcess() throws IOException {
		searchDirectory(root);
	}

	/**
	 * �f�B���N�g���z���̑S�t�@�C�����ċA�I�ɏ�������B
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
	 * �t�@�C������������
	 *
	 * @param file
	 * @throws IOException 
	 */
	public abstract void operateFile(File file) throws IOException;

	/**
	 * �w�肵���t�@�C���������Ώۂ����肷��B
	 *
	 * @param file
	 * @return
	 */
	private boolean isAcceptable(File file) {
		String ext = TextUtils.getExtention(file.getName());

		// �󂯓���g���q�̌���
		if (targetExtentions != null && !Checker.contains(targetExtentions, ext)) {
			return false;
		}

		// ���O�g���q�̌���
		if (excludedExtentions != null && Checker.contains(excludedExtentions, ext)) {
			return false;
		}

		// �t�B���^�[
		if (filter != null && !filter.accept(file)) {
			return false;
		}

		return true;
	}

	/**
	 * �Ώۂ̊g���q���Z�b�g����B
	 *
	 * @param list
	 */
	public void setTargetExtentions(List<String> list) {
		targetExtentions = list;
	}

	/**
	 * �Ώۂ̊g���q���Z�b�g����B
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
	 * �Ώۂ̊g���q��ǉ�����B
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
	 * ���O�Ώۂ̊g���q���Z�b�g����B
	 *
	 * @param list
	 */
	public void setExcludedExtentions(List<String> list) {
		excludedExtentions = list;
	}

	/**
	 * ���O�Ώۂ̊g���q���Z�b�g����B
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
	 * ���O�Ώۂ̊g���q��ǉ�����B
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
	 * �t�B���^�[���Z�b�g����B
	 *
	 * @param filter
	 */
	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	/**
	 * ���[�g�f�B���N�g�����擾����B
	 *
	 * @return ���[�g�f�B���N�g��
	 */
	public File getRoot() {
		return root;
	}
}
