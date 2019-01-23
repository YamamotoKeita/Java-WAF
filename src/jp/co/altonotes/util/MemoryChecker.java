package jp.co.altonotes.util;

import java.lang.management.ManagementFactory;

/**
 * ヒープ（メモリ）状況の確認を行う。
 *
 * @author Yamamoto Keita
 *
 */
public class MemoryChecker {

	/**
	 * Java VMが使用できるパーマネント領域の最大サイズを返す。<br>
	 * -XX:MaxPermSize=256m などでVM引数に指定される値。
	 *
	 * @return バイト数
	 */
	public static long maxPermanentGeneration() {
		//TODO 実装する
		return -1;
	}

	/**
	 * Java VMが使用できる最大メモリ量を返す。
	 * -Xmx256M などでVM引数に指定される値。
	 *
	 * @return VMが使用できる最大メモリ量
	 */
	public static long maxHeap() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
	}

	/**
	 * Java VMが使用できる最大メモリ量をメガバイト単位で返す。
	 * -Xmx256M などでVM引数に指定される値。
	 *
	 * @return VMが使用できる最大メモリ量（メガバイト単位）
	 */
	public static int maxHeapAsMegaByte() {
		return (int) (maxHeap() >> 20);
	}

	/**
	 * Java VMが起動時にOSに要求するメモリ量を返す。
	 * -Xms128M などでVM引数に指定される値。
	 *
	 * @return VMが起動時にOSに要求するメモリ量
	 */
	public static long initialHeap() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit();
	}

	/**
	 * Java VMが起動時にOSに要求するメモリ量をメガバイト単位で返す。
	 * -Xms128M などでVM引数に指定される値。
	 *
	 * @return VMが起動時にOSに要求するメモリ量（メガバイト単位）
	 */
	public static int initialHeapAsMegaByte() {
		return (int) (initialHeap() >> 20);
	}

	/**
	 * 現在使用しているメモリ量を返す。
	 *
	 * @return 現在使用しているメモリ量
	 */
	public static long usingHeap() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}

	/**
	 * 現在使用しているメモリ量をメガバイトの単位で返す。
	 *
	 * @return 現在使用しているメモリ量（メガバイト単位）
	 */
	public static int usingHeapAsMegaByte() {
		return (int) (usingHeap() >> 20);
	}

	/**
	 * 現在使用中＋使用予約済みのメモリ量を返す。
	 *
	 * @return 現在使用中＋使用予約済みのメモリ量
	 */
	public static long committedHeap() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted();
	}

	/**
	 * 現在使用中＋使用予約済みのメモリ量をメガバイト単位で返す。
	 *
	 * @return 現在使用中＋使用予約済みのメモリ量（メガバイト単位）
	 */
	public static int committedHeapAsMegaByte() {
		return (int) (committedHeap() >> 20);
	}
}
