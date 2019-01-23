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
 * HTTP�AHTTPS��GET�܂���POST�ʐM���s���N���X�B
 *
 * @author Yamamoto Keita
 *
 */
public class HttpConnector {
	private HttpHeader header;
	private URL url;
	private boolean isDisableSSLVerify;

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param url
	 * @throws MalformedURLException
	 */
	public HttpConnector(String url) throws MalformedURLException{
		this.url = new URL(url);
	}

	/**
	 * GET��HTTP���X�|���X���擾����B
	 *
	 * @return HTTP���X�|���X
	 * @throws IOException
	 */
	public HttpResponse get() throws IOException{
		return getResponse(false, null);
	}

	/**
	 * POST��HTTP���X�|���X���擾����B
	 *
	 * @param data
	 * @return HTTP���X�|���X
	 * @throws IOException
	 */
	public HttpResponse post(byte[] data) throws IOException{
		return getResponse(true, data);
	}

	/**
	 * SSL�ؖ����̐��������؂̗L��/������؂�ւ���B
	 * �f�t�H���g�ł͌��؂��L���ȏ�ԂɂȂ��Ă���B
	 *
	 * @param flag
	 */
	public void enableSSLVerify(boolean flag) {
		isDisableSSLVerify = !flag;
	}

	/**
	 * �ʐM���Ɏg�p���郊�N�G�X�g�w�b�_�[���Z�b�g����B
	 *
	 * @param header
	 */
	public void setHeader(HttpHeader header) {
		this.header = header;
	}

	/**
	 * GET�܂���POST��HTTP���X�|���X���擾����B
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
			//�ڑ�
			con = (HttpURLConnection) url.openConnection();

			if (https && isDisableSSLVerify) {
				disableSSLCertificationVerify(con);
			}

			//�w�b�_�[�t�^
			con.setRequestMethod(method);
			con.setInstanceFollowRedirects(false);
			setHeaderToConnection(con);

			if (post) {
				con.setDoOutput(true);
				out = con.getOutputStream();
				IOUtils.write(out, data);
			}

			//�ǂݍ���
			header = new HttpHeader(con);
			in = con.getInputStream();
			resdata = IOUtils.read(in);
		} catch (IOException e1) {
			if (con == null) {
				throw e1;
			}
			try {
				//404�Ȃǂ̏ꍇException���������Ă����X�|���X������̂œǂݎ��
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
	 * SSL�ؖ������M�����ꂽ�ؖ������ǂ����̌��؂��s��Ȃ��悤�ɂ���B
	 * �M������Ă��Ȃ��ؖ������������T�C�g��HTTPS�Őڑ��\�ɂȂ�B
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
		} catch (NoSuchAlgorithmException e) {// �z��O
			e.printStackTrace();
		} catch (KeyManagementException e) {// �z��O
			e.printStackTrace();
		}
	}

	/**
	 * SSL�ؖ����ɋL�q���ꂽ�z�X�g���Ǝ��ۂ̃z�X�g�������v���Ă��邩�̌��؂��s��Ȃ��悤�ɂ���B
	 * �ؖ����̃z�X�g�����قȂ��Ă��Ă��AHTTPS�Őڑ��\�ɂȂ�B
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
	 * �R�l�N�V�����Ƀw�b�_�����������ށB
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
