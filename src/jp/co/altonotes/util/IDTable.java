package jp.co.altonotes.util;

import java.io.Serializable;

/**
 * 0から始まる正の整数のIDを登録するためのテーブル。<br>
 * 任意のIDが登録済かを判定するために使う。
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
	 * コンストラクター
	 * @param maxId 登録可能な最大ID
	 */
	public IDTable(long maxId) {
		this.maxId = maxId;
		long arraySize = maxId / 8;
		arraySize += 1;
		
		if (Integer.MAX_VALUE < arraySize) {
			throw new IllegalArgumentException("最大IDが大きすぎます：" + maxId);
		}
		
		table = new byte[(int)arraySize];
	}
	
	/**
	 * IDを登録する
	 * @param id
	 */
	public void insert(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] |= INSERT_BIT[col];
	}
	
	/**
	 * IDを削除する
	 * @param id
	 */
	public void remove(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] &= REMOVE_BIT[col];
	}
	
	/**
	 * IDが登録されているか判定する
	 * @param id
	 * @return 引数のIDが登録されている場合 <code>true</code>
	 */
	public boolean contains(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		return (table[row] & INSERT_BIT[col]) != 0;
	}
	
	private void assertId(long id) {
		if (id < 0) {
			throw new IllegalArgumentException("負の数のIDは登録できません");
		} else if (maxId < id) {
			throw new IllegalArgumentException("IDが大きすぎます：" + id);
		}
	}
}
