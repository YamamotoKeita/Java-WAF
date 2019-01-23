package jp.co.altonotes.util;

import java.io.Serializable;

/**
 * 0����n�܂鐳�̐�����ID��o�^���邽�߂̃e�[�u���B<br>
 * �C�ӂ�ID���o�^�ς��𔻒肷�邽�߂Ɏg���B
 * 
 * @author Yamamoto Keita
 */
public class IDTable implements Serializable {

	private static final long serialVersionUID = 1975810691225956863L;

	private static final byte[] INSERT_BIT = {ByteUtils.binaryToByte("00000001"),
											  ByteUtils.binaryToByte("00000010"),
											  ByteUtils.binaryToByte("00000100"),
											  ByteUtils.binaryToByte("00001000"),
											  ByteUtils.binaryToByte("00010000"),
											  ByteUtils.binaryToByte("00100000"),
											  ByteUtils.binaryToByte("01000000"),
											  ByteUtils.binaryToByte("10000000")
											 };

	private static final byte[] REMOVE_BIT = {ByteUtils.binaryToByte("11111110"),
											  ByteUtils.binaryToByte("11111101"),
											  ByteUtils.binaryToByte("11111011"),
											  ByteUtils.binaryToByte("11110111"),
											  ByteUtils.binaryToByte("11101111"),
											  ByteUtils.binaryToByte("11011111"),
											  ByteUtils.binaryToByte("10111111"),
											  ByteUtils.binaryToByte("01111111")
											 };

	private byte[] table;
	private long maxId;
	
	/**
	 * �R���X�g���N�^�[
	 * @param maxId �o�^�\�ȍő�ID
	 */
	public IDTable(long maxId) {
		this.maxId = maxId;
		long arraySize = maxId / 8;
		arraySize += 1;
		
		if (Integer.MAX_VALUE < arraySize) {
			throw new IllegalArgumentException("�ő�ID���傫�����܂��F" + maxId);
		}
		
		table = new byte[(int)arraySize];
	}
	
	/**
	 * ID��o�^����
	 * @param id
	 */
	public void insert(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] |= INSERT_BIT[col];
	}
	
	/**
	 * ID���폜����
	 * @param id
	 */
	public void remove(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] &= REMOVE_BIT[col];
	}
	
	/**
	 * ID���o�^����Ă��邩���肷��
	 * @param id
	 * @return ������ID���o�^����Ă���ꍇ <code>true</code>
	 */
	public boolean contains(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		return (table[row] & INSERT_BIT[col]) != 0;
	}
	
	private void assertId(long id) {
		if (id < 0) {
			throw new IllegalArgumentException("���̐���ID�͓o�^�ł��܂���");
		} else if (maxId < id) {
			throw new IllegalArgumentException("ID���傫�����܂��F" + id);
		}
	}
}
