package jp.co.altonotes.util;

/**
 * スレッド操作に関するユーティリティー
 *
 * @author Yamamoto Keita
 *
 */
public class ThreadUtils {

	/**
	 * Threadを指定ミリ秒停止する。
	 * InterruptedExceptionが発生しても無視する。
	 *
	 * @param millis
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {}
	}

	/**
	 * スレッドが終了するまで待機する。
	 * InterruptedExceptionが発生しても無視する。
	 *
	 * @param thread
	 */
	public static void join(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException ignored) {}
	}
}
