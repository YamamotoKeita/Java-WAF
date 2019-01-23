package jp.co.altonotes.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * �f�B�X�N�g�p��
 *
 * @author Yamamoto Keita
 *
 */
public class DiskState {

	private String fileSystem;
	private int maxSize;
	private int useSize;
	private int freeSize;
	private int usePercentage;
	private String mountPoint;

	/**
	 * �e�X�g�p���\�b�h
	 * @param args
	 */
	public static void main(String[] args) {
		DiskState[] states = currentState();
		for (DiskState diskState : states) {
			System.out.println(diskState);
		}
	}

	/**
	 * @param src
	 * @return �����̕���������ɍ\�z�����f�B�X�N�g�p��
	 */
	public static DiskState[] createState(String src) {

		BufferedReader r = new BufferedReader(new StringReader(src));

		try {
			r.readLine(); //1�s�X�L�b�v

			List<DiskState> diskList = new ArrayList<DiskState>();
			String line = null;
			while ((line = r.readLine()) != null) {
				diskList.add(new DiskState(line));
			}

			return diskList.toArray(new DiskState[diskList.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @return ���݂̃f�B�X�N�g�p��
	 */
	public static DiskState[] currentState() {
		String src = CommandExecuter.exec("df -m");
		return createState(src);
	}

	private DiskState(String src) {
		parse(src);
	}

	private void parse(String src) {
		String[] items = ParseUtils.splitBySpace(src);
		fileSystem = items[0];
		maxSize = Integer.parseInt(items[1]);
		useSize = Integer.parseInt(items[2]);
		freeSize = Integer.parseInt(items[3]);
		usePercentage = Integer.parseInt(items[4].replace("%", ""));
		mountPoint = items[5];
	}


	/* (�� Javadoc)
	 * @see java.lang.OTeTTbject#toString()
	 */
	@Override
	public String toString() {
		String str = ParseUtils.padRight(mountPoint, 18, ' ') + " ";
		str += ParseUtils.padLeft(String.valueOf(maxSize), 9, ' ') + " ";
		str += ParseUtils.padLeft(String.valueOf(usePercentage), 3, ' ') + "%";

		return str;
	}

	/**TT
	 * @return fileSystem
	 */
	public String getFileSystem() {
		return fileSystem;
	}

	/**
	 * @return maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return useSize
	 */
	public int getUseSize() {
		return useSize;
	}

	/**
	 * @return freeSize
	 */
	public int getFreeSize() {
		return freeSize;
	}

	/**
	 * @return usePercentage
	 */
	public int getUsePercentage() {
		return usePercentage;
	}

	/**
	 * @return mountPoint
	 */
	public String getMountPoint() {
		return mountPoint;
	}

}
