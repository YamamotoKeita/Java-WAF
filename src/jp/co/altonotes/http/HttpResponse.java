package jp.co.altonotes.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.zip.CompressionUtils;

/**
 * HttpのResponseを表すクラス。
 * ヘッダとボディの２つで構成される。
 *
 * @author Yamamoto Keita
 *
 */
public class HttpResponse {

	private HttpHeader header;
	private byte[] body;

	/**
	 * コンストラクタ。
	 *
	 * @param header レスポンスのヘッダ
	 * @param body レスポンスのボディ
	 */
	public HttpResponse(HttpHeader header, byte[] body) {
		this.header = header;
		this.body = body;
	}

	/**
	 * ヘッダのContent-Typeを見て、このレスポンスがHTMLか判定する。
	 *
	 * @return このレスポンスがHTMLの場合true
	 */
	public boolean isHtml() {
		return header.isHtml();
	}

	/**
	 * ヘッダのContent-Typeを見て、このレスポンスの文字コードを取得する。
	 *
	 * @return このレスポンスの文字コード
	 */
	public String getCharset() {
		return header.getCharset();
	}

	/**
	 * ヘッダのContent-Encodingを見て、このレスポンスデータの圧縮方式を取得する。
	 *
	 * @return Content-Encodingの値
	 */
	public String getContentEncoding() {
		return header.getContentEncoding();
	}

	/**
	 * このレスポンスのHttpHeaderを取り出す。
	 *
	 * @return このレスポンスのヘッダを表すHttpHeaderインスタンス
	 */
	public HttpHeader getHeader() {
		return header;
	}

	/**
	 * このレスポンスのオリジナルのボディデータを取得する。
	 * 圧縮されていた場合も解凍していないため注意が必要。
	 *
	 * @return このレスポンスのボディのバイナリデータ
	 */
	public byte[] getRawBody() {
		return body;
	}

	/**
	 * このレスポンスのボディデータを取得する。
	 * ヘッダのContent-Encodingに圧縮方式が記されていた場合、
	 * それが既知の方式であれば解凍したデータを返す。
	 *
	 * @return 解凍されたレスポンスデータ
	 */
	public byte[] getDecodedBody() {
		String encoding = getContentEncoding();
		if (encoding == null) {
			return body;
		}
		if (encoding.equalsIgnoreCase("gzip")) {
			return CompressionUtils.decodeGZIP(body);
		} else if (encoding.equalsIgnoreCase("deflate")) {
			return CompressionUtils.decodeDeflate(body);
		} else if (encoding.equalsIgnoreCase("compress")) {
			//TODO compressの解凍を実装する
			System.out.println("[警告]compress圧縮データの解凍は未実装です。");
			return body;
		} else {
			System.out.println("[警告]" + encoding + "は未知の圧縮方式のため解凍できません。");
			return body;
		}
	}

	/**
	 * このレスポンスのボディデータを、Content-Encodingの圧縮方式、Content-Typeの文字コードを適用して文字列として返す。
	 * Content-Typeにcharsetが無い場合、javaデフォルト文字コードで文字列化するため、正しい内容である保証は無い。
	 *
	 * @return このレスポンスのボディ文字列
	 */
	public String getBodyString() {
		String res = null;
		String charset = getCharset();
		if (charset == null) {
			res = new String(getDecodedBody());
		} else {
			try {
				res = new String(getDecodedBody(), charset);
			} catch (UnsupportedEncodingException ignored) {
				res = new String(getDecodedBody());
			}
		}
		return res;
	}

	/**
	 * Set-Cookie, Set-Cookie2の値をString配列として取得する。
	 *
	 * @return Cookieの値の配列
	 */
	public String[] getCookieValues() {
		return header.getResponseCookies();
	}

	/**
	 * ボディデータをセットする
	 *
	 * @param body
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * ヘッダーの値をシステムに出力する
	 */
	public void printHeader() {
		header.print();
	}

	/**
	 * HTTPヘッダに記述されたLocation（リダイレクト先URL）の値を取り出す。
	 *
	 * @return Location（リダイレクト先URL）の値
	 */
	public String getLocationURL() {
		return header.getParameter(HeaderNames.LOCATION);
	}

	/**
	 * HttpServletResponse にこのインスタンスが保持するレスポンスデータを出力する。
	 *
	 * @param resp
	 * @throws IOException
	 */
	public void write(HttpServletResponse resp) throws IOException {
		header.write(resp);
		IOUtils.write(resp, body);
	}

}
