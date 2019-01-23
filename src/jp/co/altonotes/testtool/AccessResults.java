package jp.co.altonotes.testtool;

import java.util.ArrayList;
import java.util.List;

/**
 * �SWEB�A�N�Z�X�̌��ʂ�ێ�����B
 *
 * @author Yamamoto Keita
 *
 */
public class AccessResults {
	private long maxTime = Long.MIN_VALUE;
	private long minTime = Long.MAX_VALUE;
	private long totalTime;
	private int errorCount;
	private int totalCount;
	private ArrayList<String> errorList = new ArrayList<String>();

	/**
	 * �A�N�Z�X���ʂ�ǉ�����B
	 *
	 * @param time
	 * @param hasError
	 * @param e
	 */
	public synchronized void addResult(long time, List<String> errors) {
		totalCount++;
		totalTime += time;

		if (time > maxTime) {
			maxTime = time;
		}
		if (time < minTime) {
			minTime = time;
		}

		if (errors.size() > 0) {
			errorCount++;
		}

		for (String error : errors) {
			errorList.add(error);
		}
	}

	/**
	 * �S�A�N�Z�X�����擾����B
	 *
	 * @return
	 */
	public int getCount() {
		return totalCount;
	}

	/**
	 * �S�Ă̌��ʂ��o�͂���B
	 */
	public synchronized void print() {
		System.out.println("�A�N�Z�X���F"+ totalCount);
		System.out.println("�G���[���F"+ errorCount);
		if (totalCount != 0) {
			System.out.println("�Œ����X�|���X���ԁF"+ maxTime + "ms");
			System.out.println("�ŒZ���X�|���X���ԁF"+ minTime + "ms");
			long average = totalTime / (long) totalCount;
			System.out.println("���σ��X�|���X���ԁF"+ average + "ms");
		}

		int limit = 100;
		for (int i = 0; i < errorList.size(); i++) {
			if (i >= limit) {
				break;
			}
			System.out.println(errorList.get(i));
		}
		if (errorList.size() > limit) {
			System.out.println("���G���[ " + (errorList.size() - limit) + "��");
		}
	}
}
