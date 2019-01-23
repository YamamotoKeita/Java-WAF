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
 * �ϐ��𗘗p����SQL�ɒl���o�C���h�ł���SQL���s�N���X�B<br>
 * <br>
 * <pre>
 * �i�g�p��j
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

	/** SQL����o�C���h�ϐ����������邽�߂̐��K�\�� */
	public static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^\\}]*\\}");

	private PreparedStatement statement;
	private HashMap<String, Integer> variableMap = new HashMap<String, Integer>();
	private String sourceSQL;
	private Connection connection;
	private ResultSet result;

	/**
	 * �R���X�g���N�^�B
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
	 * �������ނ� SQL �������s����B
	 *
	 * @return �ŏ��̌��ʂ� ResultSet �I�u�W�F�N�g�̏ꍇ�� true�B�X�V�J�E���g�ł��邩�A�܂��͌��ʂ��Ȃ��ꍇ�� false
	 * @throws SQLException
	 * @see PreparedStatement
	 */
	public boolean execute() throws SQLException {
		return statement.execute();
	}

	/**
	 * SQL�N�G�������s���A���ʂ��擾����B
	 *
	 * @return �N�G���[�ɂ���č쐬���ꂽ�f�[�^���܂� ResultSet �I�u�W�F�N�g�Bnull �ɂ͂Ȃ�Ȃ�
	 * @throws SQLException
	 * @see PreparedStatement
	 */
	public ResultSet executeQuery() throws SQLException {
		DBUtils.close(result);
		result = statement.executeQuery();
		return result;
	}

	/**
	 * SQL �f�[�^���쌾�� (DML) �� (INSERT ���AUPDATE ���ADELETE ���Ȃ�) �����s����B
	 *
	 * @throws SQLException
	 * @return NSERT ���AUPDATE ���ADELETE ���̏ꍇ�͍s���B�����Ԃ��Ȃ� SQL ���̏ꍇ�� 0
	 * @see PreparedStatement
	 */
	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	/**
	 * �ێ�����Connection��close����B
	 */
	public void close() {
		DBUtils.close(statement);
		DBUtils.close(connection);
		DBUtils.close(result);
	}

	//*************************** Special Setter *******************************/

	/**
	 * �o�C���h�ϐ���null���Z�b�g����B
	 *
	 * @param key
	 * @param type
	 * @throws SQLException
	 */
	public void setNull(String key, int type) throws SQLException {
		statement.setNull(variableMap.get(key), type);
	}

	/**
	 * VARCHAR�^�̃o�C���h�ϐ���null���Z�b�g����B
	 *
	 * @param key
	 * @throws SQLException
	 */
	public void setNullVarchar(String key) throws SQLException {
		statement.setNull(variableMap.get(key), Types.VARCHAR);
	}

	/**
	 * INTEGER�^�̃o�C���h�ϐ���null���Z�b�g����B
	 *
	 * @param key
	 * @throws SQLException
	 */
	public void setNullInt(String key) throws SQLException {
		statement.setNull(variableMap.get(key), Types.INTEGER);
	}

	//*************************** Setter *****************************************/

	/**
	 * ������^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setString(String key, String value) throws SQLException {
		statement.setString(variableMap.get(key), value);
	}

	/**
	 * ���l�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setInt(String key, int value) throws SQLException {
		statement.setInt(variableMap.get(key), value);
	}

	/**
	 * ���l�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setLong(String key, long value) throws SQLException {
		statement.setLong(variableMap.get(key), value);
	}

	/**
	 * Timestamp�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setTimeStamp(String key, Timestamp value) throws SQLException {
		statement.setTimestamp(variableMap.get(key), value);
	}

	/**
	 * Timestamp�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
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
	 * ���t�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setDate(String key, Date value) throws SQLException {
		statement.setDate(variableMap.get(key), value);
	}

	/**
	 * ���t�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
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
	 * Boolean�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setBoolean(String key, boolean value) throws SQLException {
		statement.setBoolean(variableMap.get(key), value);
	}

	/**
	 * �o�C�i���f�[�^�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setBytes(String key, byte[] value) throws SQLException {
		statement.setBytes(variableMap.get(key), value);
	}

	/**
	 * ���������_�^�̃o�C���h�ϐ��ɒl���Z�b�g����B
	 *
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setFloat(String key, float value) throws SQLException {
		statement.setFloat(variableMap.get(key), value);
	}

	/**
	 * LIKE�����̏����ƂȂ�o�C���h�ϐ��Ƀv���t�B�b�N�X���Z�b�g����B<br>
	 * ���������� key LIKE 'prefix%' �ƂȂ�A�L�[�������̃v���t�B�b�N�X�Ŏn�܂郌�R�[�h���q�b�g����B
	 *
	 * @param key
	 * @param prefix
	 * @throws SQLException
	 */
	public void setLikeSerchPrefix(String key, String prefix) throws SQLException {
		statement.setString(variableMap.get(key), escape(prefix) + "%");
	}

	/**
	 * LIKE�����̏����ƂȂ�o�C���h�ϐ��ɃT�t�B�b�N�X���Z�b�g����B<br>
	 * ���������� key LIKE '%suffix' �ƂȂ�A�L�[�������̃T�t�B�b�N�X�ŏI��郌�R�[�h���q�b�g����B
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
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = sourceSQL + "\n";
		return str;
	}
}
