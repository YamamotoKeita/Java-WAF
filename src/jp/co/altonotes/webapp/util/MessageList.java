package jp.co.altonotes.webapp.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ���b�Z�[�W�̃��X�g��ێ�����N���X�B
 *
 * @author Yamamoto Keita
 *
 */
public final class MessageList implements Iterable<String> {

	private ArrayList<String> list = new ArrayList<String>();
	private String prefix = "<li>";
	private String suffix = "</li>";
	private String header;
	private String footer;

	/**
	 * ���b�Z�[�W��ǉ�����B
	 *
	 * @param message
	 */
	public void add(String message) {
		list.add(message);
	}

	/**
	 * ���b�Z�[�W��ێ����Ă��邩���肷��B
	 *
	 * @return ���b�Z�[�W��ێ����Ă���ꍇ<code>true</code>
	 */
	public boolean hasMessage() {
		if (0 < list.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return ���b�Z�[�W�̐�
	 */
	public int size() {
		return list.size();
	}

	/**
	 * ���b�Z�[�W��S�č폜����
	 */
	public void clear() {
		list.clear();
	}
	
	/**
	 * @param msg
	 * @return �����̃��b�Z�[�W���Z�b�g����Ă���� <code>true</code>
	 */
	public boolean contains(String msg) {
		return list.contains(msg);
	}
	
	/**
	 * @return �ʂ̃��b�Z�[�W�̐擪�ɕt�^���镶����
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix �ʂ̃��b�Z�[�W�̐擪�ɕt�^���镶����
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return �ʂ̃��b�Z�[�W�̖����ɕt�^���镶����
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix �ʂ̃��b�Z�[�W�̖����ɕt�^���镶����
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return ���b�Z�[�W���X�g�S�̂̐擪�ɕt�^���镶����
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header ���b�Z�[�W���X�g�S�̂̐擪�ɕt�^���镶����
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return ���b�Z�[�W���X�g�S�̖̂����ɕt�^���镶����
	 */
	public String getFooter() {
		return footer;
	}

	/**
	 * @param footer ���b�Z�[�W���X�g�S�̖̂����ɕt�^���镶����
	 */
	public void setFooter(String footer) {
		this.footer = footer;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder(100);

		if (header != null) {
			temp.append(header + "\r\n");
		}

		for (String message : list) {
			if (prefix != null) {
				temp.append(prefix);
			}

			temp.append(message);

			if (suffix != null) {
				temp.append(suffix);
			}
			temp.append("\r\n");
		}

		if (footer != null) {
			temp.append(footer);
		}

		return temp.toString();
	}

	/**
	 * @return ���b�Z�[�W�� iterator
	 */
	public Iterator<String> iterator() {
		return list.iterator();
	}

}
