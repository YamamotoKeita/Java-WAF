package jp.co.altonotes.webapp.property;

/**
 * プロパティアクセスの結果を保持する。
 * パフォーマンスのため、メソッド内で生成するのではなく、引数に渡す。
 * 
 * @author Yamamoto Keita
 *
 */
final class Result {

	/** メッセージ */
	protected String message;
	
	/** エラーフラグ */
	protected boolean isFailed;

	/**
	 * コンストラクター
	 */
	protected Result() {}

	/**
	 * アクセス処理失敗の際に呼び出す。
	 * @param message
	 */
	protected void fail(String message) {
		this.message = message;
		isFailed = true;
	}
	
	/**
	 * @return メッセージがある場合<code>true</code>
	 */
	protected boolean hasMessage() {
		return message != null;
	}


	@Override
	public String toString() {
		return isFailed ? "FAILED" : "SUCCESS" + " : " + message;
	}

	/**
	 * 保持する結果をクリアする
	 */
	public void clear() {
		message = null;
		isFailed = false;
	}

}
