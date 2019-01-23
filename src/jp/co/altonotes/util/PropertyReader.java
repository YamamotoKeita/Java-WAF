package jp.co.altonotes.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.model.DateTime;

/**
 * プロパティファイルを読み込む。
 *
 * @author Yamamoto Keita
 *
 */
public class PropertyReader {

	private Properties properties;

	/**
	 * コンストラクター
	 *
	 */
	private PropertyReader() {
	}

	/**
	 * InputStream からPropertyReaderを作成する
	 *
	 * @param in
	 * @return InputStream から読み取ったプロパティ
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
	 * リソースパスからプロパティを読み込む
	 *
	 * @param path
	 * @return リソースパスから読み込んだプロパティ
	 * @throws IOException
	 */
	public static PropertyReader loadFromResource(String path) throws IOException {
		InputStream in = getClassLoader().getResourceAsStream(path);
		if (in == null) {
			throw new IOException(path + " が読み込めません");
		}
		return load(in);
	}

	/**
	 * リソースパスからプロパティを読み込む
	 *
	 * @param path
	 * @param klass
	 * @return リソースパスからプロパティを読み込む
	 * @throws IOException
	 */
	public static PropertyReader loadFromResource(String path, Class<?> klass) throws IOException {
		InputStream in = klass.getResourceAsStream(path);
		if (in == null) {
			throw new IOException(path + " が読み込めません");
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
	 * 引数に指定したPropertyの値を取得する。
	 *
	 * @param name
	 * @return 取得したStringのプロパティ値
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * Propertyを日時として取得する。
	 *
	 * @param name
	 * @param pattern 
	 * @return 取得したDateTimeのプロパティ値
	 */
	public DateTime getDateTime(String name, String pattern) {
		String str = null;
		DateTime time = null;
		try {
			str = getProperty(name);
			time = new DateTime(pattern, str);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("property " + name + " が " + pattern + " 形式の時刻として取得できませんでした。：" + e.toString());
		}
		return time;
	}

	/**
	 * Propertyをintとして取得する。
	 *
	 * @param name
	 * @return 取得したintのプロパティ値
	 */
	public int getInt(String name) {
		return Integer.parseInt(getProperty(name));
	}

	/**
	 * Propertyをfloatとして取得する。
	 *
	 * @param name
	 * @return 取得したfloatのプロパティ値
	 */
	public float getFloat(String name) {
		return Float.parseFloat(getProperty(name));
	}

	/**
	 * Propertyをbooleanとして取得する。
	 *
	 * @param name
	 * @return 取得したbooleanのプロパティ値
	 */
	public boolean getBoolean(String name) {
		return Boolean.valueOf(getProperty(name));
	}
}
