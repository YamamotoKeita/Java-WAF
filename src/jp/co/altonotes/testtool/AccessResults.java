package jp.co.altonotes.testtool;

import java.util.ArrayList;
import java.util.List;

/**
 * 全WEBアクセスの結果を保持する。
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
	 * アクセス結果を追加する。
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
	 * 全アクセス数を取得する。
	 *
	 * @return
	 */
	public int getCount() {
		return totalCount;
	}

	/**
	 * 全ての結果を出力する。
	 */
	public synchronized void print() {
		System.out.println("アクセス数："+ totalCount);
		System.out.println("エラー数："+ errorCount);
		if (totalCount != 0) {
			System.out.println("最長レスポンス時間："+ maxTime + "ms");
			System.out.println("最短レスポンス時間："+ minTime + "ms");
			long average = totalTime / (long) totalCount;
			System.out.println("平均レスポンス時間："+ average + "ms");
		}

		int limit = 100;
		for (int i = 0; i < errorList.size(); i++) {
			if (i >= limit) {
				break;
			}
			System.out.println(errorList.get(i));
		}
		if (errorList.size() > limit) {
			System.out.println("他エラー " + (errorList.size() - limit) + "件");
		}
	}
}
