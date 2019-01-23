package jp.co.altonotes.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

/**
 * �\�z�i�K�B��������i
 *
 * @author Yamamoto Keita
 *
 * @param <T>
 */
public abstract class AbstractDAO<T> {

	/** DAO�N���X����DataSource�̃}�b�v */
	private static HashMap<String, DataSource> sourceMap = new HashMap<String, DataSource>();

	/** WHERE��Ɏw�肷��f�t�H���g�̃e�[�u���� */
	private String defaultTable;

	private Connection connection;
	
	/**
	 * �f�[�^�\�[�X���Z�b�g����
	 * @param name
	 * @param dataSource
	 */
	public static void setDataSource(String name, DataSource dataSource) {
		sourceMap.put(name, dataSource);
	}

	/**
	 * �f�t�H���g�R���X�g���N�^�[
	 */
	public AbstractDAO(){}
	
	/**
	 * �R���X�g���N�^�[
	 * @param connection
	 */
	public AbstractDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @param sql
	 * @return �X�V���ꂽ���R�[�h��
	 */
	public int execute(SQL sql) {
		return 0;
	}

	/**
	 * @param sql
	 * @return �擾�������R�[�h
	 */
	public T fetch(SQL sql) {
		// Connection��close����B
		return null;
	}

	/**
	 * @param sql
	 * @return �擾�������R�[�h�̃��X�g
	 */
	public List<T> select(SQL sql) {
		// Connection��close����B
		return null;
	}

}
