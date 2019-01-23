package jp.co.altonotes.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.model.DateTime;

/**
 * �v���p�e�B�t�@�C����ǂݍ��ށB
 *
 * @author Yamamoto Keita
 *
 */
public class PropertyReader {

	private Properties properties;

	/**
	 * �R���X�g���N�^�[
	 *
	 */
	private PropertyReader() {
	}

	/**
	 * InputStream ����PropertyReader���쐬����
	 *
	 * @param in
	 * @return InputStream ����ǂݎ�����v���p�e�B
	 * @throws IOException
	 */
	public static PropertyReader load(InputStream in) throws IOException {
		Properties prop = null;

		try {
			prop  = new Properties();
			prop .load(in);

			PropertyReader reader = new PropertyReader();
			reader.properties = prop;
			return reader;
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * ���\�[�X�p�X����v���p�e�B��ǂݍ���
	 *
	 * @param path
	 * @return ���\�[�X�p�X����ǂݍ��񂾃v���p�e�B
	 * @throws IOException
	 */
	public static PropertyReader loadFromResource(String path) throws IOException {
		InputStream in = getClassLoader().getResourceAsStream(path);
		if (in == null) {
			throw new IOException(path + " ���ǂݍ��߂܂���");
		}
		return load(in);
	}

	/**
	 * ���\�[�X�p�X����v���p�e�B��ǂݍ���
	 *
	 * @param path
	 * @param klass
	 * @return ���\�[�X�p�X����v���p�e�B��ǂݍ���
	 * @throws IOException
	 */
	public static PropertyReader loadFromResource(String path, Class<?> klass) throws IOException {
		InputStream in = klass.getResourceAsStream(path);
		if (in == null) {
			throw new IOException(path + " ���ǂݍ��߂܂���");
		}
		return load(in);
	}

	private static ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Exception e) {}

		if (loader == null) {
			loader = PropertyReader.class.getClassLoader();
		}
		return loader;
	}

	/**
	 * �����Ɏw�肵��Property�̒l���擾����B
	 *
	 * @param name
	 * @return �擾����String�̃v���p�e�B�l
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * Property������Ƃ��Ď擾����B
	 *
	 * @param name
	 * @param pattern 
	 * @return �擾����DateTime�̃v���p�e�B�l
	 */
	public DateTime getDateTime(String name, String pattern) {
		String str = null;
		DateTime time = null;
		try {
			str = getProperty(name);
			time = new DateTime(pattern, str);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("property " + name + " �� " + pattern + " �`���̎����Ƃ��Ď擾�ł��܂���ł����B�F" + e.toString());
		}
		return time;
	}

	/**
	 * Property��int�Ƃ��Ď擾����B
	 *
	 * @param name
	 * @return �擾����int�̃v���p�e�B�l
	 */
	public int getInt(String name) {
		return Integer.parseInt(getProperty(name));
	}

	/**
	 * Property��float�Ƃ��Ď擾����B
	 *
	 * @param name
	 * @return �擾����float�̃v���p�e�B�l
	 */
	public float getFloat(String name) {
		return Float.parseFloat(getProperty(name));
	}

	/**
	 * Property��boolean�Ƃ��Ď擾����B
	 *
	 * @param name
	 * @return �擾����boolean�̃v���p�e�B�l
	 */
	public boolean getBoolean(String name) {
		return Boolean.valueOf(getProperty(name));
	}
}
