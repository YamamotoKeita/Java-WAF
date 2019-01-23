package jp.co.altonotes.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import jp.co.altonotes.io.IOUtils;

/**
 * HTTP、HTTPSでGETまたはPOST通信を行うクラス。
 *
 * @author Yamamoto Keita
 *
 */
public class HttpConnector {
	private HttpHeader header;
	private URL url;
	private boolean isDisableSSLVerify;

	/**
	 * コンストラクター。
	 *
	 * @param url
	 * @throws MalformedURLException
	 */
	public HttpConnector(String url) throws MalformedURLException{
		this.url = new URL(url);
	}

	/**
	 * GETでHTTPレスポンスを取得する。
	 *
	 * @return HTTPレスポンス
	 * @throws IOException
	 */
	public HttpResponse get() throws IOException{
		return getResponse(false, null);
	}

	/**
	 * POSTでHTTPレスポンスを取得する。
	 *
	 * @param data
	 * @return HTTPレスポンス
	 * @throws IOException
	 */
	public HttpResponse post(byte[] data) throws IOException{
		return getResponse(true, data);
	}

	/**
	 * SSL証明書の正当性検証の有効/無効を切り替える。
	 * デフォルトでは検証が有効な状態になっている。
	 *
	 * @param flag
	 */
	public void enableSSLVerify(boolean flag) {
		isDisableSSLVerify = !flag;
	}

	/**
	 * 通信時に使用するリクエストヘッダーをセットする。
	 *
	 * @param header
	 */
	public void setHeader(HttpHeader header) {
		this.header = header;
	}

	/**
	 * GETまたはPOSTでHTTPレスポンスを取得する。
	 *
	 * @param post
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private HttpResponse getResponse(boolean post, byte[] data) throws IOException{
		HttpURLConnection con = null;
		OutputStream out = null;
		InputStream in = null;
		byte[] resdata = null;
		HttpHeader header = null;
		boolean https = url.getProtocol().startsWith("https");
		String method = post ? "POST" : "GET";

		if (https && isDisableSSLVerify) {
			disableSSLHostnameVerify();
		}

		try {
			//接続
			con = (HttpURLConnection) url.openConnection();

			if (https && isDisableSSLVerify) {
				disableSSLCertificationVerify(con);
			}

			//ヘッダー付与
			con.setRequestMethod(method);
			con.setInstanceFollowRedirects(false);
			setHeaderToConnection(con);

			if (post) {
				con.setDoOutput(true);
				out = con.getOutputStream();
				IOUtils.write(out, data);
			}

			//読み込み
			header = new HttpHeader(con);
			in = con.getInputStream();
			resdata = IOUtils.read(in);
		} catch (IOException e1) {
			if (con == null) {
				throw e1;
			}
			try {
				//404などの場合Exceptionが発生してもレスポンスがあるので読み取り
				InputStream es = con.getErrorStream();
				if (es != null) {
					resdata = IOUtils.read(es);
				}
			} catch (IOException e2) {
				throw e2;
			}
		} finally {
			if (con != null) con.disconnect();
		}

		return new HttpResponse(header, resdata);
	}

	/**
	 * SSL証明書が信頼された証明書かどうかの検証を行わないようにする。
	 * 信頼されていない証明書を持ったサイトにHTTPSで接続可能になる。
	 *
	 * @param con
	 */
	private void disableSSLCertificationVerify(HttpURLConnection con) {
		javax.net.ssl.HttpsURLConnection scon = (javax.net.ssl.HttpsURLConnection) con;

		javax.net.ssl.X509TrustManager tm = new javax.net.ssl.X509TrustManager() {
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {}
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {}
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, new javax.net.ssl.TrustManager[]{tm}, new SecureRandom());
			scon.setSSLSocketFactory(sslcontext.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {// 想定外
			e.printStackTrace();
		} catch (KeyManagementException e) {// 想定外
			e.printStackTrace();
		}
	}

	/**
	 * SSL証明書に記述されたホスト名と実際のホスト名が合致しているかの検証を行わないようにする。
	 * 証明書のホスト名が異なっていても、HTTPSで接続可能になる。
	 *
	 */
	private void disableSSLHostnameVerify() {
		javax.net.ssl.HostnameVerifier hnvfr = new javax.net.ssl.HostnameVerifier() {
			public boolean verify(String host, javax.net.ssl.SSLSession ses) {
				return true;
			}
		};
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hnvfr);
	}

	/**
	 * コネクションにヘッダ情報を書き込む。
	 *
	 * @param con
	 */
	private void setHeaderToConnection(HttpURLConnection con) {
		if (header != null) {
			Entry<String, List<String>>[] entries = header.getEntries();
			List<String> valList = null;
			for (Entry<String, List<String>> entry : entries) {
				valList = entry.getValue();
				for (int i = 0; i < valList.size(); i++) {
					if (i == 0) {
						con.setRequestProperty(entry.getKey(), valList.get(i));
					} else {
						con.addRequestProperty(entry.getKey(), valList.get(i));
					}
				}
			}
		}
	}

}
