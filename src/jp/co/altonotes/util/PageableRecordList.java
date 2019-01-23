package jp.co.altonotes.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ページング可能なレコードリスト。
 *
 * @author Yamamoto Keita
 * @param <RecordType> 
 *
 */
public class PageableRecordList <RecordType> {

	/** 全レコード		*/
	private RecordType[] allRecords;

	/** 全レコードの数	*/
	private int recordCount = 0;

	/** 現在のページ番号	*/
	private int currentPageNumber = 1;

	/** 末尾のページ番号	*/
	private int maxPageNumber = 0;

	/** 1ページに表示するレコードの最大数	*/
	private int maxRecordOfaPage = 0;

	/** 表示するページアンカー（各ページへジャンプするためのリンク）の最大数	*/
	private int maxAnchorCount = 5;

	/**
	 * コンストラクタ。
	 *
	 * @param maxRecordOfaPage  1ページに表示するレコードの最大数
	 * @param allRecords 保持する全レコード
	 */
	public PageableRecordList(int maxRecordOfaPage, RecordType[] allRecords) {
		this.allRecords = allRecords;
		this.maxRecordOfaPage = maxRecordOfaPage;
		this.recordCount = allRecords.length;

		if (recordCount == 0) {
			maxPageNumber = 1;
		} else if ((recordCount % maxRecordOfaPage) == 0) {
			maxPageNumber = allRecords.length / maxRecordOfaPage;
		} else {
			maxPageNumber = (allRecords.length / maxRecordOfaPage) + 1;
		}
	}

	/**
	 * コンストラクタ。
	 *
	 * @param maxRecordOfaPage  1ページに表示するレコードの最大数
	 * @param maxAnchorCount
	 * @param allRecords 保持する全レコード
	 */
	public PageableRecordList(int maxRecordOfaPage, int maxAnchorCount, RecordType[] allRecords) {
		this.allRecords = allRecords;
		this.maxRecordOfaPage = maxRecordOfaPage;
		this.recordCount = allRecords.length;
		this.maxAnchorCount = maxAnchorCount;

		if (recordCount == 0) {
			maxPageNumber = 1;
		} else if ((recordCount % maxRecordOfaPage) == 0) {
			maxPageNumber = allRecords.length / maxRecordOfaPage;
		} else {
			maxPageNumber = (allRecords.length / maxRecordOfaPage) + 1;
		}
	}

	/**
	 * 現在のページが保持するレコードを取得する。
	 *
	 * @return 現在のページが保持するレコード配列
	 */
	public List<RecordType> getCurrentRecords() {
		ArrayList<RecordType> result = new ArrayList<RecordType>();

		int startIndex = maxRecordOfaPage * (currentPageNumber - 1);

		if (currentPageNumber == maxPageNumber) {
			for (int i = 0; (startIndex + i) < recordCount; i++) {
				result.add(allRecords[startIndex + i]);
			}
		} else {
			for (int i = 0; i < maxRecordOfaPage; i++) {
				result.add(allRecords[startIndex + i]);
			}
		}
		return result;
	}

	/**
	 * 指定したページに移動する。
	 * 0以下のページ番号を指定した場合、1ページへ移動。
	 * 最大ページより大きなページ番号を指定した場合、最大ページへ移動する。
	 *
	 * @param pageNumber
	 * @return 指定したページ番号が存在しない場合false
	 */
	public boolean movePage(int pageNumber) {
		currentPageNumber = pageNumber;
		if (currentPageNumber < 1) {
			currentPageNumber = 1;
			return false;
		} else if (currentPageNumber > maxPageNumber) {
			currentPageNumber = maxPageNumber;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 次のページへ移動する。
	 *
	 * @return 次のページが存在せず移動できない場合false、移動できた場合はtrue。
	 */
	public boolean nextPage() {
		if (hasMoreRightPage()) {
			currentPageNumber++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 一つ前のページへ移動する。
	 *
	 * @return 一つ前のページが存在せず移動できない場合false、移動できた場合はtrue。
	 */
	public boolean previousPage() {
		if (hasMoreLeftPage()) {
			currentPageNumber--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 表示するページアンカーの左端の番号を取得する。
	 *
	 * @return 表示するページアンカーの左端の番号。
	 */
	public int getFirstAnchorNumber() {
		//表示アンカー数が奇数の場合、現在ページは「(表示数/2) + 1」番目の位置に、偶数の場合は「表示数/2」番目の位置にくる
		int num = currentPageNumber - (maxAnchorCount / 2);
		if (num > maxPageNumber - maxAnchorCount + 1) {
			num = maxPageNumber - maxAnchorCount + 1;
		}
		if (num < 1) {
			num = 1;
		}
		return num;
	}

	/**
	 * 表示するページアンカーの右端の番号を取得する。
	 *
	 * @return 表示するページアンカーの右端の番号。
	 */
	public int getLastAnchorNumber() {
		//表示アンカー数が奇数の場合、現在ページは「(表示数/2) + 1」番目の位置に、偶数の場合は「表示数/2」番目の位置にくる
		int num = currentPageNumber + ((maxAnchorCount - 1) / 2);
		if (num < maxAnchorCount) {
			num = maxAnchorCount;
		}
		if (num > maxPageNumber) {
			num = maxPageNumber;
		}
		return num;
	}

	/**
	 * 現在のページ番号を取得する。
	 * ページ番号は1より始まる。
	 *
	 * @return 現在のページ番号
	 */
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	/**
	 * 一つ前のページ番号を返す。
	 *
	 * @return 一つ前のページ番号
	 */
	public int getPreviousPageNumber() {
		int i = currentPageNumber - 1;
		if (i < 1) {
			return 1;
		} else {
			return i;
		}
	}

	/**
	 * 一つ後ろのページ番号を返す。
	 *
	 * @return 一つ後ろのページ番号
	 */
	public int getNextPageNumber() {
		int i = currentPageNumber + 1;
		if (maxPageNumber < i) {
			return maxPageNumber;
		} else {
			return i;
		}
	}

	/**
	 * 現在のページより左のページが存在する場合trueを返す。
	 *
	 * @return 現在のページより左のページが存在する場合 true
	 */
	public boolean hasMoreLeftPage() {
		return currentPageNumber > 1;
	}

	/**
	 * 現在のページより右のページが存在する場合trueを返す。
	 *
	 * @return 現在のページより右のページが存在する場合 true
	 */
	public boolean hasMoreRightPage() {
		return currentPageNumber < maxPageNumber;
	}

	/**
	 * @return １ページ内の最大レコード数
	 */
	public int getMaxRecordOfaPage() {
		return maxRecordOfaPage;
	}

	/**
	 * １ページ内の最大レコード数をセットする
	 * @param maxRecordOfaPage
	 */
	public void setMaxRecordOfaPage(int maxRecordOfaPage) {
		this.maxRecordOfaPage = maxRecordOfaPage;
	}

	/**
	 * @return ページアンカー（各ページへジャンプするためのリンク）の最大数
	 */
	public int getMaxAnchorCount() {
		return maxAnchorCount;
	}

	/**
	 * ページアンカー（各ページへジャンプするためのリンク）の最大数をセットする
	 * @param maxAnchorCount
	 */
	public void setMaxAnchorCount(int maxAnchorCount) {
		this.maxAnchorCount = maxAnchorCount;
	}

	/**
	 * @return レコード数
	 */
	public int getRecordCount() {
		return recordCount;
	}

}
