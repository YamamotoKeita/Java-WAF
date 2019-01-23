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
 * Database�֌W�̃��[�e�B���e�B�[���\�b�h�Q�B
 *
 * @author Yamamoto Keita
 *
 */
public class DBUtils {

	/**
	 * SQLExecuter���N���[�Y����B
	 * null�̏ꍇ�����s�킸�ASQLException���������Ă���������B
	 * SQLExecuter���g�p����Connection, Statement, ResultSet�͑S�ăN���[�Y�����B
	 *
	 * @param exe
	 */
	public static void close(SQLExecuter exe) {
		if (exe != null) {
			exe.close();
		}
	}

	/**
	 * ResultSet���N���[�Y����B
	 * null�̏ꍇ�����s�킸�ASQLException���������Ă���������B
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
	 * Connection���N���[�Y����B
	 * null�̏ꍇ�����s�킸�ASQLException���������Ă���������B
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
	 * PreparedStatement���N���[�Y����B
	 * null�̏ꍇ�����s�킸�ASQLException���������Ă���������B
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
	 * �����̖��̂��L�[�ɂ��āAJava Environment�ɓo�^���ꂽ�f�[�^�\�[�X���擾����B<br>
	 * <code>InitialContext.lookup("java:comp/env/" + name)</code>�Ŏ擾���Ă���B
	 *
	 * @param name
	 * @return Java Environment�ɓo�^���ꂽ�f�[�^�\�[�X�B
	 * @throws NamingException
	 */
	public static DataSource getEnvironmentDataSource(String name) throws NamingException {
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("java:comp/env/" + name);
		return dataSource;
	}

	/**
	 * Java Environment�ɓo�^���ꂽ�f�[�^�\�[�X��������̖��̂��L�[�ɂ��ăR�l�N�V�������擾����B
	 *
	 * @param name
	 * @return Java Environment�ɓo�^���ꂽ�f�[�^�\�[�X����擾����Connection
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static Connection getEnvironmentConnection(String name) throws SQLException, NamingException {
		return getEnvironmentDataSource(name).getConnection();
	}
}
