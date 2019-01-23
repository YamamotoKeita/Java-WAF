package jp.co.altonotes.testtool;

import java.util.ArrayList;

import jp.co.altonotes.util.ThreadUtils;

/**
 * �y�[�W�A�����[�h���s���B
 *
 * @author Yamamoto Keita
 *
 */
public class PageLoader {
	private int accessInterval = 100;
	private int oneAccessCount = 10;
	private ArrayList<String> subURLList = new ArrayList<String>();
	private AccessResults results = new AccessResults();
	private int userCount;

	/**
	 * �y�[�W���[�h���J�n����B
	 */
	public void start() {

		ArrayList<Thread> threads = new ArrayList<Thread>(userCount);
		for (int i = 0; i < userCount; i++) {
			WebAccess access = new WebAccess(results);
			threads.add(access);
		}

		int count = 0;
		for (int i = 0; i < userCount; i++) {
			threads.get(i).start();
			count++;
			if (count >= oneAccessCount) {
				ThreadUtils.sleep(accessInterval);
				count = 0;
			}
		}

		while (results.getCount() < userCount) {
			ThreadUtils.sleep(500);
		}
	}

	/**
	 * ���ʂ��o�͂���B
	 */
	public void printResults() {
		results.print();
	}

	public void setAccessInterval(int accessInterval) {
		this.accessInterval = accessInterval;
	}

	public void setOneAccessCount(int oneAccessCount) {
		this.oneAccessCount = oneAccessCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public void addSubURL(String url) {
		this.subURLList.add(url);
	}
}
