package jp.co.altonotes.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

/**
 * 構想段階。実験試作品
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public abstract class AbstractDAO<T> {

	/** DAOクラス名とDataSourceのマップ */
	private static HashMap<String, DataSource> sourceMap = new HashMap<String, DataSource>();

	/** WHERE句に指定するデフォルトのテーブル名 */
	private String defaultTable;

	private Connection connection;
	
	/**
	 * データソースをセットする
	 * @param name
	 * @param dataSource
	 */
	public static void setDataSource(String name, DataSource dataSource) {
		sourceMap.put(name, dataSource);
	}

	/**
	 * デフォルトコンストラクター
	 */
	public AbstractDAO(){}
	
	/**
	 * コンストラクター
	 * @param connection
	 */
	public AbstractDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @param sql
	 * @return 更新されたレコード数
	 */
	public int execute(SQL sql) {
		return 0;
	}

	/**
	 * @param sql
	 * @return 取得したレコード
	 */
	public T fetch(SQL sql) {
		// Connectionをcloseする。
		return null;
	}

	/**
	 * @param sql
	 * @return 取得したレコードのリスト
	 */
	public List<T> select(SQL sql) {
		// Connectionをcloseする。
		return null;
	}

}
