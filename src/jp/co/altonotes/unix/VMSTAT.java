package jp.co.altonotes.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * vmstat �R�}���h�̏o�̓p�[�T�[<br>
 * OS�ɂ���ďo�͂��قȂ�̂Ŕėp�I�ȓ���͕ۏ؂��Ȃ��B
 *
 * <p>�Ή�OS</p>
 * <ul>
 * <li>CentOS5.5
 * </ul>
 * @author Yamamoto Keita
 *
 */
public class VMSTAT {

	private Map<String, Long> map = new LinkedHashMap<String, Long>();

	/**
	 * �e�X�g�p���\�b�h
	 * @param args
	 */
	public static void main(String[] args) {
		VMSTAT stat = currentState();

		System.out.println("mem free:" + stat.freeMemory());
		System.out.println("cpu use:" + stat.cpuUsage());
		System.out.println("swap amount:" + stat.swapAmount());
	}

	/**
	 *
	 * @param src
	 * @return ���������ɍ\�z�����T�[�o�[�p�t�H�[�}���X���
	 */
	public static VMSTAT createState(String src) {
		VMSTAT vmstat = new VMSTAT();
		vmstat.parse(src);
		return vmstat;
	}

	/**
	 * @return ���݂̃T�[�o�[�p�t�H�[�}���X���
	 */
	public static VMSTAT currentState() {
		String src = CommandExecuter.exec("vmstat -a -S m");
		return createState(src);
	}

	/**
	 * �󂫃������ʂ��擾����
	 *
	 * @return �󂫃�������
	 */
	public long freeMemory() {
		long free = map.get("free");
		long inact = map.get("inact");
		return free + inact;
	}

	/**
	 * �X���b�v�T�C�Y���擾����B
	 * @return �X���b�v�T�C�Y
	 */
	public long swapAmount() {
		return map.get("swpd");
	}

	/**
	 * �X���b�v�A�E�g���������Ă��邩���肷��B<br>
	 * ����������f�B�X�N�ɏ������񂾃f�[�^�ʂ�����B
	 *
	 * @return �X���b�v�A�E�g���������Ă���ꍇ <code>true</code>
	 */
	public boolean hasSwapOut() {
		long l = map.get("so");
		return 0 < l;
	}

	/**
	 * @return �X���b�v�A�E�g�̗�
	 */
	public long swapOut() {
		return map.get("so");
	}

	/**
	 * CPU�g�p���i100 - �A�C�h�����j���擾����
	 * @return CPU�g�p��
	 */
	public int cpuUsage() {
		long id = map.get("id");
		return 100 - (int)id;
	}

	/**
	 * @return �ҋ@�v���Z�X��
	 */
	public int waitingProcess() {
		return (int) (long) map.get("r");
	}

	/**
	 * @return �u���b�N���ꂽ�v���Z�X��
	 */
	public int blockedProcess() {
		return (int) (long) map.get("b");
	}

	private void parse(String src) {
		BufferedReader r = new BufferedReader(new StringReader(src));

		try {
			r.readLine(); // 1�s�ڂ͂Ƃ΂�
			String header = r.readLine();
			if (header == null) {
				throw new IllegalArgumentException("�s���ǂݍ��߂܂���ł����B");
			}
			String[] titles = ParseUtils.splitBySpace(header);
			String body = r.readLine();
			if (body == null) {
				throw new IllegalArgumentException("�s���ǂݍ��߂܂���ł����B");
			}
			String[] fields = ParseUtils.splitBySpace(body);

			if (titles.length != fields.length) {
				throw new IllegalArgumentException("�w�b�_�ƃf�[�^�̐����قȂ�܂�:" + src);
			}

			for (int i = 0; i < titles.length; i++) {
				long val = Long.parseLong(fields[i]);
				map.put(titles[i], val);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
