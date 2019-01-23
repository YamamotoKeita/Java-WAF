package jp.co.altonotes.csv;

/**
 * CSVの１行に含まれるそれぞれの項目を表すクラス。
 *
 * @author Yamamoto Keita
 */
public class CSVElement {
	private String source;

	/**
	 * コンストラクタ。
	 *
	 * @param str
	 * @param enquote
	 */
	protected CSVElement(String str) {
		source = str;
	}

	/**
	 * 必要な場合はエンクォートして文字列を返す。
	 *
	 * @return 適宜エンクォートされた文字列
	 */
	public String getCSVString() {
		return enquote(source, false);
	}

	/**
	 * 強制的にエンクォートした文字列を返す。
	 *
	 * @return エンクォートされた文字列
	 */
	public String getEnquotedString() {
		return enquote(source, true);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return source;
	}

	/**
	 * 引数の文字列を CSV で出力できる形に変換する。<br>
	 * 文字列が " か , を含んでいるときには、" を "" に置き換え、文字列全体を " で囲む。
	 *
	 * forceEnquote が true の場合、 " か , を含まない場合でも強制的に " で文字列を囲む。
	 *
	 * @param str 処理したい文字列
	 * @param forceEnquote trueなら強制的にエンクォートする
	 * @return item を処理した文字列
	 */
	private static String enquote(String str, boolean forceEnquote) {
		if (str.length() == 0) {
			return str;
		}
		if (str.indexOf('"') < 0 && str.indexOf(',') < 0 && forceEnquote == false) {
			return str;
        }

		// 全ての文字を " から "" に変換する場合の長さを想定
		StringBuffer sb = new StringBuffer(str.length() * 2 + 2);
		sb.append('"');
		sb.append(str.replaceAll("\"", "\"\""));
		sb.append('"');

		return new String(sb);
	}

}

