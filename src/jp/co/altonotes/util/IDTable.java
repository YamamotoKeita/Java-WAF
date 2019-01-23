package jp.co.altonotes.util;

import java.io.Serializable;

/**
 * 0‚©‚çn‚Ü‚é³‚Ì®”‚ÌID‚ğ“o˜^‚·‚é‚½‚ß‚Ìƒe[ƒuƒ‹B<br>
 * ”CˆÓ‚ÌID‚ª“o˜^Ï‚©‚ğ”»’è‚·‚é‚½‚ß‚Ég‚¤B
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
	 * ƒRƒ“ƒXƒgƒ‰ƒNƒ^[
	 * @param maxId “o˜^‰Â”\‚ÈÅ‘åID
	 */
	public IDTable(long maxId) {
		this.maxId = maxId;
		long arraySize = maxId / 8;
		arraySize += 1;
		
		if (Integer.MAX_VALUE < arraySize) {
			throw new IllegalArgumentException("Å‘åID‚ª‘å‚«‚·‚¬‚Ü‚·F" + maxId);
		}
		
		table = new byte[(int)arraySize];
	}
	
	/**
	 * ID‚ğ“o˜^‚·‚é
	 * @param id
	 */
	public void insert(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] |= INSERT_BIT[col];
	}
	
	/**
	 * ID‚ğíœ‚·‚é
	 * @param id
	 */
	public void remove(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		table[row] &= REMOVE_BIT[col];
	}
	
	/**
	 * ID‚ª“o˜^‚³‚ê‚Ä‚¢‚é‚©”»’è‚·‚é
	 * @param id
	 * @return ˆø”‚ÌID‚ª“o˜^‚³‚ê‚Ä‚¢‚éê‡ <code>true</code>
	 */
	public boolean contains(long id) {
		assertId(id);
		
		int row = (int) (id / 8);
		int col = (int)(id % 8);
		
		return (table[row] & INSERT_BIT[col]) != 0;
	}
	
	private void assertId(long id) {
		if (id < 0) {
			throw new IllegalArgumentException("•‰‚Ì”‚ÌID‚Í“o˜^‚Å‚«‚Ü‚¹‚ñ");
		} else if (maxId < id) {
			throw new IllegalArgumentException("ID‚ª‘å‚«‚·‚¬‚Ü‚·F" + id);
		}
	}
}
