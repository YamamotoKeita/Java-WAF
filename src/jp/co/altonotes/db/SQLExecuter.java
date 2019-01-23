package jp.co.altonotes.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.altonotes.model.DateTime;

/**
 * 変数を利用してSQLに値をバインドできるSQL実行クラス。<br>
 * <br>
 * <pre>
 * （使用例）
 *	int id = 1000;
 *	String sql = "SELECT * FROM SAMPLE_TABLE WHERE ID = ${ID}";
 *	SQLExecuter executer = new SQLExecuter(connection, sql);
 *	executer.setInt("ID", id);
 *	ResultSet result = executer.executeQuery();
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public class SQLExecuter {

	/** SQLからバインド変数を検索するための正規表現 */
	public static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^\\}]*\\}");

	private PreparedStatement statement;
	private HashMap<String, Integer> variableMap = new HashMap<String, Integer>();
	private String sourceSQL;
	private Connection connection;
	private ResultSet result;

	/**
	 * コンストラクタ。
	 *
	 * @param con
	 * @param sql
	 * @throws SQLException
	 */
	public SQLExecuter(Connection con, String sql) throws SQLException {
		connection = con;
		Matcher mtr = VARIABLE_PATTERN.matcher(sql);
		StringBuffer temp = new StringBuffer();
		int idx = 0;
		int count = 0;
		while (mtr.find()) {
			int start = mtr.start();
			int end = mtr.end();
			String key = sql.substring(start + 2, end - 1);
			variableMap.put(key, ++count);
			temp.append(sql.substring(idx, start));
			temp.append("?");
			idx = end;
		}
		temp.append(sql.substring(idx));
		sourceSQL = temp.toString();
		statement = con.prepareStatement(sourceSQL);
	}

	/**
	 * あらゆる種類の SQL 文を実行する。
	 *
	 * @return 最初の結果が ResultSet オブジェクトの場合は true。更新カウントであるか、または結果がない場合は false
	 * @throws SQLException
	 * @see PreparedStatement
	 */
	public boolean execute() throws SQLException {
		return statement.execute();
	}

	/**
	 * SQLクエリを実行し、結果を取得する。
	 *
	 * @return クエリーによって作成されたデータを含む ResultSet オブジェクト。null にはならない
	 * @throws SQLException
	 * @see PreparedStatement
	 */
	public ResultSet executeQuery() throws SQLException {
		DBUtils.close(result);
		result = statement.executeQuery();
		return result;
	}

	/**
	 * SQL データ操作言語 (DML) 文 (INSERT 文、UPDATE 文、DELETE 文など) を実行する。
	 *
	 * @throws SQLException
	 * @return NSERT 文、UPDATE 文、DELETE 文の場合は行数。何も返さない SQL 文の場合は 0
	 * @see PreparedStatement
	 */
	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	/**
	 * 保持するConnectionをcloseする。
	 */
	public void close() {
		DBUtils.close(statement);
		DBUtils.close(connection);
		DBUtils.close(result);
	}

	//*************************** Special Setter *******************************/

	/**
	 * バインド変数にnullをセットする。
	 *
	 * @param key
	 * @param type
	 * @throws SQLException
	 */
	public void setNull(String key, int type) throws SQLException {
		statement.setNull(variableMap.get(key), type);
	}

	/**
	 * VARCHAR型のバインド変数にnullをセットする。
	 *
	 * @param key
	 * @throws SQLException
	 */
	public void setNullVarchar(String key) throws SQLException {
		statement.setNull(variableMap.get(key), Types.VARCHAR);
	}

	/**
	 * INTEGER型のバインド変数にnullをセットする。
	 *
	 * @param key
	 * @throws SQLException
	 */
	public void setNullInt(String key) throws SQLException {
		statement.setNull(variableMap.get(key), Types.INTEGER);
	}

	//*************************** Setter *****************************************/

	/**
	 * 文字列型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setString(String key, String value) throws SQLException {
		statement.setString(variableMap.get(key), value);
	}

	/**
	 * 数値型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setInt(String key, int value) throws SQLException {
		statement.setInt(variableMap.get(key), value);
	}

	/**
	 * 数値型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setLong(String key, long value) throws SQLException {
		statement.setLong(variableMap.get(key), value);
	}

	/**
	 * Timestamp型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setTimeStamp(String key, Timestamp value) throws SQLException {
		statement.setTimestamp(variableMap.get(key), value);
	}

	/**
	 * Timestamp型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setTimeStamp(String key, DateTime value) throws SQLException {
		Timestamp timestamp = null;
		if (value != null) {
			timestamp = value.getTimestamp();
		}
		statement.setTimestamp(variableMap.get(key), timestamp);
	}

	/**
	 * 日付型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setDate(String key, Date value) throws SQLException {
		statement.setDate(variableMap.get(key), value);
	}

	/**
	 * 日付型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setDate(String key, DateTime value) throws SQLException {
		Date date = null;
		if (value != null) {
			date = value.getSQLDate();
		}
		statement.setDate(variableMap.get(key), date);
	}

	/**
	 * Boolean型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setBoolean(String key, boolean value) throws SQLException {
		statement.setBoolean(variableMap.get(key), value);
	}

	/**
	 * バイナリデータ型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setBytes(String key, byte[] value) throws SQLException {
		statement.setBytes(variableMap.get(key), value);
	}

	/**
	 * 浮動小数点型のバインド変数に値をセットする。
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setFloat(String key, float value) throws SQLException {
		statement.setFloat(variableMap.get(key), value);
	}

	/**
	 * LIKE検索の条件となるバインド変数にプレフィックスをセットする。<br>
	 * 検索条件は key LIKE 'prefix%' となり、キーが引数のプレフィックスで始まるレコードがヒットする。
	 *
	 * @param key
	 * @param prefix
	 * @throws SQLException
	 */
	public void setLikeSerchPrefix(String key, String prefix) throws SQLException {
		statement.setString(variableMap.get(key), escape(prefix) + "%");
	}

	/**
	 * LIKE検索の条件となるバインド変数にサフィックスをセットする。<br>
	 * 検索条件は key LIKE '%suffix' となり、キーが引数のサフィックスで終わるレコードがヒットする。
	 *
	 * @param key
	 * @param suffix
	 * @throws SQLException
	 */
	public void setLikeSerchSuffix(String key, String suffix) throws SQLException {
		statement.setString(variableMap.get(key), "%" + escape(suffix));
	}

	private String escape(String prefix) {
		prefix = prefix.replace("%", "\\%");
		prefix = prefix.replace("_", "\\_");
		return prefix;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = sourceSQL + "\n";
		return str;
	}
}
