package jp.co.altonotes.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * RAID��Ԃ��擾����
 *
 * @author Yamamoto Keita
 *
 */
public class RaidDevice {

	private String name;
	private String deviceState;
	private String raidType;
	private long size;
	private String diskNumber;
	private String diskState;

	/**
	 * �e�X�g�p���\�b�h
	 * @param args
	 */
	public static void main(String[] args) {
		RaidDevice[] devices = currentState();
		for (RaidDevice device : devices) {
			System.out.println(device);
		}
	}

	/**
	 * @param src
	 * @return �����̕���������ɂ���RAID�f�o�C�X�z��
	 */
	public static RaidDevice[] createState(String src) {

		BufferedReader r = new BufferedReader(new StringReader(src));

		try {
			r.readLine(); //1�s�X�L�b�v

			List<String[]> blockList = new ArrayList<String[]>();
			List<String> lineBlock = null;
			String line = null;

			while ((line = r.readLine()) != null) {
				line = line.trim();

				// �s������ꍇ
				if (0 < line.length()) {
					if (lineBlock == null) {
						lineBlock = new ArrayList<String>();
					}
					lineBlock.add(line);

				// ��s�̏ꍇ
				} else if (lineBlock != null) {
					String[] a = lineBlock.toArray(new String[lineBlock.size()]);
					blockList.add(a);
					lineBlock = null;
				}
			}

			List<RaidDevice> deviceList = new ArrayList<RaidDevice>();
			for (String[] lines : blockList) {
				deviceList.add(new RaidDevice(lines));
			}

			return deviceList.toArray(new RaidDevice[deviceList.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @return ���݂�RAID�f�o�C�X�z��
	 */
	public static RaidDevice[] currentState() {
		String src = CommandExecuter.exec("cat /proc/mdstat");
		return createState(src);
	}

	/**
	 * @return RAID�f�o�C�X������ȏ�ԂȂ� <code>true</code>
	 */
	public boolean isNormal() {
		return "2/2".equals(diskNumber) && "UU".equals(diskState) && "active".equals(deviceState);
	}

	/**
	 * �f�o�C�X�̏�Ԃ��S�Đ��킩���肷��
	 * @param devices
	 * @return �����Ɏw�肵���f�o�C�X���S�Đ���ȏ�ԂȂ� <code>true</code>
	 */
	public static boolean isAllNormal(RaidDevice[] devices) {
		for (RaidDevice raidDevice : devices) {
			if (!raidDevice.isNormal()) {
				return false;
			}
		}
		return true;
	}

	private RaidDevice(String[] array) {
		parse(array);
	}

	private void parse(String[] lines) {

		String[] line1 = ParseUtils.splitBySpace(lines[0]);
		this.name = line1[0];
		this.deviceState = line1[2];
		this.raidType = line1[3];

		String[] line2 = ParseUtils.splitBySpace(lines[1]);
		this.size = Long.parseLong(line2[0]);
		this.diskNumber = trim(line2[2]);
		this.diskState = trim(line2[3]);
	}

	private static String trim(String str) {
		if (str == null) {
			return null;
		}

		if (str.startsWith("[")) {
			str = str.substring(1);
		}
		if (str.endsWith("]")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return deviceState
	 */
	public String getDeviceState() {
		return deviceState;
	}

	/**
	 * @return type
	 */
	public String getRaidType() {
		return raidType;
	}

	/**
	 * @return size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return diskNumber
	 */
	public String getDiskNumber() {
		return diskNumber;
	}

	/**
	 * @return diskState
	 */
	public String getDiskState() {
		return diskState;
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + " " + raidType + " [" + deviceState + " " + diskNumber + " " + diskState + "] " + size;
	}

}
