package jp.co.altonotes.util;

import java.util.ArrayList;

/**
 * 時間を計測する。<br>
 * 簡易パフォーマンステスト用。
 *
 * <p>使用例</p>
 * <pre>
 * StopWatch watch = new StopWatch();
 * watch.start();
 * // 何らかの処理
 * watch.stop();
 * watch.printLastTime();
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public class StopWatch {

	private static final int UNDEFINED = -1;

	private ArrayList<Long> recordList = new ArrayList<Long>();
	private long startTime = UNDEFINED;
	private long endTime = UNDEFINED;

	/**
	 * 時間の計測を開始する。
	 */
	public void start() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * 時間の計測を終了する。
	 */
	public void stop() {
		if (startTime != UNDEFINED) {
			endTime = System.currentTimeMillis();
			recordList.add(endTime - startTime);
			startTime = UNDEFINED;
		}
	}

	/**
	 * 最後に計測した時間をミリ秒で取得する。
	 *
	 * @return 最後に計測した時間
	 */
	public long lastTime() {
		if (recordList.size() == 0) {
			throw new IllegalStateException("計測された時間が一つもありません。");
		}
		return recordList.get(recordList.size()-1);
	}

	/**
	 * 計測した全ての時間をミリ秒で取得する。
	 *
	 * @return 計測した全ての時間
	 */
	public long[] records() {
		long[] res = new long[recordList.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = recordList.get(i);
		}
		return res;
	}

	/**
	 * 計測した全ての時間をSystemに出力する
	 */
	public void printLapTime() {
		if (recordList.size() == 0) {
			throw new IllegalStateException("計測された時間が一つもありません。");
		}
		long[] records = records();
		int maxFigure = String.valueOf(records.length).length();
		for (int i = 0; i < records.length; i++) {
			String num = TextUtils.padLeft(i, maxFigure, '0');
			String time = TextUtils.padLeft(records[i], 10, ' ');
			System.out.println(num + ":" + time + "ms");
		}
	}

	/**
	 * 最後に計測した時間をSystemに出力する
	 */
	public void printLastTime() {
		if (recordList.size() == 0) {
			throw new IllegalStateException("計測された時間が一つもありません。");
		}
		System.out.println(lastTime() + "ms");
	}

	/**
	 * 計測した時間をクリアする
	 */
	public void reset() {
		recordList = new ArrayList<Long>();
	}
}
