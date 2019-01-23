package jp.co.altonotes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Database関係のユーティリティーメソッド群。
 *
 * @author Yamamoto Keita
 *
 */
public class DBUtils {

	/**
	 * SQLExecuterをクローズする。
	 * nullの場合何も行わず、SQLExceptionが発生しても無視する。
	 * SQLExecuterが使用したConnection, Statement, ResultSetは全てクローズされる。
	 *
	 * @param exe
	 */
	public static void close(SQLExecuter exe) {
		if (exe != null) {
			exe.close();
		}
	}

	/**
	 * ResultSetをクローズする。
	 * nullの場合何も行わず、SQLExceptionが発生しても無視する。
	 *
	 * @param rs
	 */
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Connectionをクローズする。
	 * nullの場合何も行わず、SQLExceptionが発生しても無視する。
	 *
	 * @param con
	 */
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * PreparedStatementをクローズする。
	 * nullの場合何も行わず、SQLExceptionが発生しても無視する。
	 *
	 * @param statement
	 */
	public static void close(PreparedStatement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 引数の名称をキーにして、Java Environmentに登録されたデータソースを取得する。<br>
	 * <code>InitialContext.lookup("java:comp/env/" + name)</code>で取得している。
	 *
	 * @param name
	 * @return Java Environmentに登録されたデータソース。
	 * @throws NamingException
	 */
	public static DataSource getEnvironmentDataSource(String name) throws NamingException {
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("java:comp/env/" + name);
		return dataSource;
	}

	/**
	 * Java Environmentに登録されたデータソースから引数の名称をキーにしてコネクションを取得する。
	 *
	 * @param name
	 * @return Java Environmentに登録されたデータソースから取得したConnection
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static Connection getEnvironmentConnection(String name) throws SQLException, NamingException {
		return getEnvironmentDataSource(name).getConnection();
	}
}
