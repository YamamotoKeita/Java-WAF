package jp.co.altonotes.unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * vmstat コマンドの出力パーサー<br>
 * OSによって出力が異なるので汎用的な動作は保証しない。
 *
 * <p>対応OS</p>
 * <ul>
 * <li>CentOS5.5
 * </ul>
 * @author Yamamoto Keita
 *
 */
public class VMSTAT {

	private Map<String, Long> map = new LinkedHashMap<String, Long>();

	/**
	 * テスト用メソッド
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
	 * @return 引数を元に構築したサーバーパフォーマンス情報
	 */
	public static VMSTAT createState(String src) {
		VMSTAT vmstat = new VMSTAT();
		vmstat.parse(src);
		return vmstat;
	}

	/**
	 * @return 現在のサーバーパフォーマンス情報
	 */
	public static VMSTAT currentState() {
		String src = CommandExecuter.exec("vmstat -a -S m");
		return createState(src);
	}

	/**
	 * 空きメモリ量を取得する
	 *
	 * @return 空きメモリ量
	 */
	public long freeMemory() {
		long free = map.get("free");
		long inact = map.get("inact");
		return free + inact;
	}

	/**
	 * スワップサイズを取得する。
	 * @return スワップサイズ
	 */
	public long swapAmount() {
		return map.get("swpd");
	}

	/**
	 * スワップアウトが発生しているか判定する。<br>
	 * メモリからディスクに書き込んだデータ量を見る。
	 *
	 * @return スワップアウトが発生している場合 <code>true</code>
	 */
	public boolean hasSwapOut() {
		long l = map.get("so");
		return 0 < l;
	}

	/**
	 * @return スワップアウトの量
	 */
	public long swapOut() {
		return map.get("so");
	}

	/**
	 * CPU使用率（100 - アイドル率）を取得する
	 * @return CPU使用率
	 */
	public int cpuUsage() {
		long id = map.get("id");
		return 100 - (int)id;
	}

	/**
	 * @return 待機プロセス数
	 */
	public int waitingProcess() {
		return (int) (long) map.get("r");
	}

	/**
	 * @return ブロックされたプロセス数
	 */
	public int blockedProcess() {
		return (int) (long) map.get("b");
	}

	private void parse(String src) {
		BufferedReader r = new BufferedReader(new StringReader(src));

		try {
			r.readLine(); // 1行目はとばす
			String header = r.readLine();
			if (header == null) {
				throw new IllegalArgumentException("行が読み込めませんでした。");
			}
			String[] titles = ParseUtils.splitBySpace(header);
			String body = r.readLine();
			if (body == null) {
				throw new IllegalArgumentException("行が読み込めませんでした。");
			}
			String[] fields = ParseUtils.splitBySpace(body);

			if (titles.length != fields.length) {
				throw new IllegalArgumentException("ヘッダとデータの数が異なります:" + src);
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
