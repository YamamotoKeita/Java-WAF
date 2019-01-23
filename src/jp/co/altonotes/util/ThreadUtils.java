package jp.co.altonotes.util;

/**
 * �X���b�h����Ɋւ��郆�[�e�B���e�B�[
 *
 * @author Yamamoto Keita
 *
 */
public class ThreadUtils {

	/**
	 * Thread���w��~���b��~����B
	 * InterruptedException���������Ă���������B
	 *
	 * @param millis
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {}
	}

	/**
	 * �X���b�h���I������܂őҋ@����B
	 * InterruptedException���������Ă���������B
	 *
	 * @param thread
	 */
	public static void join(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException ignored) {}
	}
}
